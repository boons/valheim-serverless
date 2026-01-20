package org.boons.psu.sl.domain

data class Props(
    val gameSaveDir: String,
    val gameFiles: List<String>,
    val gameExe: String,
    val shareDir: String,
    val shareLockName: String,
    val backupDir: String,
)