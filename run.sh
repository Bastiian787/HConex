#!/usr/bin/env bash
set -e

BLUE="\033[0;34m"
GREEN="\033[0;32m"
RED="\033[0;31m"
RESET="\033[0m"

echo -e "${BLUE}[INFO] Compilando HConex (módulo raíz)...${RESET}"
if mvn -q clean compile; then
  echo -e "${GREEN}[SUCCESS] Compilación OK${RESET}"
else
  echo -e "${RED}[ERROR] Falló la compilación${RESET}"
  exit 1
fi

echo -e "${BLUE}[INFO] Ejecutando com.hconex.Application...${RESET}"
mvn -q exec:java -Dexec.mainClass=com.hconex.Application
