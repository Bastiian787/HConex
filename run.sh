<<<<<<< HEAD
#!/bin/bash
=======
#!/usr/bin/env bash
set -e
>>>>>>> 5ed2b83 (Unify root module entrypoint and run script)

BLUE="\033[0;34m"
GREEN="\033[0;32m"
RED="\033[0;31m"
RESET="\033[0m"

<<<<<<< HEAD
echo -e "${BLUE}[INFO] Preparando compilación...${RESET}"
mkdir -p target/classes

SOURCES=(
  "src/main/java/com/hconex/Application.java"
  "src/main/java/com/hconex/config/HabboConfig.java"
  "src/main/java/com/hconex/core/packets/Packet.java"
  "src/main/java/com/hconex/core/proxy/ProxyServer.java"
  "src/main/java/com/hconex/core/proxy/ProxyHandler.java"
  "src/main/java/com/hconex/core/proxy/ConnectionManager.java"
  "src/main/java/com/hconex/core/protocol/HabboProtocol.java"
  "src/main/java/com/hconex/core/protocol/PacketFactory.java"
  "src/main/java/com/hconex/core/protocol/PacketEncoder.java"
  "src/main/java/com/hconex/logging/LogEntry.java"
  "src/main/java/com/hconex/logging/PacketLogger.java"
  "src/main/java/com/hconex/ui/ConsoleUI.java"
  "src/main/java/com/hconex/utils/HexUtils.java"
  "src/main/java/com/hconex/utils/ByteUtils.java"
)

echo -e "${BLUE}[INFO] Compilando archivos Java...${RESET}"
if javac -d target/classes "${SOURCES[@]}"; then
  echo -e "${GREEN}[SUCCESS] Compilación completada correctamente.${RESET}"
  echo -e "${BLUE}[INFO] Ejecutando aplicación...${RESET}"
  java -cp target/classes com.hconex.Application
else
  echo -e "${RED}[ERROR] La compilación falló. Abortando ejecución.${RESET}"
  exit 1
fi
=======
echo -e "${BLUE}[INFO] Compilando HConex (módulo raíz)...${RESET}"
if mvn -q clean compile; then
  echo -e "${GREEN}[SUCCESS] Compilación OK${RESET}"
else
  echo -e "${RED}[ERROR] Falló la compilación${RESET}"
  exit 1
fi

echo -e "${BLUE}[INFO] Ejecutando com.hconex.Application...${RESET}"
mvn -q exec:java -Dexec.mainClass=com.hconex.Application
>>>>>>> 5ed2b83 (Unify root module entrypoint and run script)
