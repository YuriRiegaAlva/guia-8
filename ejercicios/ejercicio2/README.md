# ejercicio2

A Java learning project created with [javetas](https://github.com/YuriRiegaAlva/javetas).

## Layout

```
ejercicio2/
├── javetas.toml  # project config & dependencies
├── Makefile      # build / run / clean shortcuts
├── .gitignore    # files git should ignore
└── src/          # your .java source files
```

## Requirements

- JDK 25 or newer. Check with `java -version` and `javac -version`.

## Compile and run

With the Makefile (Linux/macOS):

```sh
make          # compile
make run      # compile and run Main
make clean    # delete compiled classes
```

Or with javetas, from anywhere inside this project:

```sh
javetas build
javetas run
javetas run SomeOtherClass   # run a different class
```

## How it works (the learning part)

1. `make build` runs `javac -d out <all .java files under src/>`.
   `javac` compiles your source into `.class` bytecode inside `out/`.
2. `make run` runs `java -cp out Main`.
   `java` starts the JVM and runs the `main` method of that class.
3. `-cp out` is the *classpath*: it tells Java where to look for
   compiled classes.

Try adding a second class and using it from Main:

```sh
javetas add Persona
```

Then edit `src/**/Main.java` to create a `Persona` and call its methods.
