package org.boons.psu.sl.config

import java.nio.file.Path
import java.nio.file.Paths
import java.util.Properties
import kotlin.io.path.exists
import kotlin.io.path.inputStream

/**
 * Application configuration loaded from external properties file.
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
         * Loads configuration from external properties file.
         * The file must be located in the same directory as the JAR.
         * The file in resources is only an example and is never used.
         */
        fun load(): AppConfig {
            val configPath = Paths.get(CONFIG_FILE)

            if (!configPath.exists()) {
                throw IllegalStateException(
                    "Configuration file not found: $CONFIG_FILE\n" +
                    "The file must be located in the same directory as the JAR.\n" +
                    "See the example file in src/main/resources/$CONFIG_FILE"
                )
            }

            val properties = Properties()
            configPath.inputStream().use { inputStream ->
                properties.load(inputStream)
            }

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
