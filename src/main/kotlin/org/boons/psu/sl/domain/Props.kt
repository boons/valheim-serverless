package org.boons.psu.sl.domain

/**
 * Legacy data class kept for backward compatibility.
 * @deprecated Use AppConfig instead
 */
@Deprecated("Use AppConfig instead", ReplaceWith("AppConfig", "org.boons.psu.sl.config.AppConfig"))
data class Props(
    val gameSaveDir: String,
    val gameFiles: List<String>,
    val gameExe: String,
    val shareDir: String,
    val shareLockName: String,
    val backupDir: String,
)

