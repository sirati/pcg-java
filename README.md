***

# Master practical: Cryptography - SoSe2024 - Ludwig-Maximilians-Universität in Munich

# Main project - Group F

The aim of this project was to implement some PCG from their family (https://www.pcg-random.org/) in Java and in
addition
try to improve the Java implementation regarding performance and intercompability within the Java ecosystem.

#### List of implemented PCG's:

* PCG-XSH-RR (32bit/2^64 period)
* PCG-XSH-RS (32bit/2^64 period)
* PCG_XSL_RR (64bit output/2^128 period)
* PCG_RXS_M_XS_32 (32bit) (insecure)
* PCG_RXS_M_XS_64 (64bit) (insecure)

For comparison with the results of the paper we forked the initial C library and edited the code to create test example
for our Java implementation. This code can be found under: https://github.com/TheNand25/pcg-c

#### List of implemented performance and statistical tests:

* TestU01 library: SmallCrush, Crush and BigCrush battery
* Linear complexity of PCGs
* Vectorization benchmarks

#### Additional automatic tests:

* Comparison with the papers C-Code results
* Test of seekability by skip methode
* Testing of recognising JDK features and enabled previews/incubators to load correct extensions automatically

This is all what is contributed to the end of the educational project. Further work and contribution might follow.

## Installation

For sanity reasons best use IntelliJ IDEA by JetBrains.
IntelliJ will install and update the gradle project straight after checkout.

Else start the ```./gradlew clean jar``` build Process in Terminal after checkout. This will build the project to be
used as a library in another application. It might be, that you need to update your JDK to v21 on your own. Some tests,
especially BigCrush, of the project will only run on v21 excluding newer versions.

## Usage

1. Create one of the available pcg's with creator service either in Java Primitive Variant or with Java vectorizing if
   using Java21 preview features. e.g.:

    ```java
    var builder = new PCGBuilder<>().type(de.edu.lmu.pcg.PCG_XSH_RS.class).seed(42L);
    
    var pcgManual = builder.preferred_variant(JavaPrimitive).build();
    var pcgVector = builder.preferred_variant(JavaVectoring).build();
    //in case the vectoring variant it was loaded it will be build instead
    ```
   without specifying a preferred_variant JavaVectoring will be chosen over JavaPrimitive

in general to use Vectoring you need to run with java21 with ```--enable-preview --add-modules=jdk.incubator.vector```,
otherwise the bundled extension will not be loaded.

Call ```nextInt()``` or ```nextLong()``` on the pcg instance depending on what type of pcg you built. alternative there
are the neutral methods `fill(ByteBuffer buf)` or `fillOnceInto(int[] arr, int start, int max);`

2. For example output on all 5 implemented pcg's run Main.java in ./src/main/java/de/edu/lmu/pcg

## Testing

### Running Crush

1. you need to install [libtestu01.0-devel](https://packages.debian.org/sid/libtestu01-0-dev) (for Linux, or else
   download from the [testU01 homepage](https://simul.iro.umontreal.ca/testu01/tu01.html) and figure out yourself how to
   link the rust project with it)
2. have rust & jdk21 installed
3. run ```cargo build --package pcg_test_crush --lib``` (this needs to be debug build!)
4. run ```./gradlew crushTest  --info```

### Running Other statistical Tests and comparative Verification

For the linear complexity test, comparison with in C-Project generated random numbers and test of the skip methode:
run ```./gradlew test --info``` will automatically perform all the below and all currently runtime available PCGs

Go to ```src/test/java/de/edu/lmu/pcg/test```. Here is the file to...

1. ...run the linear complexity test. ```LinearCompTest.java```.


2. ...run the comparison with in C generated random numbers. ```Test.java```.


3. ...run the linear complexity test. ```PCGUtilTest.java```.

### Benchmarking

Run ```./gradlew jmh```

This currently tests PCG_XSH_RS's vectoring vs non-vectoring implementation,
and PCG_RXS_M_XS_32 using two different benchmarking methods.

## Authors and acknowledgment

Authors: sirati, Ferdinand Schlosser, Louisa Sommer and Clarissa Kümhof

Thanks to the lectureres Lydia Kondylidou and Tanguy Bozec!
