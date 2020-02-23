# Dietstory v83 README

| Master |
|--------|
|[![CircleCI](https://circleci.com/gh/BenjixD/MapleSolaxiaV2/tree/master.svg?style=svg)](https://circleci.com/gh/BenjixD/MapleSolaxiaV2/tree/master)|

Credits given to Nexon, Ronan C. P. Lana, the original MapleSolaxia staff and other contributors. Project diverged from [MapleSolaxia](https://github.com/ronancpl/HeavenMS). Distributability and usage of the code is open-source. Anyone is free to install, use, modify and redistribute the contents, as long as there is no kind of commercial trading involved and the credits to the original creators are maintained within the codes.

The Dietstory Project aims to recreate most if not all of Maplestory v83 features offered. In addition, the Dietstory team have pursued further content from later versions of the game in order to improve the v83 experience. Such changes involves frequent additions to the v83 WZ client/server files diverging from the basic resources found in other server projects.

## Server Updates!

- DietStory has recently migrated a breaking change off Java 7 to supporting Java 8. See the PR for more details [here](https://github.com/BenjixD/MapleSolaxiaV2/pull/87).

- DietStory server recently integrated a REST API framework (MapleAPI) to support web service communication directly to the server. 

## Download

``Launcher and resource files coming soon!``

## Setup

This is a Java 8 project using the Apache Ant build framework with Apache Ivy dependency manager. Alternatively, setting up the project on NetBeans 8.0.2 IDE will quickly allow you to locally build and run the project. Dietstory also provides a [Docker image](https://hub.docker.com/r/benjixd/dietstory-build) for building the Dietstory Project without an environment setup. 

For general environment setup on installing a v83 Maplestory private server: http://forum.ragezone.com/f428/maplestory-private-server-v83-741739/

> Note: Dietstory development and deployment differ from the general setup. Use the provided guide above only as an orientation to v83 server setup.

### Environment Setup

Dietstory requires JDK8 with [JCE Unlimited Strength Policy 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html). Once JDK7 is installed, overwrite the following folders with the JAR files provided by jce_policy-8:
- <PATH_TO_JAVA_HOME>\jre\lib
- <PATH_TO_JAVA_HOME>\lib\security

To build the project, [Apache Ant](https://ant.apache.org/) 1.9 cli tool and [Apache Ivy](https://ant.apache.org/ivy/history/2.5.0/index.html) 2.5.0+ extension is required. Alternatively, installing [NetBeans IDE](https://netbeans.apache.org/) provides the necessary tools to build without explicitly running a terminal. If you are running NetBeans, ensure to add the external jars under `cores\` to the IDE compile-time build settings.

> NetBeans users should look to also integrate a dependency manager such as `Apache Ivy` to build this project.

#### Docker

The alternative to setting up a local environment is to use Dietstory's docker image which can be pulled from the public Docker Registry. Dietstory's build image is used for DietStory's CI/CD pipeline so expect the build image to work above all else.

```
docker pull benjixd/dietstory-build:java_8
```

> Note: Learn more about setting up a Docker daemon: https://www.docker.com/

### Building the Server

The following command compiles the Dietstory project, replacing $JAVA_HOME with your Java 8 path:

```
ant -Dplatforms.JDK_1.8.home="$JAVA_HOME" -Dnb.internal.action.name=rebuild clean resolve jar
```

#### Docker

The dietstory-build image provides the ability to build the project from a local source or a provided repository source. 

From a local source, you will need to mount the directory containing the Dietstory project.
```
docker run --rm -e BUILD_SOURCE=local -v <PATH_TO_DIETSTORY_SOURCE_ROOT>:/mnt benjixd/dietstory-build:java_8
```

From this remote source, mount a directory for the resultant build artifacts.
```
docker run --rm [-e BUILD_SOURCE=repository] [-e BUILD_BRANCH=<YOUR_TARGET_BRANCH>] -v <BUILD_FOLDER>:/mnt benjixd/dietstory-build:java_8
```

> If successful, the container should provide the `build/*`, `/dist/*`, and `cores/*` artifacts.

### Database Setup

Dietstory uses MySQL 5.7 as its preferred database engine, although any RDBMS supporting 0 datetime values of your choice should suffice. Once the database has been setup, tables must be created to satisfy Dietstory's schema.

#### Setting up Dietstory schema

Using MySQL Query Browser or any other query browser of your choice, populate the database with transactions found under `sql/`. Refer to the readme in the sql folder to run the corresponding transactions in order.

> From here you can manually register an account(s) if you do not wish to setup the Dietstory launcher.

### Configurations

Configure the IP and port you want to use for your MapleStory server in `configuration.ini` file, or set it as "localhost" if you want to run it only on your machine. 

> Ensure to provide the configurations with a database user who has at least READ, INSERT, UPDATE, DELETE privileges.

## Running the Server

Run the Dietstory server after compiling the jar with the following command:
```
java -cp "dist/*:cores/*" -Dwzpath="wz/" net.server.Server
```

> Ensure that the `configurations.ini` config file is present and correct

> Alternatively you can run the `launch.sh` or `launch.bat` scripts which call the above command for you.

Now that you have a running server, you should be able to access the server through the Dietstory client locally. To host the server, either run hosting tools such as `WampServer` or `Hamachi`, or deploy your build on a public cloud service.

### Docker

Alternatively, using the docker command to spin up the server with 5 channels:
```
docker run --rm -it -v ${PWD}:/mnt -p 7575:7575 -p 7576:7576 -p 7577:7577 -p 8484:8484 -p 8485:8485 benjixd/dietstory
```
> Environment variables: MYSQL_DB_USER, MYSQL_ROOT_PASSWORD, MYSQL_HOST_NAME, JAVA_STATIC_MEM

### Docker Compose

Use docker-compose to run the entire Dietstory service including a populated local database.

First build the `dietstory-populate-db` image locally.

```
docker build -build-arg MYSQL_HOST_NAME=dietstory_database MYSQL_ROOT_PASSWORD=<MY_DB_PASSWORD> -t dietstory-populate-db -f docker/populate_database/Dockerfile .
```

Then run the following command to spin up the entire Dietstory stack.

```
MYSQL_ROOT_PASSWORD=<MY_DB_PASSWORD> DIETSTORY_PATH=<DIETSTORY_PATH> DATA_PATH=<DATABASE_MOUNT_DIR> docker-compose -f <DIETSTORY_PATH>/docker/dev_compose/docker-compose.yml up
```
> Make sure that your provided ${MYSQL_ROOT_PASSWORD} is correct to the existing database at ${DATA_PATH}. If no existing database, then MySQL will build a new one with the provided root, ${MYSQL_ROOT_PASSWORD} pair.
> Ensure the mounted directories provided are `absolute paths`

The docker compose file spins up a MySQL container and then runs a set of initial transactions for the database. The dietstory then goes live, connecting to the new MySQL container. The default ports are exposed:
```
- 3306:3306 # MySQL DB
- 7575:7575 # Channel 1
- 7576:7576 # Channel 2
- 7577:7577 # Channel 3
- 8484:8484 # Server Listener
- 8485:8485 # Maple API
```
> This docker-compose configuration is NOT meant to be used in a production setting. Consider using MySQL secrets to secure the database and avoid using root user. The docker compose file is meant to do quick local initialization and dev testing.

## Installing the Client

For players looking to connect to a hosted Dietstory server, the Dietstory client must be installed.
- From `ManagerMsv83.exe`, install MapleStory on your folder of preference (e.g. "C:\Nexon\MapleStory") and follow their instructions
- Delete these files: `HShield` (folder), `ASPLauncher.exe`, `MapleStory.exe` and `patcher.exe`
- Extract into the client folder the `Dietstory.exe` custom client
- Overwrite the original WZ files with the provided Dietstory modified WZ files

> Note: If you plan on hosting a server, distribute a modified version of the Dietstory client with your appropriate server IP. You will need to HEX-EDIT the client by overwritting all occurences of IP addresses with your server IP.

## Additional Notes

If by any means the program did not open, or raise an error (incorrect parameter) and you are using Windows 8 or 10, it might be a compatibility issue. Try running the executable using the following settings:
- Run in compatibility mode: Windows 7
- Unchecked reduced color mode
- 640 x 480 resolution
- Unchecked disable display on high DPI settings
- Run as an administrator

> Running the client a couple of times may also fix the issue. 

> Should the client being refused to connect to the game server, it may be because firewall issues. Head to the end of this file to proceed to enabling this connection with the computer's firewall. Alternatively, one can deactivate the firewall and try opening the client again.

## WZ Editing

DO NOT USE the server's XMLs for reimporting into the client's WZ, it WILL generate some kind of bugs afterwards.
- Instead use the HaRepacker 4.2.4, encryption "GMS (old)".
- Open the desired WZ for editing and, USING THE UI, make the desired changes.
- Save the changed WZ, overwriting the original content at the client folder.
- Finally, RE-EXPORT ("Private Server..." exporting option) the changed XMLs into the server's WZ.XML files, overwriting the old contents.

These steps are IMPORTANT to maintain synchronization between the server and client modules. 

> Tutorial for editing WZ files can be found here: http://forum.ragezone.com/f719/tutorial-wz-editing-concepts-tutorial-838526/

