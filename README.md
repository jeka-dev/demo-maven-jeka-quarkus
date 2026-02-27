# Demo for using JeKa in conjunction with  Maven 

JeKa can be used for filling the missing features of other build tools

Here, we are using a Quarkus project built with Maven. the project is forked from https://github.com/domaframework/quarkus-sample.

This project consists in a native web app, that provides a GUI for managing a basket of fruits.

![screenshot.png](./screenshot.png)

Initially, users that wanted to build and run this project with *Maven*, had to fulfill some prerequisites, as installing a proper 
Graalvm JDK and setting needed environment variables.

Jeka comes to the rescue by providing full portability: 
  - Installation of proper JDK if missing.
  - Set proper environment variables prior delegating the build to Maven

## Build and execute from anywhere

You can build and run the project, without having *JeKa*, *Maven* or any *JDK* installed on the host machine.

At root of the project, just execute :
```shell
./jeka build
```
This will produce the native Quarkus image, ready to run.
For this, JeKa :
- Downloads needed JDK and JeKa version if needed
- Sets the relevant environment variables
- Executes the  `mvnw clean package -Pnative` *Maven* command.

Once built, you can execute the native image by executing :
```shell
./jeka -p
```
Any arguments passed after the `-p` option will be passed as program arguments.

> [!TIP]
> You can omit the build phase by directly executing `jeka -p`. If the native executable is not found, JeKa 
> triggers a build to generate it.

## Run the application without cloning the repo

If you have JeKa installed on the host machine, the project can be executed transparently from its remote 
Git repo (no need to clone manually).

```shell
jeka -r https://github.com/jeka-dev/demo-maven-jeka-quarkus.git -p 
```

This command can be run from any location on the host machine. The initial execution may take longer due to the 
compilation process. However, following runs will be as fast as lightning.

## Deal with IDE

The setup and build phases are coded in [jeka-src/Script.java file](jeka-src/Script.java).
To edit it conveniently in Intellij, execute :
```shell
./jeka intellij: iml
```

> [!TIP]
> You can add any public method you want here, they will be executable directly by executing  : `jeka myMethodName`.

## Run any Maven command

Any Maven command can be wrapped by JeKa. Just execute commands as `jeka mvn mvnArgs="clean compile ..."`.
