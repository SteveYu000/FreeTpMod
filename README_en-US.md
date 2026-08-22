# Free TP Mod

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.1--26.2-blue)
![Loaders](https://img.shields.io/badge/Loaders-Fabric%20%7C%20Forge%20%7C%20NeoForge%20%7C%20Paper-orange)
![License](https://img.shields.io/badge/License-GPL--3.0-green)

[简体中文](README.md)

A server-side mod for multiple Minecraft versions and loaders that allows every player, not just operators (OPs), to teleport freely.

It removes the ability to teleport other entities, preventing other players from teleporting you somewhere unwanted.

## Installation

Client-side installation is optional, while server-side installation is required. The Fabric version also requires Fabric API on the server. For Paper, place the plugin JAR in the server's `plugins` directory.

## Usage

- `/tp <x> <y> <z>`: Teleport yourself to the specified coordinates.
- `/tp <target entity>`: Teleport yourself to the target entity.

The mod does not provide a command branch for teleporting other entities and does not register any additional command aliases. If another mod has already modified `/tp`, the Fabric, Forge, and NeoForge versions preserve the existing command and report the conflict. The Paper version does not modify the command map; it safely handles player `/tp` input through the public Bukkit API.

## Project Structure

All platform projects are organized under the `platforms` directory:

- `platforms/fabric`
- `platforms/forge`
- `platforms/neoforge`
- `platforms/paper`

The repository root serves only as the cross-platform entry point and contains `platforms`, documentation, and the license. It is no longer a Gradle project itself.

## Building

Select a target with the Gradle property `-Ptarget=<Minecraft version>`. The version must exactly match the name of a `.properties` file in the selected platform's `versions` directory. Those files are the complete list of currently buildable targets. In PowerShell, it is recommended to quote the entire `-Ptarget` argument.

### JDK Requirements

| Minecraft version | JDK |
| --- | --- |
| 1.20.1–1.20.4 | 17 |
| 1.20.5–1.21.11 | 21 |
| 26.x | 25 |

The authoritative requirement is the `java_version` value in `platforms/<platform>/versions/<version>.properties`. Before building, make sure `JAVA_HOME` points to the appropriate JDK.

### Forge

Forge 1.20.1–1.20.4 and 1.21 use the main Forge Wrapper:

```powershell
cd platforms\forge
.\gradlew.bat '-Ptarget=1.20.4' build
```

Forge 1.20.6 uses the separate ForgeGradle 7 Wrapper:

```powershell
cd platforms\forge\gradle7
.\gradlew.bat '-Ptarget=1.20.6' build
```

Forge artifacts are written to `platforms/forge/build/<Minecraft version>/libs`.

### Fabric

Run this command from the repository root:

```powershell
gradle -p platforms\fabric '-Ptarget=1.21.4' build
```

Artifacts are written to `platforms/fabric/build/libs`.

### NeoForge

Run this command from the repository root:

```powershell
gradle -p platforms\neoforge '-Ptarget=1.21.4' build
```

Artifacts are written to `platforms/neoforge/build/libs`.

### Paper

Run this command from the repository root:

```powershell
gradle -p platforms\paper '-Ptarget=1.21.4' build
```

Artifacts are written to `platforms/paper/build/libs`.

The Forge projects include their corresponding Gradle Wrappers. Fabric, NeoForge, and Paper require a compatible `gradle` command on the system. You can also replace `gradle` in the commands above with the full path to a compatible `gradle.bat` executable.

Installable artifacts follow the naming format `ftp_mod-1.0.1+mc<version>-<platform>.jar`. Files ending in `-sources.jar` are source archives and should not be installed on the server. To remove old artifacts and perform a full rebuild, replace `build` at the end of a command with `clean build`.
