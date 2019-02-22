[![CircleCI](https://circleci.com/gh/vdtn359/change-log-generator-gui.svg?style=svg)](https://circleci.com/gh/vdtn359/change-log-generator-gui)
[![codecov](https://codecov.io/gh/vdtn359/change-log-generator-gui/branch/master/graph/badge.svg)](https://codecov.io/gh/vdtn359/change-log-generator-gui)
![Maven Central](https://img.shields.io/maven-central/v/org.apache.maven/apache-maven.svg)


A tool to generate Liquibase changelogs
# GUI app

## Manual Build
**On Unix**
```bash
./mvnw clean install
./mvnw -pl change-log-generator-gui jfx:native
```
**On Window**
```bash
./mvnw.cmd clean install
./mvnw -pl change-log-generator-gui jfx:native
```
## Binaries
https://www.dropbox.com/sh/ahrbnvcufvi1n26/AAAWI2pGbi4TCbnl7W2z_9Jxa?dl=0

## Release a new version
```
./release.sh [version]
```

# CLI tool:
## Manual build

**On Unix**
```bash
./mvnw clean install
```
**On Window**
```bash
./mvnw.cmd clean install
```

It will generate 2 jar files in the target folder: <br/>
`changelog-generator-1.0-SNAPSHOT.jar` and `changelog-generator-1.0-SNAPSHOT-jar-with-dependencies.jar`

changelog-generator-1.0-SNAPSHOT-jar-with-dependencies.jar is a fat jar and it will contain all dependencies.

**Check the java version**

```bash
java -version
```
Make sure you have java 1.8. 

**Use it**

```bash
java -jar changelog-generator-1.0-SNAPSHOT-jar-with-dependencies.jar [command]
```

## Wrapper 
You can use wrapper instead of manually building the jar file.

**Download the wrapper to your vbox**

```bash
curl https://raw.githubusercontent.com/vdtn359/change-log-generator-gui/master/changelog-generator.sh -O
```

**Change permission**

```bash
chmod a+x changelog-generator.sh
```

**Initialise the jar file**

```bash
./changelog-generator.sh init
```

**Use the wrapper**

```bash
sudo ./changelog-generator.sh [command]
```

## Commands

**Display help**

```bash
./changelog-generator.sh --help
```

**Display help for a specific command**

```bash
./changelog-generator.sh table --help
```

**Generate table change log**

This command will inspect an existing mysql table and generate multi tenant schema

```bash
sudo ./changelog-generator.sh table [-f filename] [-y]
```

By default, it will use the default hiive_hiive configuration. You can override the config values when you run the command
To use the default configuration without overriding it, use -y flag. To use a different configuration file, use -f flag

**Generate column change log**

This command will ask for a list of column configurations and generate both single tenant and multi tenant changes

```bash
sudo ./changelog-generator.sh column [-f filename] [-y]
```

Config values include:
* jira number: will be prepend to the multi-tenant change log file name
* author name: use in the change log
* schema: multi tenant schema, default to (accelo_shared)
* output file name: single tenant sql file name (without the extension)
