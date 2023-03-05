package org.boons.valheim.sl

import org.boons.valheim.sl.domain.Props
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.*

const val version = "0.2"

fun main() {
    println("Valheim serverless v$version")

    val props = loadProperties()
    println("Program props: $props")

    val saveFile1 = Path.of(props.gameSaveDir, "${props.gameWorldName}.db")
    val saveFile2 = Path.of(props.gameSaveDir, "${props.gameWorldName}.fwl")
    val shareDir = Path.of(props.shareDir)
    val shareLockFile = shareDir.resolve("${props.shareLockName}.lock")
    val shareFile1 = Path.of(props.shareDir, saveFile1.name)
    val shareFile2 = Path.of(props.shareDir, saveFile2.name)
    val backupDir = Path.of(props.backupDir, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + "-local")
    val backupFile1 = backupDir.resolve(saveFile1.name)
    val backupFile2 = backupDir.resolve(saveFile2.name)

    val locks = shareDir.listDirectoryEntries("*.lock")
    if (locks.isEmpty()) {
        shareLockFile.createFile()
        println("Lock set for ${props.shareLockName}")

        if (saveFile1.exists()) {
            backupDir.createDirectories()
            saveFile1.copyTo(backupFile1)
            saveFile2.copyTo(backupFile2)
            println("Local savegame backup created")
        } else {
            println("No local savegame to backup")
        }

        if (shareFile1.exists()) {
            shareFile1.copyTo(saveFile1, true)
            shareFile2.copyTo(saveFile2, true)
            println("Cloud savegame restored")
        } else {
            println("No cloud savegame found, nothing to restore")
        }
    } else {
        val locker = locks[0].name.substringBeforeLast(".lock")
        println("Map is already used by $locker. Join his game !")
    }

    // launch game
    Runtime.getRuntime().exec(props.gameExe).waitFor()

    if (shareLockFile.exists()) {
        val backupCloudDir = Path.of(
            props.backupDir,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + "-cloud"
        )
        val backupCloudFile1 = backupCloudDir.resolve(saveFile1.name)
        val backupCloudFile2 = backupCloudDir.resolve(saveFile2.name)

        if (shareFile1.exists()) {
            backupCloudDir.createDirectories()
            shareFile1.copyTo(backupCloudFile1)
            shareFile2.copyTo(backupCloudFile2)
            println("Cloud savegame backup created")
        } else {
            println("No cloud savegame to backup")
        }

        saveFile1.copyTo(shareFile1, true)
        saveFile2.copyTo(shareFile2, true)
        println("Local savegame shared")

        shareLockFile.deleteExisting()
        println("Lock removed for ${props.shareLockName}")
    }
}

fun loadProperties(): Props {
    val fis = ClassLoader.getSystemResourceAsStream("valheim-serverless.config")
    val prop = Properties()
    prop.load(fis)

    return Props(
        prop.getProperty("game.savedir"),
        prop.getProperty("game.worldname"),
        prop.getProperty("game.exe"),
        prop.getProperty("share.dir"),
        prop.getProperty("share.lockname"),
        prop.getProperty("backup.dir"),
    )
}