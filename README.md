# Free TP Mod

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.1--26.2-blue)
![Loaders](https://img.shields.io/badge/Loaders-Fabric%20%7C%20Forge%20%7C%20NeoForge%20%7C%20Paper-orange)
![License](https://img.shields.io/badge/License-GPL--3.0-green)

[English Version](README_en-US.md)

一个支持多个 Minecraft 版本和 Loader 的服务端模组，让所有玩家都能自由使用传送命令，而不仅限于管理员(OP)。

同时，移除了传送其他实体的功能，防止你被其他玩家传到奇奇怪怪的地方。
## 安装指南

客户端选装，服务端需装。Fabric 版本还需要在服务端安装 Fabric API；Paper 版本放入服务端的 `plugins` 目录。

## 使用

- `/tp <x> <y> <z>`：将自己传送到指定坐标。
- `/tp <目标实体>`：将自己传送到目标实体。

模组不会提供传送其他实体的命令分支，也不会注册其他命令别名。Fabric、Forge 和 NeoForge 版本遇到其他模组已修改 `/tp` 时会保留现有命令并报告冲突；Paper 版本不修改命令表，而是通过公共 Bukkit API 安全处理玩家输入的 `/tp`。

## 项目结构

各平台工程统一放在 `platforms` 目录中：

- `platforms/fabric`
- `platforms/forge`
- `platforms/neoforge`
- `platforms/paper`

仓库根目录只作为跨平台入口，保留 `platforms`、文档和许可证，不再作为 Gradle 工程。

## 构建

通过 Gradle 属性 `-Ptarget=<Minecraft 版本>` 选择目标。版本号必须与对应平台 `versions` 目录中的 `.properties` 文件名完全一致；这些文件就是当前可构建版本的完整列表。PowerShell 中建议用引号包住整个 `-Ptarget` 参数。

### JDK 要求

| Minecraft 版本 | JDK |
| --- | --- |
| 1.20.1–1.20.4 | 17 |
| 1.20.5–1.21.11 | 21 |
| 26.x | 25 |

具体要求以 `platforms/<平台>/versions/<版本>.properties` 中的 `java_version` 为准。构建前应确保 `JAVA_HOME` 指向相应的 JDK。

### Forge

Forge 1.20.1–1.20.4 和 1.21 使用主 Forge Wrapper：

```powershell
cd platforms\forge
.\gradlew.bat '-Ptarget=1.20.4' build
```

Forge 1.20.6 使用独立的 ForgeGradle 7 Wrapper：

```powershell
cd platforms\forge\gradle7
.\gradlew.bat '-Ptarget=1.20.6' build
```

Forge 产物位于 `platforms/forge/build/<Minecraft 版本>/libs`。

### Fabric

在仓库根目录执行：

```powershell
gradle -p platforms\fabric '-Ptarget=1.21.4' build
```

产物位于 `platforms/fabric/build/libs`。

### NeoForge

在仓库根目录执行：

```powershell
gradle -p platforms\neoforge '-Ptarget=1.21.4' build
```

产物位于 `platforms/neoforge/build/libs`。

### Paper

在仓库根目录执行：

```powershell
gradle -p platforms\paper '-Ptarget=1.21.4' build
```

产物位于 `platforms/paper/build/libs`。

Forge 工程自带对应的 Gradle Wrapper。Fabric、NeoForge 和 Paper 需要系统中存在兼容的 `gradle` 命令，也可以将命令中的 `gradle` 替换为 `gradle.bat` 的完整路径。

构建生成的可安装文件名格式为 `ftp_mod-1.0.1+mc<版本>-<平台>.jar`；带 `-sources.jar` 后缀的文件是源码包，不应安装到服务器。需要清除旧产物并完整重建时，可以把命令末尾的 `build` 改为 `clean build`。
