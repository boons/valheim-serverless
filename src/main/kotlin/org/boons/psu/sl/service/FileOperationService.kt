package org.boons.psu.sl.service

import java.nio.file.Path
import kotlin.io.path.*

/**
 * Manages file and directory operations for game saves.
 */
@OptIn(ExperimentalPathApi::class)
class FileOperationService {

    /**
     * Copies a file or directory from source to destination.
     * For directories, the destination is completely replaced (no merge with existing content).
     *
     * @param source The source path to copy from
     * @param destination The destination path to copy to
     * @param overwrite Whether to overwrite existing files/directories
     */
    fun copyPath(source: Path, destination: Path, overwrite: Boolean = false) {
        if (!source.exists()) {
            return
        }

        if (source.isDirectory()) {
            copyDirectory(source, destination, overwrite)
        } else {
            copyFile(source, destination, overwrite)
        }
    }

    /**
     * Copies multiple paths in batch.
     */
    fun copyPaths(sourcePaths: List<Path>, destinationPaths: List<Path>, overwrite: Boolean = false) {
        require(sourcePaths.size == destinationPaths.size) {
            "Source and destination lists must have the same size"
        }

        sourcePaths.indices.forEach { index ->
            copyPath(sourcePaths[index], destinationPaths[index], overwrite)
        }
    }

    /**
     * Checks if at least one path exists among the provided list.
     */
    fun anyPathExists(paths: List<Path>): Boolean {
        return paths.any { it.exists() }
    }

    private fun copyDirectory(source: Path, destination: Path, overwrite: Boolean) {
        // If destination already exists and overwrite is requested, delete it completely
        if (destination.exists() && overwrite) {
            destination.deleteRecursively()
        }

        // Copy the directory recursively
        source.copyToRecursively(destination, followLinks = false, overwrite = overwrite)
    }

    private fun copyFile(source: Path, destination: Path, overwrite: Boolean) {
        destination.parent?.createDirectories()
        source.copyTo(destination, overwrite = overwrite)
    }
}
