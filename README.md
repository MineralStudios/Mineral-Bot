[![Build Status](https://github.com/MineralStudios/Mineral-Bot/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/MineralStudios/Mineral-Bot/actions/workflows/gradle-publish.yml)

# Mineral-Bot
The most realistic bots in existence, based off an actual Minecraft Client.

## Build
`./gradlew build`

## How does it work?

This features a modified 1.7.10 Client with the static fields removed. This allows multiple client instances to be created under one JVM instance. Everything can be loaded as a Bukkit Plugin. Nothing is standalone.
It is also exceptionally light weight. It can handle 1500 bots fighting on a $20 dedicated server.
