# Free TP Mod

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.1--26.2-blue)
![Loaders](https://img.shields.io/badge/Loaders-Fabric%20%7C%20Forge%20%7C%20NeoForge%20%7C%20Paper-orange)
![License](https://img.shields.io/badge/License-GPL--3.0-green)

一个支持多个 Minecraft 版本和 Loader 的服务端模组，让所有玩家都能自由使用传送命令，而不仅限于管理员(OP)。

同时，移除了传送其他实体的功能，防止你被其他玩家传到奇奇怪怪的地方。
## 安装指南

客户端选装，服务端需装。Fabric 版本还需要在服务端安装 Fabric API；Paper 版本放入服务端的 `plugins` 目录。

各 Loader 和 Minecraft 版本的发布状态见 [兼容性列表](COMPATIBILITY.md)。

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

仓库根目录只作为跨平台入口，保留 `platforms`、文档和许可证，不再作为 Gradle 工程。Forge 可在其平台目录中构建：

```powershell
cd platforms/forge
.\gradlew.bat -Ptarget=1.20.1 build
cd gradle7
.\gradlew.bat -Ptarget=1.20.6 build
```

Forge 构建产物位于 `platforms/forge/build/<Minecraft 版本>/libs`。
