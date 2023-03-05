package org.boons.valheim.sl.domain

data class Props(
    val gameSaveDir: String,
    val gameWorldName: String,
    val gameExe: String,
    val shareDir: String,
    val shareLockName: String,
    val backupDir: String,
)