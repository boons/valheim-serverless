package org.boons.psu.sl.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.*

class BackupManagerTest {

    @Test
    fun `should create backup with timestamp`(@TempDir tempDir: Path) {
        // Given
        val backupDir = tempDir.resolve("backups")
        val fileOps = FileOperationService()
        val backupManager = BackupManager(backupDir, fileOps)

        val sourceFile = tempDir.resolve("save.dat")
        sourceFile.writeText("game save data")

        // When
        val result = backupManager.createBackup(listOf(sourceFile), "local")

        // Then
        assertTrue(result)
        val backupDirs = backupDir.listDirectoryEntries()
        assertEquals(1, backupDirs.size)
        assertTrue(backupDirs[0].name.matches(Regex("\\d{8}-\\d{6}-local")))
        assertTrue(backupDirs[0].resolve("save.dat").exists())
    }

    @Test
    fun `should return false when no source files exist`(@TempDir tempDir: Path) {
        // Given
        val backupDir = tempDir.resolve("backups")
        val fileOps = FileOperationService()
        val backupManager = BackupManager(backupDir, fileOps)

        val nonExistentFile = tempDir.resolve("non_existent.dat")

        // When
        val result = backupManager.createBackup(listOf(nonExistentFile), "local")

        // Then
        assertFalse(result)
        assertFalse(backupDir.exists())
    }

    @Test
    fun `should backup multiple files`(@TempDir tempDir: Path) {
        // Given
        val backupDir = tempDir.resolve("backups")
        val fileOps = FileOperationService()
        val backupManager = BackupManager(backupDir, fileOps)

        val file1 = tempDir.resolve("save1.dat")
        val file2 = tempDir.resolve("save2.dat")
        file1.writeText("save 1")
        file2.writeText("save 2")

        // When
        val result = backupManager.createBackup(listOf(file1, file2), "cloud")

        // Then
        assertTrue(result)
        val backupDirs = backupDir.listDirectoryEntries()
        val backupContent = backupDirs[0]
        assertTrue(backupContent.resolve("save1.dat").exists())
        assertTrue(backupContent.resolve("save2.dat").exists())
    }

    @Test
    fun `should backup directories`(@TempDir tempDir: Path) {
        // Given
        val backupDir = tempDir.resolve("backups")
        val fileOps = FileOperationService()
        val backupManager = BackupManager(backupDir, fileOps)

        val sourceDir = tempDir.resolve("savegame")
        sourceDir.createDirectories()
        sourceDir.resolve("data.dat").writeText("game data")
        sourceDir.resolve("config.ini").writeText("game config")

        // When
        val result = backupManager.createBackup(listOf(sourceDir), "local")

        // Then
        assertTrue(result)
        val backupDirs = backupDir.listDirectoryEntries()
        val backupContent = backupDirs[0].resolve("savegame")
        assertTrue(backupContent.exists())
        assertTrue(backupContent.resolve("data.dat").exists())
        assertTrue(backupContent.resolve("config.ini").exists())
    }
}
