#!/bin/bash
# Maven Wrapper Script
# Wrapper script to run Maven commands in the project

# Check if Maven is available, if not, download it
MVN_VERSION="3.9.6"
MVN_URL="https://archive.apache.org/dist/maven/maven-3/${MVN_VERSION}/binaries/apache-maven-${MVN_VERSION}-bin.tar.gz"
MVN_DIR="${HOME}/.m2/wrapper/dists/apache-maven-${MVN_VERSION}"
MVN_EXECUTABLE="${MVN_DIR}/bin/mvn"

# Create Maven directory if it doesn't exist
if [ ! -d "${MVN_DIR}" ]; then
    echo "Downloading Maven ${MVN_VERSION}..."
    mkdir -p "${MVN_DIR}"
    curl -L "${MVN_URL}" | tar xz -C "${HOME}/.m2/wrapper/dists/"
    mv "${HOME}/.m2/wrapper/dists/apache-maven-${MVN_VERSION}" "${MVN_DIR}"
fi

# Execute Maven with all provided arguments
exec "${MVN_EXECUTABLE}" "$@"