package org.boons.psu.sl.service

import org.boons.psu.sl.config.AppConfig
import java.nio.file.Path

/**
 * Orchestrates the save synchronization workflow between local and cloud saves.
 */
class SaveSyncService(
    private val config: AppConfig,
    private val fileOperationService: FileOperationService,
    private val backupManager: BackupManager,
    private val lockManager: LockManager,
    private val gameLauncher: GameLauncher
) {
    private val localSavePaths: List<Path> = config.gameFiles.map { filename ->
        config.gameSaveDir.resolve(filename)
    }

    private val cloudSavePaths: List<Path> = config.gameFiles.map { filename ->
        config.shareDir.resolve(filename)
    }

    /**
     * Executes the complete save synchronization workflow:
     * 1. Check for existing locks
     * 2. Backup local saves and restore cloud saves
     * 3. Launch the game
     * 4. Backup cloud saves and upload local saves
     */
    fun execute() {
        val existingLocker = lockManager.checkExistingLock()

        if (existingLocker != null) {
            handleExistingLock(existingLocker)
        } else {
            handleNoLock()
        }

        launchGame()

        if (lockManager.ownsLock()) {
            handlePostGameSync()
        }
    }

    private fun handleExistingLock(lockerName: String) {
        println("Map is already used by $lockerName. Join the game!")
    }

    private fun handleNoLock() {
        lockManager.acquireLock()
        println("Lock set for ${config.shareLockName}")

        backupLocalSaves()
        restoreCloudSaves()
    }

    private fun backupLocalSaves() {
        val backed = backupManager.createBackup(localSavePaths, "local")
        if (backed) {
            println("Local savegame backup created")
        } else {
            println("No local savegame to backup")
        }
    }

    private fun restoreCloudSaves() {
        if (fileOperationService.anyPathExists(cloudSavePaths)) {
            fileOperationService.copyPaths(cloudSavePaths, localSavePaths, overwrite = true)
            println("Cloud savegame restored")
        } else {
            println("No cloud savegame found, nothing to restore")
        }
    }

    private fun launchGame() {
        gameLauncher.launchAndWait()
    }

    private fun handlePostGameSync() {
        backupCloudSaves()
        uploadLocalSaves()
        releaseLock()
    }

    private fun backupCloudSaves() {
        val backed = backupManager.createBackup(cloudSavePaths, "cloud")
        if (backed) {
            println("Cloud savegame backup created")
        } else {
            println("No cloud savegame to backup")
        }
    }

    private fun uploadLocalSaves() {
        fileOperationService.copyPaths(localSavePaths, cloudSavePaths, overwrite = true)
        println("Local savegame shared")
    }

    private fun releaseLock() {
        lockManager.releaseLock()
        println("Lock removed for ${config.shareLockName}")
    }
}
