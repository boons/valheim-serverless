package org.boons.psu.sl.service

import java.nio.file.Path
import kotlin.io.path.*

/**
 * Manages lock files to coordinate shared access to game saves.
 */
class LockManager(
    private val shareDir: Path,
    private val lockName: String
) {
    private val lockFile: Path = shareDir.resolve("$lockName.lock")

    init {
        // Ensure the share directory exists
        if (!shareDir.exists()) {
            shareDir.createDirectories()
        }
    }

    /**
     * Checks if any lock file exists in the share directory.
     *
     * @return The name of the locker if a lock exists, null otherwise
     */
    fun checkExistingLock(): String? {
        val locks = shareDir.listDirectoryEntries("*.lock")
        return if (locks.isNotEmpty()) {
            locks[0].name.substringBeforeLast(".lock")
        } else {
            null
        }
    }

    /**
     * Acquires the lock by creating a lock file.
     */
    fun acquireLock() {
        lockFile.createFile()
    }

    /**
     * Releases the lock by deleting the lock file.
     */
    fun releaseLock() {
        if (lockFile.exists()) {
            lockFile.deleteExisting()
        }
    }

    /**
     * Checks if this instance owns the lock.
     */
    fun ownsLock(): Boolean {
        return lockFile.exists()
    }
}
