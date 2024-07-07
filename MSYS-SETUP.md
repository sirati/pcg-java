# Installation and Compilation Guide for TestU01-1.2.3

0. **Download and Extract TestU01-1.2.3**

1. **Install MSYS2 MSYS Shell**

2. **Open MSYS2 MSYS Shell (not the MINGW64 Shell) and Update/Install All Required Libraries**
    ```sh
    pacman -Syu
    pacman -S mingw-w64-x86_64-gcc mingw-w64-x86_64-make
    pacman -S autoconf automake
    pacman -S make
    ```

    Check for successful installation with:
    ```sh
    which gcc
    which make
    ```

3. **Set Environment Variable**
    Add `C:\msys64\mingw64\bin` to `PATH`.

4. **Install TestU01-1.2.3**
    ```sh
    cd /path/to/TestU01-1.2.3

    mkdir -p /path/to/TestU01-1.2.3/installation

    ./configure --disable-shared --disable-dependency-tracking --prefix=/TestU01-1.2.3/installation
    ```

5. **Set Session Environment Variables**
    ```sh
    export LD_LIBRARY_PATH=/c/path/to/TestU01-1.2.3/installation/lib:$LD_LIBRARY_PATH
    export LIBRARY_PATH=/c/path/to/TestU01-1.2.3/installation/lib:$LIBRARY_PATH
    export C_INCLUDE_PATH=/c/path/to/TestU01-1.2.3/installation/include:$C_INCLUDE_PATH
    ```

6. **Navigate to Code and Compile**
    ```sh
    cd /c/Path/To/Code/

    gcc example.c -o example -ltestu01 -lprobdist -lmylib -lm
    ```

7. **Run Program**
    ```sh
    ./example.exe
    ```

    Redirect console output to file if necessary:
    ```sh
    ./example.exe >> output.txt
    ```
