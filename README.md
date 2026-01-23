# PSU Serverless

Share a Valheim map (or any game) with your friends without a dedicated server. This application manages game save synchronization between local and cloud storage with automatic backups and lock-based coordination.

## Version 0.5

### 🎯 Features
- ✅ Automatic backup of local and cloud saves with timestamps
- ✅ Cloud save synchronization via shared directory (Dropbox, Google Drive, etc.)
- ✅ Lock mechanism to prevent conflicts when multiple players try to play simultaneously
- ✅ Support for both files and directories
- ✅ Complete directory replacement (no merge) for data integrity
- ✅ Clean, testable architecture following SOLID principles
- ✅ Comprehensive test suite

### 📋 Requirements
- Java 17 or higher
- Kotlin 2.1.20
- Gradle 8.12+

### 🚀 Quick Start

1. **Build the project**
```bash
./gradlew build
```

2. **Configure the application**

2. **Configure the application**

Create a `psu-serverless.config` file **in the same directory as the JAR** (or in the project root for development):

```properties
# Directory where the game stores its saves
game.savedir=/Users/yourname/AppData/LocalLow/IronGate/Valheim/worlds

# Files and/or directories to synchronize (semicolon-separated)
game.savefiles=MyWorld.db;MyWorld.fwl;MyWorld

# Game executable to launch
game.exe=/path/to/valheim.exe

# Shared directory (Dropbox, Google Drive, OneDrive, etc.)
share.dir=/Users/yourname/Dropbox/valheim-share

# Your player name (for lock identification)
share.lockname=YourName

# Local backup directory
backup.dir=/Users/yourname/valheim-backups
```

**Note**: An example configuration file is provided in `src/main/resources/psu-serverless.config.example`. Copy it to the JAR directory and rename it to `psu-serverless.config`.

3. **Run the application**
```bash
./gradlew run
```

Or use the generated JAR:
```bash
java -jar build/libs/psu-serverless-0.5.jar
```

### 📖 How It Works

1. **Before Game Launch**:
   - Checks if another player has the lock (is playing)
   - If no lock exists:
     - Creates a lock file with your name
     - Backs up your local saves
     - Downloads and restores cloud saves

2. **Game Playing**:
   - Your game runs normally
   - The lock prevents others from modifying the cloud saves

3. **After Game Closes**:
   - Backs up the current cloud saves
   - Uploads your local saves to the cloud
   - Releases the lock

### 🏗️ Architecture

The application follows clean code principles with clear separation of concerns:

```
src/main/kotlin/org/boons/psu/sl/
├── Main.kt                      # Entry point
├── ApplicationRunner.kt         # Application orchestration
├── config/
│   └── AppConfig.kt            # Configuration management
└── service/
    ├── BackupManager.kt        # Backup operations
    ├── FileOperationService.kt # File/directory operations
    ├── GameLauncher.kt         # Game process management
    ├── LockManager.kt          # Lock coordination
    └── SaveSyncService.kt      # Synchronization workflow
```

See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed documentation.

### 🧪 Testing

Run the test suite:
```bash
./gradlew test
```

View test report:
```bash
open build/reports/tests/test/index.html
```

### 🛠️ Development

**Build distribution**:
```bash
./gradlew installDist
```

The distribution will be in `build/install/psu-serverless/`.

**Clean build**:
```bash
./gradlew clean build
```

### 📚 Documentation

- [ARCHITECTURE.md](ARCHITECTURE.md) - Detailed architecture documentation
- [CHANGELOG.md](CHANGELOG.md) - Version history

### 🐛 Troubleshooting

**"Invalid Java installation" errors on macOS?**

See [JDK_MANAGEMENT.md](JDK_MANAGEMENT.md) for how to clean up invalid Java installations.

**Lock file stuck?**

If the application crashes, the lock file might remain. Manually delete `*.lock` files in your share directory.

**Game doesn't launch?**

Verify the `game.exe` path in your configuration. On Windows, use backslashes or double backslashes.

### 📄 License

See [LICENSE](LICENSE) file for details.

### 🤝 Contributing

This project follows clean code principles. When contributing:
- Write unit tests for new features
- Follow Kotlin coding conventions
- Update documentation
- Keep classes focused on single responsibilities
