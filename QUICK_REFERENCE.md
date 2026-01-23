# Quick Reference Guide

## Project Structure

```
valheim-serverless/
├── src/
│   ├── main/
│   │   ├── kotlin/org/boons/psu/sl/
│   │   │   ├── Main.kt                      # Entry point (9 lines)
│   │   │   ├── ApplicationRunner.kt         # App orchestration (55 lines)
│   │   │   ├── config/
│   │   │   │   └── AppConfig.kt            # Configuration (51 lines)
│   │   │   ├── service/
│   │   │   │   ├── BackupManager.kt        # Backup logic (48 lines)
│   │   │   │   ├── FileOperationService.kt # File ops (63 lines)
│   │   │   │   ├── GameLauncher.kt         # Game launch (22 lines)
│   │   │   │   ├── LockManager.kt          # Lock mgmt (56 lines)
│   │   │   │   └── SaveSyncService.kt      # Main workflow (91 lines)
│   │   │   └── domain/
│   │   │       └── Props.kt                # Legacy (deprecated)
│   │   └── resources/
│   │       └── psu-serverless.config       # Configuration file
│   └── test/
│       └── kotlin/org/boons/psu/sl/service/
│           ├── BackupManagerTest.kt        # 4 tests
│           ├── FileOperationServiceTest.kt # 6 tests
│           └── LockManagerTest.kt          # 4 tests
├── build.gradle.kts                         # Build configuration
├── README.md                                # User documentation
├── ARCHITECTURE.md                          # Architecture details
└── CHANGELOG.md                             # Version history
```

## Common Commands

### Build & Test
```bash
# Clean build
./gradlew clean build

# Run tests
./gradlew test

# Run the application
./gradlew run

# Create JAR
./gradlew jar
# Output: build/libs/psu-serverless-0.5.jar

# Create distribution
./gradlew installDist
# Output: build/install/psu-serverless/
```

### Running
```bash
# Via Gradle
./gradlew run

# Via JAR
java -jar build/libs/psu-serverless-0.5.jar

# Via distribution
./build/install/psu-serverless/bin/psu-serverless
```

## Configuration Quick Reference

```properties
# Where game saves are stored locally
game.savedir=/path/to/game/saves

# Files/directories to sync (semicolon-separated)
# Can be files OR directories
game.savefiles=world.db;world.fwl;WorldData

# Game executable to launch
game.exe=/path/to/game.exe

# Shared directory (Dropbox, Google Drive, etc.)
share.dir=/path/to/shared/folder

# Your player name (for lock identification)
share.lockname=YourName

# Where to store backups locally
backup.dir=/path/to/backups
```

## Workflow Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Start Application                     │
└───────────────────────┬─────────────────────────────────┘
                        │
                        ▼
                ┌───────────────┐
                │ Check for Lock │
                └───────┬────────┘
                        │
            ┌───────────┴───────────┐
            │                       │
       Lock Exists              No Lock
            │                       │
            ▼                       ▼
    ┌──────────────┐      ┌────────────────┐
    │ Show Message │      │  Acquire Lock  │
    │ "Join Game!" │      └────────┬────────┘
    └──────┬───────┘               │
           │                       ▼
           │              ┌─────────────────┐
           │              │ Backup Local    │
           │              │ Saves (if exist)│
           │              └────────┬─────────┘
           │                       │
           │                       ▼
           │              ┌─────────────────┐
           │              │ Restore Cloud   │
           │              │ Saves (if exist)│
           │              └────────┬─────────┘
           │                       │
           └───────────────────────┤
                                   │
                                   ▼
                           ┌──────────────┐
                           │  Launch Game │
                           └──────┬───────┘
                                  │
                                  ▼
                            [Game Running]
                                  │
                                  ▼
                           ┌──────────────┐
                           │  Game Closed │
                           └──────┬───────┘
                                  │
                    ┌─────────────┴─────────────┐
                    │                           │
              Owns Lock?                  Doesn't Own Lock
                    │                           │
                    ▼                           ▼
         ┌─────────────────────┐        ┌──────────┐
         │ Backup Cloud Saves  │        │   Exit   │
         └──────────┬───────────┘        └──────────┘
                    │
                    ▼
         ┌─────────────────────┐
         │ Upload Local Saves  │
         └──────────┬───────────┘
                    │
                    ▼
         ┌─────────────────────┐
         │   Release Lock      │
         └──────────┬───────────┘
                    │
                    ▼
                ┌────────┐
                │  Exit  │
                └────────┘
```

## Code Organization Principles

### Service Responsibilities

| Service | What It Does | Dependencies |
|---------|-------------|--------------|
| `FileOperationService` | Copy files/directories | None |
| `LockManager` | Create/check/release locks | None |
| `BackupManager` | Create timestamped backups | FileOperationService |
| `GameLauncher` | Launch game process | None |
| `SaveSyncService` | Orchestrate workflow | All services |
| `ApplicationRunner` | Wire everything together | SaveSyncService |

### Testing Strategy

Each service has its own test suite:
- Tests use `@TempDir` for isolated file operations
- No mocking needed (except for future async features)
- Tests are fast (no actual game launch)
- Each test is independent

## Key Features

### 1. Directory Support
```properties
# Can handle files
game.savefiles=world.db;world.fwl

# Can handle directories
game.savefiles=WorldData;PlayerData

# Can mix both
game.savefiles=world.db;world.fwl;WorldData
```

### 2. Complete Directory Replacement
When syncing directories:
- ✅ Old directory is completely deleted
- ✅ New directory is copied fresh
- ✅ No merge conflicts
- ✅ Ensures data integrity

### 3. Timestamped Backups
```
backup-dir/
├── 20260123-143022-local/    # Before game launch
│   └── world.db
└── 20260123-163045-cloud/    # After game close
    └── world.db
```

### 4. Lock Mechanism
```
share-dir/
├── PlayerName.lock           # Active player
├── world.db                  # Shared save files
└── WorldData/                # Shared directories
```

## Troubleshooting Quick Tips

### Build Issues
```bash
# Clean everything
./gradlew clean
rm -rf ~/.gradle/caches/

# Rebuild
./gradlew build
```

### JDK Issues on macOS
```bash
# List Java installations
/usr/libexec/java_home -V

# Remove invalid JDK
rm -rf ~/Library/Java/JavaVirtualMachines/invalid-jdk-name
```

### Stuck Lock
```bash
# Manually remove lock files
rm /path/to/share-dir/*.lock
```

### File Permission Issues
```bash
# Check permissions
ls -la /path/to/share-dir

# Fix permissions (macOS/Linux)
chmod -R u+rw /path/to/share-dir
```

## Performance Notes

- Startup time: < 1 second
- File copy speed: Limited by disk I/O
- Memory usage: ~50MB
- No network overhead (uses local file sync)

## Version History

- **0.5**: Major refactoring with clean architecture
- **0.4**: Added directory support
- **0.3**: Initial stable version

## Next Steps

1. ✅ Configure `psu-serverless.config`
2. ✅ Run `./gradlew build`
3. ✅ Test with `./gradlew test`
4. ✅ Run application with `./gradlew run`
5. ✅ Share `share.dir` via cloud storage with friends

Enjoy gaming without a dedicated server! 🎮
