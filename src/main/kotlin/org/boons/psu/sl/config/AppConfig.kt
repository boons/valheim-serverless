package org.boons.psu.sl.config

import java.nio.file.Path
import java.nio.file.Paths
import java.util.Properties

/**
 * Application configuration loaded from properties file.
 */
data class AppConfig(
    val gameSaveDir: Path,
    val gameFiles: List<String>,
    val gameExecutable: String,
    val shareDir: Path,
    val shareLockName: String,
    val backupDir: Path,
) {
    companion object {
        private const val CONFIG_FILE = "psu-serverless.config"
        private const val GAME_SAVE_DIR_KEY = "game.savedir"
        private const val GAME_SAVE_FILES_KEY = "game.savefiles"
        private const val GAME_EXE_KEY = "game.exe"
        private const val SHARE_DIR_KEY = "share.dir"
        private const val SHARE_LOCK_NAME_KEY = "share.lockname"
        private const val BACKUP_DIR_KEY = "backup.dir"

        /**
         * Loads configuration from the properties file in classpath.
         */
        fun load(): AppConfig {
            val properties = Properties()
            val inputStream = ClassLoader.getSystemResourceAsStream(CONFIG_FILE)
                ?: throw IllegalStateException("Configuration file not found: $CONFIG_FILE")

            properties.load(inputStream)

            return AppConfig(
                gameSaveDir = Paths.get(properties.getRequiredProperty(GAME_SAVE_DIR_KEY)),
                gameFiles = properties.getRequiredProperty(GAME_SAVE_FILES_KEY).split(";"),
                gameExecutable = properties.getRequiredProperty(GAME_EXE_KEY),
                shareDir = Paths.get(properties.getRequiredProperty(SHARE_DIR_KEY)),
                shareLockName = properties.getRequiredProperty(SHARE_LOCK_NAME_KEY),
                backupDir = Paths.get(properties.getRequiredProperty(BACKUP_DIR_KEY)),
            )
        }

        private fun Properties.getRequiredProperty(key: String): String {
            return getProperty(key) ?: throw IllegalStateException("Required property not found: $key")
        }
    }
}
