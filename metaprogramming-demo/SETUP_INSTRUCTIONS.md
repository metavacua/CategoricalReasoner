# Setup Instructions for Java Metaprogramming Demo

## Environment Setup

This project requires Java 21 and Maven 3.8+. On Ubuntu/Debian systems:

```bash
# Install Java 21
sudo apt-get update
sudo apt-get install -y openjdk-21-jdk

# Download and extract Maven
cd /tmp
curl -LO https://archive.apache.org/dist/maven/maven-3/3.8.7/binaries/apache-maven-3.8.7-bin.tar.gz
tar -xzf apache-maven-3.8.7-bin.tar.gz

# Set environment variables
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH="/tmp/apache-maven-3.8.7/bin:$PATH"
```

## Build and Run

```bash
cd metaprogramming-demo

# Compile
mvn clean compile

# Run tests
mvn test

# Run the demonstration
mvn exec:java -Dexec.mainClass="org.catty.metaprogramming.Main"
```

All tests pass successfully (7/7).