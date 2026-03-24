#!/bin/bash

# Crear directorio para clases compiladas
mkdir -p target/classes

# Compilar
javac -cp ".:target/classes" -d target/classes \
  src/main/java/com/hconex/*.java \
  src/main/java/com/hconex/config/*.java \
  src/main/java/com/hconex/core/packets/*.java \
  src/main/java/com/hconex/core/proxy/*.java \
  src/main/java/com/hconex/core/protocol/*.java \
  src/main/java/com/hconex/logging/*.java \
  src/main/java/com/hconex/utils/*.java 2>&1

# Ejecutar
java -cp target/classes com.hconex.Application
