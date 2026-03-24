#!/bin/bash

set -e

mkdir -p target/META-INF

cat > target/META-INF/MANIFEST.MF << 'EOF'
Manifest-Version: 1.0
Main-Class: com.hconex.Application
Created-By: HConex Build System
Implementation-Version: 0.0.1
EOF

cd target/classes
jar cvfm ../HConex.jar ../META-INF/MANIFEST.MF com/
cd ../..

cp target/HConex.jar .

echo "✓ JAR created: HConex.jar"
