package org.boons.psu.sl.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.*

class LockManagerTest {

    @Test
    fun `should acquire and release lock`(@TempDir shareDir: Path) {
        // Given
        val lockManager = LockManager(shareDir, "test-lock")

        // When
        lockManager.acquireLock()

        // Then
        assertTrue(lockManager.ownsLock())
        assertEquals("test-lock", lockManager.checkExistingLock(), "Should find its own lock")

        // When
        lockManager.releaseLock()

        // Then
        assertFalse(lockManager.ownsLock())
    }

    @Test
    fun `should detect existing lock from another instance`(@TempDir shareDir: Path) {
        // Given
        val lockManager1 = LockManager(shareDir, "user1")
        val lockManager2 = LockManager(shareDir, "user2")

        // When
        lockManager1.acquireLock()
        val existingLocker = lockManager2.checkExistingLock()

        // Then
        assertEquals("user1", existingLocker)
        assertFalse(lockManager2.ownsLock())
    }

    @Test
    fun `should create share directory if it does not exist`(@TempDir tempDir: Path) {
        // Given
        val shareDir = tempDir.resolve("non-existent-share")
        assertFalse(shareDir.exists())

        // When
        LockManager(shareDir, "test")

        // Then
        assertTrue(shareDir.exists())
    }

    @Test
    fun `should return null when no lock exists`(@TempDir shareDir: Path) {
        // Given
        val lockManager = LockManager(shareDir, "test")

        // When
        val existingLocker = lockManager.checkExistingLock()

        // Then
        assertNull(existingLocker)
    }
}
