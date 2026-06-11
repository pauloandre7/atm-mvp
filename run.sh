#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")"
mvn clean package
java -jar target/atm-mvp-1.0-SNAPSHOT.jar
