package org.boons.psu.sl.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.*

@OptIn(ExperimentalPathApi::class)
class FileOperationServiceTest {

    private val service = FileOperationService()

    @Test
    fun `should copy single file`(@TempDir tempDir: Path) {
        // Given
        val sourceFile = tempDir.resolve("source.txt")
        sourceFile.writeText("test content")
        val destFile = tempDir.resolve("dest.txt")

        // When
        service.copyPath(sourceFile, destFile)

        // Then
        assertTrue(destFile.exists())
        assertEquals("test content", destFile.readText())
    }

    @Test
    fun `should copy directory recursively`(@TempDir tempDir: Path) {
        // Given
        val sourceDir = tempDir.resolve("source")
        sourceDir.createDirectories()
        sourceDir.resolve("file1.txt").writeText("content1")
        sourceDir.resolve("subdir").createDirectories()
        sourceDir.resolve("subdir/file2.txt").writeText("content2")

        val destDir = tempDir.resolve("dest")

        // When
        service.copyPath(sourceDir, destDir)

        // Then
        assertTrue(destDir.exists())
        assertTrue(destDir.resolve("file1.txt").exists())
        assertTrue(destDir.resolve("subdir/file2.txt").exists())
        assertEquals("content1", destDir.resolve("file1.txt").readText())
        assertEquals("content2", destDir.resolve("subdir/file2.txt").readText())
    }

    @Test
    fun `should replace directory when overwrite is true`(@TempDir tempDir: Path) {
        // Given
        val sourceDir = tempDir.resolve("source")
        sourceDir.createDirectories()
        sourceDir.resolve("new_file.txt").writeText("new content")

        val destDir = tempDir.resolve("dest")
        destDir.createDirectories()
        destDir.resolve("old_file.txt").writeText("old content")

        // When
        service.copyPath(sourceDir, destDir, overwrite = true)

        // Then
        assertTrue(destDir.exists())
        assertTrue(destDir.resolve("new_file.txt").exists())
        assertFalse(destDir.resolve("old_file.txt").exists(), "Old file should be deleted")
    }

    @Test
    fun `should not copy non-existent path`(@TempDir tempDir: Path) {
        // Given
        val sourceFile = tempDir.resolve("non_existent.txt")
        val destFile = tempDir.resolve("dest.txt")

        // When
        service.copyPath(sourceFile, destFile)

        // Then
        assertFalse(destFile.exists())
    }

    @Test
    fun `should check if any path exists`(@TempDir tempDir: Path) {
        // Given
        val existingFile = tempDir.resolve("exists.txt")
        existingFile.writeText("test")
        val nonExistentFile = tempDir.resolve("not_exists.txt")

        // When & Then
        assertTrue(service.anyPathExists(listOf(existingFile)))
        assertFalse(service.anyPathExists(listOf(nonExistentFile)))
        assertTrue(service.anyPathExists(listOf(nonExistentFile, existingFile)))
    }

    @Test
    fun `should copy multiple paths`(@TempDir tempDir: Path) {
        // Given
        val source1 = tempDir.resolve("source1.txt")
        val source2 = tempDir.resolve("source2.txt")
        source1.writeText("content1")
        source2.writeText("content2")

        val dest1 = tempDir.resolve("dest1.txt")
        val dest2 = tempDir.resolve("dest2.txt")

        // When
        service.copyPaths(listOf(source1, source2), listOf(dest1, dest2))

        // Then
        assertTrue(dest1.exists())
        assertTrue(dest2.exists())
        assertEquals("content1", dest1.readText())
        assertEquals("content2", dest2.readText())
    }
}
