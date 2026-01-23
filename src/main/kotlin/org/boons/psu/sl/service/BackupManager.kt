package org.boons.psu.sl.service

import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.path.createDirectories

/**
 * Manages backup operations for game saves.
 */
class BackupManager(
    private val backupBaseDir: Path,
    private val fileOperationService: FileOperationService
) {
    companion object {
        private const val DATE_TIME_PATTERN = "yyyyMMdd-HHmmss"
        private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)
    }

    /**
     * Creates a backup with a timestamped directory.
     *
     * @param sourcePaths The paths to backup
     * @param backupType The type of backup (e.g., "local", "cloud")
     * @return true if backup was created, false if no source files existed
     */
    fun createBackup(sourcePaths: List<Path>, backupType: String): Boolean {
        if (!fileOperationService.anyPathExists(sourcePaths)) {
            return false
        }

        val backupDir = generateBackupDirectory(backupType)
        backupDir.createDirectories()

        val backupPaths = sourcePaths.map { sourcePath: Path ->
            backupDir.resolve(sourcePath.fileName)
        }

        fileOperationService.copyPaths(sourcePaths, backupPaths)
        return true
    }

    private fun generateBackupDirectory(backupType: String): Path {
        val timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER)
        return backupBaseDir.resolve("$timestamp-$backupType")
    }
}
