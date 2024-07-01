# Minigolf Server/Client/Editor (Playforia) [![Test Build](https://github.com/PhilippvK/playforia-minigolf/workflows/Test%20build/badge.svg)](https://github.com/PhilippvK/playforia-minigolf/actions?query=workflow%3A%22Test+build%22) [![quay.io/philippvk/minigolf](https://quay.io/repository/philippvk/minigolf/status)](https://quay.io/repository/philippvk/minigolf)

## WARNING

Currently there are major code and toolchain changes in development. Meanwhile feel free to use the latest stable [Release](https://github.com/PhilippvK/playforia-minigolf/releases)!

## Screenshot

![Original Playforia Minigolf Main Menu](screenshot.png)

## Context

Playforia.net was an online game community created by Finnish game studio Playforia Inc. in 2002. As of the end of 2018, Playforia announced to close its web presence on January 7th, 2019. (Wikipedia: https://en.wikipedia.org/wiki/Playforia)
The gaming platform was also formerly known as Aapeli or Playray.

The Java Applet-based Minigolf Client was one of the most popular multiplayer games on the platform. When I found a partially working codebase for parts of the Playforia related Java-Projects on GitHub (https://github.com/WorldStarHipHopX/playforia) I got it running on my computer by implementing a few small changes, which are explained below.

## Features

### Original game
- 3718 Maps in 8 Categories
- Up to 4 players or Single Player mode
- Graphics quality options
- ...

### Reimplementation
- Commented out any communication with original Playforia.net servers
- Use local map store instead of database
- Added ability to pass IP of server to client
- Ability to play on a single computer and hosting a game for up to 4 players in your home network
- Removed bad words and custom tracks
- Added ability to choose a nickname freely

## Usage

### Prerequisites
- Clone this repo: `git clone git@github.com:PhilippvK/playforia-minigolf.git`
- Install Java Development Kit 21 (https://adoptium.net/en-GB/temurin/releases/)
- Install Apache `maven` for building: https://maven.apache.org/install.html
- *Optional:* Install IntelliJ IDEA Java IDE (https://www.jetbrains.com/idea/download/) and import this repository as project

### Building

Run `mvn install` in the root directory. This builds `client`, `server` and `editor` submodules and all their respectable executables

### Running

First, the server application has to be started as it provides resources like sounds, maps and textures which are required for "offline" modes, too.
By default, the server uses tracks from the project's bundled resources, however if you want to run with a custom set of tracks, launch the server using the `--tracks-dir` option!
Assuming that all 3 tools have compiled successfully (or downloaded them from the [Releases Page](https://github.com/PhilippvK/playforia-minigolf/releases)), you have 3 possible ways for running the server binary:
1. Using the IntelliJ IDE: Use the provides build artifacts or run the server by pressing the play button after compiling
2. Using the Maven tool:  Run `mvn compile exec:java` in the `./server`, `./client` or `./editor` directory
3. Use the exported JAR file: `java -jar server.jar` and so on.

The client can be started the same way (AFTER THE SERVER WAS STARTED) but you can also give launch options for server ip and game language in the following format

```bash
java -jar client.jar -server 192.168.1.7 -lang en_US # Replace IP with the one of your server (which you can find out by using for example `ifconfig`/`ipconfig`) and lang with en_US, fi_FI or sv_SE
```

**NEW:** You can now choose your nickname freely. Please avoid using hate speech,...

#### Running Minigolf Server in Docker Container

We provide an experimental Dockerfile for easy hosting of the server application. 
You can either build and run the image:
```sh
docker build -t pfmg .
docker run pfmg
```
or download the pre-built images from [quay.io](https://quay.io/repository/philippvk/minigolf) via `docker pull quay.io/philippvk/minigolf:latest`.

Running the Editor is quite straightforward as it can be started like expected: `java -jar editor.jar`

### CLI options
Common CLI options for both the client and the server:
- `-ip` to set the hostname
- `-p` to set the port
- `-h` to learn about all the available options

Server CLI options:
- `--tracks-dir` to use custom tracks instead of the default set of bundled tracks

Client CLI options:
- `--verbose` to enable debug logging
- `--norandom` to disable randomization for shots
- `--username` to set username from CLI and skip inputting it 

## Compatibility

Tested:
- Ubuntu 22.04 with Java version `21.0.3`
- Windows 10/11

## Problems
- Ratings are not synced
- Custom Tracks category disabled
- Server sometimes crashes due to race conditions

## Notices

1. The code is neither written by me nor my property. I do NOT represent the same values as people who have worked on this code before. (Original Source: https://github.com/WorldStarHipHopX/playforia)
2. I am not responsible for any bug, problems, security flaws,...
3. Also, I do not intent to extend the current codebase very much.
4. The Java code you will find in the repository is pretty bad. Some parts even look like they were generated, for example by an converter tool
5. There is actually an aimbot implemented in the client code. Look for `allowCheating` in `GameCanvas.java` for trying it out. Use it wisely.

## Contribution

### Contributors

- [@PhilippvK](https://github.com/PhilippvK) (BuyMeACoffe: https://www.buymeacoff.ee/PhilippvK)
- [@maitovelkkis](https://github.com/maitovelkkis)
- [@eYeWoRRy](https://github.com/eYeWoRRy)
- [@pehala](https://github.com/pehala)

### How to create a new Release?

1. Ensure that `git status` on `master` branch is clean and `mvn install` runs fine
2. Update version via Maven: f.e. `mvn versions:set -DnewVersion=2.1.1.0-BETA`
3. Create tag: f.e. `git tag v2.1.1.0-BETA`
4. Push master branch and tag: f.e. `git push origin master v2.1.1.0-BETA`
5. Wait 5-10 minutes until GitHub release workflow is done (See: https://github.com/PhilippvK/playforia-minigolf/actions)
6. There should be a new Draft for a Release on https://github.com/PhilippvK/playforia-minigolf/releases
7. Edit Release text and publish!

---

## Final Words

Have fun.

If you miss the good old times before Playforia.net went down, Minigolf probably was one of your favourite games. I hope you will have some fun in the single player or with friends with this little crappy piece of oldschool software!
