# Free TP Mod

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.1-blue)
![Forge Version](https://img.shields.io/badge/Forge-47.4.10-orange)
![License](https://img.shields.io/badge/License-GPL--3.0-green)

一个Minecraft 1.20.1的Forge模组，让所有玩家都能自由使用传送命令，而不仅限于管理员(OP)。

同时，移除了传送其他实体的功能，防止你被其他玩家传到奇奇怪怪的地方。
## 安装指南
客户端选装，服务端需装。

## 使用

- `/tp <x> <y> <z>`：将自己传送到指定坐标。
- `/tp <目标实体>`：将自己传送到目标实体。

模组不会提供传送其他实体的命令分支，也不会注册其他命令别名。如果其他模组已经修改了 `/tp`，Free TP 会保留现有命令并在服务器日志中报告冲突，避免破坏服务器命令树。
