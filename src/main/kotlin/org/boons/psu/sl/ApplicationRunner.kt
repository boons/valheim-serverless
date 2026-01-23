package org.boons.psu.sl

import org.boons.psu.sl.config.AppConfig
import org.boons.psu.sl.service.BackupManager
import org.boons.psu.sl.service.FileOperationService
import org.boons.psu.sl.service.GameLauncher
import org.boons.psu.sl.service.LockManager
import org.boons.psu.sl.service.SaveSyncService

/**
 * Main application runner that coordinates all services.
 */
class ApplicationRunner {
    companion object {
        private const val VERSION = "0.5"
    }

    fun run() {
        printBanner()

        val config = loadConfiguration()
        printConfiguration(config)

        val saveSyncService = createSaveSyncService(config)
        saveSyncService.execute()
    }

    private fun printBanner() {
        println("PSU serverless v$VERSION")
    }

    private fun loadConfiguration(): AppConfig {
        return try {
            AppConfig.load()
        } catch (e: Exception) {
            System.err.println("Failed to load configuration: ${e.message}")
            throw e
        }
    }

    private fun printConfiguration(config: AppConfig) {
        println("Program configuration:")
        println("  Game save directory: ${config.gameSaveDir}")
        println("  Game files: ${config.gameFiles.joinToString(", ")}")
        println("  Share directory: ${config.shareDir}")
        println("  Backup directory: ${config.backupDir}")
    }

    private fun createSaveSyncService(config: AppConfig): SaveSyncService {
        val fileOperationService = FileOperationService()
        val backupManager = BackupManager(config.backupDir, fileOperationService)
        val lockManager = LockManager(config.shareDir, config.shareLockName)
        val gameLauncher = GameLauncher(config.gameExecutable)

        return SaveSyncService(
            config = config,
            fileOperationService = fileOperationService,
            backupManager = backupManager,
            lockManager = lockManager,
            gameLauncher = gameLauncher
        )
    }
}
