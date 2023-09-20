# antlr-langium-transpiler

With this project, you can transpile ANTLR4 grammars (`*.g4`) to Langium grammars (`*.langium`).

## Build

```sh
mvn clean compile assembly:single
```

## Use

```sh
java -jar target/antlr-langium-transpiler-1.0-SNAPSHOT-jar-with-dependencies.jar antlr4-grammar-file.g4 langium-grammar-folder/
```

## Test

```sh
mvn test
```