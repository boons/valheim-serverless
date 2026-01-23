# Changelog

## Version 0.5 - Major Refactoring (January 2026)

### Configuration Changes
- **External Configuration**: Configuration file must now be placed in the same directory as the JAR
- The file in `src/main/resources/psu-serverless.config.example` is now only a template
- Application will no longer load configuration from classpath
- Clear error message when configuration file is not found

### Architecture Refactoring
- **Clean Code Principles**: Complete restructuring following SOLID principles
- **Separation of Concerns**: Code organized into distinct layers:
  - `config/`: Configuration management
  - `service/`: Business logic services
  - `domain/`: Data models (legacy support)
- **Service-Oriented Architecture**: 
  - `FileOperationService`: File and directory operations
  - `LockManager`: Lock file coordination
  - `BackupManager`: Backup operations with timestamps
  - `GameLauncher`: Game process management
  - `SaveSyncService`: Main workflow orchestration
  - `ApplicationRunner`: Application lifecycle management

### Code Quality Improvements
- **Testability**: All services are independently testable
- **Documentation**: Comprehensive KDoc comments on all public APIs
- **Error Handling**: Better error messages and validation
- **Immutability**: Configuration uses immutable data classes
- **Explicit Dependencies**: Constructor-based dependency injection

### Testing
- **Unit Tests**: Comprehensive test suite with 15 tests
  - `FileOperationServiceTest`: 6 tests
  - `LockManagerTest`: 4 tests  
  - `BackupManagerTest`: 4 tests
- **TempDir**: All tests use isolated temporary directories
- **Code Coverage**: Core business logic fully tested

### Version Upgrades
- **Kotlin**: 2.1.0 → 2.1.20 (latest stable)
- **JUnit Jupiter**: 5.11.4 (added)
- **Kotlinx Coroutines**: 1.10.1 (added for future async support)
- **JVM Toolchain**: Kept at Java 17

### Documentation
- **ARCHITECTURE.md**: Complete architecture documentation
- **JDK_MANAGEMENT.md**: Guide for managing Java installations on macOS
- **Improved README**: Updated with new structure

### Breaking Changes
- `Props` class deprecated in favor of `AppConfig`
- Main class fully qualified: `org.boons.psu.sl.MainKt`

## Version 0.4

### Version Upgrades
- **Kotlin**: 1.8.0 → 2.1.0 (latest stable)
- **Gradle**: 7.6 → 8.12 (latest stable)  
- **JVM Toolchain**: Kept at 17
- **Code improvements**: Replaced deprecated `Runtime.exec()` with `ProcessBuilder`

### New Features
- **Directory Support**: The program can now handle directories in addition to individual files
  - The `game.savefiles` property can contain file AND directory names
  - Directories are copied recursively
  - During restore/save, directories are **completely replaced** (no merge)

### Configuration Example
```properties
game.savedir=/tmp/valheim/saves
game.savefiles=world.db;world.fwl;WorldData
game.exe=whoami
share.dir=/tmp/valheim-share
share.lockname=boons
backup.dir=/tmp/valheim-backup
```

In this example, `world.db` and `world.fwl` are files, and `WorldData` is a directory.
