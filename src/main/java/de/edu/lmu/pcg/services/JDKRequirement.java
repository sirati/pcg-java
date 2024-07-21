package de.edu.lmu.pcg.services;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

public class JDKRequirement {

    static ClassLoader load_version_specific() throws URISyntaxException, IOException {
        var currentJDK = Requirement.fromCurrentJDK();
        String versionSpecificPath = "libs/version_specific/jvm/";

        List<URL> jarUrls = new ArrayList<>();

        var folders = PCGCtorService.class.getProtectionDomain().getClassLoader().getResources(versionSpecificPath).asIterator();
        var perJar = new HashMap<String, Set<String>>();

        for (Iterator<URL> it = folders; it.hasNext(); ) {
            var url = it.next();
            if (url.getProtocol() == "jar") {
                var jarSeparator = url.getPath().indexOf("!/");
                perJar.computeIfAbsent(url.getPath().substring(5, jarSeparator), k -> new HashSet<>())
                        .add(url.getPath().substring(jarSeparator + 2));
            } else {
                var folder = new File(url.toURI());
                if (folder.isDirectory()) {
                    File[] jarFiles = folder.listFiles((dir, name) -> name.endsWith(".jar"));
                    if (jarFiles != null) {
                        for (File jarFile : jarFiles) {
                            if (!verifyJar(new FileInputStream(jarFile), currentJDK)) continue; //if we failed we skip this jar
                            jarUrls.add(jarFile.toURI().toURL());
                        }
                    } else {
                        throw new RuntimeException("No jar files found in " + folder + " this indicated an IO error or a bug here!");
                    }
                }
            }
        }
        for (var entry : perJar.entrySet()) {
            try (JarFile jarFile = new JarFile(Paths.get(new URI("file:" + entry.getKey())).toFile())) {
                jarFile.stream().filter(jarEntry -> entry.getValue().stream().anyMatch(path -> jarEntry.getName().startsWith(path)))
                        .filter(jarEntry -> {
                            try {
                                return verifyJar(jarFile.getInputStream(jarEntry), currentJDK);
                            } catch (IOException e) {
                                return false;
                            }
                        })
                        .map(jarEntry -> {
                            try {
                                //extract the jar entry to a temp file
                                File tempFile = File.createTempFile("pcg-inner-jar" , new File(jarEntry.getName()).getName());
                                tempFile.deleteOnExit();
                                try (var is = jarFile.getInputStream(jarEntry);
                                     var os = new FileOutputStream(tempFile)) {
                                    is.transferTo(os);
                                }

                                return tempFile.toURI().toURL();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .forEach(jarUrls::add);
            }
        }


        if (!jarUrls.isEmpty()) {
            URL[] urls = jarUrls.toArray(new URL[0]);
            return new URLClassLoader(urls, PCGCtorService.class.getClassLoader());
        } else {
            return PCGCtorService.class.getClassLoader();
        }
    }

    private static boolean verifyJar(InputStream jarFile, Requirement currentJDK) {
        try (var is = new JarInputStream(jarFile)) {
            //we need to skip till the META-INF/pcg-jdk-requirements.txt entry
            JarEntry entry;
            for (entry=is.getNextJarEntry(); entry!=null; entry=is.getNextJarEntry()) {
                if (entry.getName().equals("META-INF/pcg-jdk-requirements.xml")) {
                    break;
                }
            }
            if (entry == null) {
                return false; //we did not find the file
            }

            //now we seeked the file we are interested in
            if (parseRequirements(is).stream().anyMatch(currentJDK::fulfills)) {
                return true;
            }

        } catch (IOException e) {
            return false;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public record Requirement(int javaVersionMajor, boolean preview, Set<String> modules) {

        public static final String ADD_MODULES_ARG = "--add-modules=";

        public boolean fulfills(Requirement other) {
            return javaVersionMajor == other.javaVersionMajor &&
                    (!other.preview || preview) && modules.containsAll(other.modules);
        }
        public static Requirement fromCurrentJDK() {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            List<String> jvmArguments = runtimeMXBean.getInputArguments();
            String javaVersion = System.getProperty("java.version");
            String majorVersion = javaVersion.split("\\.")[0];


            boolean preview = jvmArguments.stream().anyMatch(arg -> arg.startsWith("--enable-preview"));

            /*//this is supposed to be the correct way, but it does not work at all
            Set<String> modules = Set.of(System.getProperty("jdk.module.path")
                    .replaceAll("\\s", "")
                    .split(File.pathSeparator));*/
            Set<String> modules = jvmArguments.stream()
                    .filter(arg -> arg.startsWith(ADD_MODULES_ARG))
                    .map(arg -> arg.substring(ADD_MODULES_ARG.length()))
                    .collect(Collectors.toSet());
            return new Requirement(Integer.parseInt(majorVersion), preview, modules);
        }


        @Override
            public String toString() {
                return "Requirement{" +
                        "javaVersionMajor=" + javaVersionMajor +
                        ", preview=" + preview +
                        ", modules=" + modules +
                        '}';
            }
        }

    public static List<Requirement> parseRequirements(InputStream is) throws ParserConfigurationException, IOException, SAXException {
        List<Requirement> requirements = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(is);

        NodeList nodeList = document.getElementsByTagName("requirement");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i);

            int javaVersion = Integer.parseInt(element.getElementsByTagName("javaVersionMajor").item(0).getTextContent());
            boolean needsPreview = Boolean.parseBoolean(element.getElementsByTagName("needsPreview").item(0).getTextContent());

            NodeList modulesList = element.getElementsByTagName("module");
            var requiredModules = new HashSet<String>();
            for (int j = 0; j < modulesList.getLength(); j++) {
                requiredModules.add(modulesList.item(j).getTextContent());
            }

            requirements.add(new Requirement(javaVersion, needsPreview, requiredModules));
        }

        return requirements;
    }

    public static String documentToString(Document doc) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("indent", "yes");

        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);

        transformer.transform(source, result);

        return writer.toString();
    }


}
