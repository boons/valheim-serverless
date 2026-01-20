package org.boons.psu.sl

import org.boons.psu.sl.domain.Props
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.*

const val version = "0.3"

fun main() {
    println("PSU serverless v$version")

    val props = loadProperties()
    println("Program props: $props")

    val saveFiles = props.gameFiles.map { filename -> Path.of(props.gameSaveDir, filename) }
    val shareDir = Path.of(props.shareDir)
    val shareLockFile = shareDir.resolve("${props.shareLockName}.lock")
    val shareFiles = saveFiles.map { saveFile -> Path.of(props.shareDir, saveFile.name) }
    val backupDir = Path.of(props.backupDir, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + "-local")
    val backupFiles = saveFiles.map { saveFile -> backupDir.resolve(saveFile.name) }

    val locks = shareDir.listDirectoryEntries("*.lock")
    if (locks.isEmpty()) {
        shareLockFile.createFile()
        println("Lock set for ${props.shareLockName}")

        if (saveFiles.first().exists()) {
            backupDir.createDirectories()
            saveFiles.mapIndexed() { index, saveFile -> saveFile.copyTo(backupFiles[index]) }
            println("Local savegame backup created")
        } else {
            println("No local savegame to backup")
        }

        if (shareFiles.first().exists()) {
            shareFiles.mapIndexed { index, shareFile -> shareFile.copyTo(saveFiles[index], true) }
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
        val backupCloudFiles = saveFiles.map { saveFile -> backupCloudDir.resolve(saveFile.name) }

        if (shareFiles.first().exists()) {
            backupCloudDir.createDirectories()
            shareFiles.mapIndexed { index, shareFile -> shareFile.copyTo(backupCloudFiles[index]) }
            println("Cloud savegame backup created")
        } else {
            println("No cloud savegame to backup")
        }

        saveFiles.forEachIndexed { index, saveFile -> saveFile.copyTo(shareFiles[index], true) }
        println("Local savegame shared")

        shareLockFile.deleteExisting()
        println("Lock removed for ${props.shareLockName}")
    }
}

fun loadProperties(): Props {
    val fis = ClassLoader.getSystemResourceAsStream("psu-serverless.config")
    val prop = Properties()
    prop.load(fis)

    return Props(
        prop.getProperty("game.savedir"),
        prop.getProperty("game.savefiles").split(";"),
        prop.getProperty("game.exe"),
        prop.getProperty("share.dir"),
        prop.getProperty("share.lockname"),
        prop.getProperty("backup.dir"),
    )
}