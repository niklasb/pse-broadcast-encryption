# Building

Building the Java components is as simple as doing

    $ mvn clean install
    $ pushd modules/server
    $ mvn package appassembler:assemble
    $ popd

Or using the shell/batch script `tools/build-all[.cmd]`, which 
does exactly this.

## Building the native components

There are several prerequisites:
* standard GNU build utilities (package `build-essential` on Debian)
* recent GCC (must support C++11)
* GMP (can be installed through the package manager on 
       most Linux distributions, the package is called 
       `libgmp-dev` on Debian-based systems)
* javah (part of the JDK)

### Building flint

Just follow the standard Linux building procedure, but don't forget
to specify the `--reentrant` flag when configuring the library:

    $ mkdir /tmp/flint; cd /tmp/flint
    $ wget http://flintlib.org/flint-2.3.tar.gz
    $ tar xzf flint-2.3.tar.gz
    $ cd flint-2.3
    $ ./configure --prefix=/usr/local --reentrant
    $ make
    $ sudo make install

### Building the CryptoCast native components

First you need to build the Java components as described above.
Then you can build the C++ components:

    $ cd native
    $ make headers all

The libraries will be placed into native/lib. Now you can use the Java option
`-Djava.library.path=/path/to/cryptocast/native/lib` if you want to start
a program that makes use of these libraries. The starter scripts
`bin/server` and `bin/benchmark` already specifiy this flag.

# Running

Running the server (will use the native components if possible):

    $ bin/server

on Windows:

    $ bin\server.bat

## Deploying the client app

    $ pushd modules/client
    $ mvn android:deploy
    $ popd
