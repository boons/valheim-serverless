# Architecture Documentation

## Overview

The PSU Serverless application is designed with clean code principles and follows a service-oriented architecture with clear separation of concerns.

## Package Structure

```
org.boons.psu.sl/
├── Main.kt                    # Application entry point
├── ApplicationRunner.kt       # Main orchestrator
├── config/
│   └── AppConfig.kt          # Configuration management
├── service/
│   ├── BackupManager.kt      # Backup operations
│   ├── FileOperationService.kt # File/directory operations
│   ├── GameLauncher.kt       # Game process management
│   ├── LockManager.kt        # Lock file coordination
│   └── SaveSyncService.kt    # Main synchronization workflow
└── domain/
    └── Props.kt              # Legacy (deprecated)
```

## Components

### 1. Configuration Layer
- **AppConfig**: Loads and validates application configuration from properties file
- Converts string paths to Path objects
- Provides clear error messages for missing configuration

### 2. Service Layer

#### FileOperationService
- Handles all file and directory operations
- Supports both files and directories
- Directory copying replaces destination completely (no merge)
- Batch operations for efficiency

#### LockManager
- Coordinates access to shared saves
- Creates and removes lock files
- Checks for existing locks

#### BackupManager
- Creates timestamped backups
- Supports different backup types (local/cloud)
- Organizes backups in dated directories

#### GameLauncher
- Launches the game executable
- Waits for game completion
- Handles command parsing

#### SaveSyncService
- Orchestrates the entire workflow
- Manages the synchronization lifecycle:
  1. Check locks
  2. Backup local saves
  3. Restore cloud saves
  4. Launch game
  5. Backup cloud saves
  6. Upload local saves
  7. Release lock

### 3. Application Layer
- **ApplicationRunner**: Wires all services together
- **Main**: Simple entry point

## Design Principles Applied

1. **Single Responsibility**: Each class has one clear purpose
2. **Dependency Injection**: Services receive dependencies via constructor
3. **Immutability**: Configuration is immutable (data class)
4. **Clear Naming**: Method and class names describe their purpose
5. **Error Handling**: Proper validation and error messages
6. **Documentation**: KDoc comments on public APIs
7. **Separation of Concerns**: Configuration, business logic, and infrastructure are separated

## Workflow

```
Main → ApplicationRunner → SaveSyncService
                              ├─→ LockManager
                              ├─→ BackupManager
                              │    └─→ FileOperationService
                              └─→ GameLauncher
```

## Configuration

See `psu-serverless.config` for configuration options:
- `game.savedir`: Local save directory
- `game.savefiles`: Semicolon-separated list of save files/directories
- `game.exe`: Game executable command
- `share.dir`: Shared/cloud directory
- `share.lockname`: Lock identifier
- `backup.dir`: Backup directory

## Testing

The modular design makes unit testing straightforward:
- Each service can be tested independently
- Dependencies can be mocked
- No static methods (except factories)
