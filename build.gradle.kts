plugins {
    id("java")
}

group = "de.edu.lmu"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


dependencies {
    implementation(files("libs/ssj-3.3.1.jar"))
    implementation("ca.umontreal.iro.simul:ssj:3.3.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

/*tasks.register<JavaExec>("runLinearComp") {
    group = "application"
    mainClass.set("de.edu.lmu.LinearComp")
    classpath = sourceSets["main"].runtimeClasspath
}*/