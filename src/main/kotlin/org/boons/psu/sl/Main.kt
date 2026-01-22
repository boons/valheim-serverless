package org.boons.psu.sl

import org.boons.psu.sl.domain.Props
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.io.path.*

const val version = "0.4"

fun main() {
    println("PSU serverless v$version")

    val props = loadProperties()
    println("Program props: $props")

    val savePaths = props.gameFiles.map { filename -> Path.of(props.gameSaveDir, filename) }
    val shareDir = Path.of(props.shareDir)
    val shareLockFile = shareDir.resolve("${props.shareLockName}.lock")
    val sharePaths = savePaths.map { savePath -> Path.of(props.shareDir, savePath.name) }
    val backupDir = Path.of(props.backupDir, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + "-local")
    val backupPaths = savePaths.map { savePath -> backupDir.resolve(savePath.name) }

    val locks = shareDir.listDirectoryEntries("*.lock")
    if (locks.isEmpty()) {
        shareLockFile.createFile()
        println("Lock set for ${props.shareLockName}")

        if (anyPathExists(savePaths)) {
            backupDir.createDirectories()
            savePaths.forEachIndexed { index, savePath ->
                copyPath(savePath, backupPaths[index])
            }
            println("Local savegame backup created")
        } else {
            println("No local savegame to backup")
        }

        if (anyPathExists(sharePaths)) {
            sharePaths.forEachIndexed { index, sharePath ->
                copyPath(sharePath, savePaths[index], overwrite = true)
            }
            println("Cloud savegame restored")
        } else {
            println("No cloud savegame found, nothing to restore")
        }
    } else {
        val locker = locks[0].name.substringBeforeLast(".lock")
        println("Map is already used by $locker. Join the game !")
    }

    // launch game
    ProcessBuilder(props.gameExe.split(" "))
        .inheritIO()
        .start()
        .waitFor()

    if (shareLockFile.exists()) {
        val backupCloudDir = Path.of(
            props.backupDir,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + "-cloud"
        )
        val backupCloudPaths = savePaths.map { savePath -> backupCloudDir.resolve(savePath.name) }

        if (anyPathExists(sharePaths)) {
            backupCloudDir.createDirectories()
            sharePaths.forEachIndexed { index, sharePath ->
                copyPath(sharePath, backupCloudPaths[index])
            }
            println("Cloud savegame backup created")
        } else {
            println("No cloud savegame to backup")
        }

        savePaths.forEachIndexed { index, savePath ->
            copyPath(savePath, sharePaths[index], overwrite = true)
        }
        println("Local savegame shared")

        shareLockFile.deleteExisting()
        println("Lock removed for ${props.shareLockName}")
    }
}

/**
 * Copies a file or directory from source to destination.
 * For directories, the destination is completely replaced (no merge).
 */
@OptIn(ExperimentalPathApi::class)
fun copyPath(source: Path, destination: Path, overwrite: Boolean = false) {
    if (!source.exists()) {
        return
    }

    if (source.isDirectory()) {
        // If destination already exists, delete it completely
        if (destination.exists() && overwrite) {
            destination.deleteRecursively()
        }

        // Copy the directory recursively
        source.copyToRecursively(destination, followLinks = false, overwrite = overwrite)
    } else {
        // It's a simple file
        destination.parent?.createDirectories()
        source.copyTo(destination, overwrite = overwrite)
    }
}

/**
 * Checks if at least one path exists.
 */
fun anyPathExists(paths: List<Path>): Boolean {
    return paths.any { it.exists() }
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