# valheim-serverless
Share a valheim map with your friends without a dedicated server

## Version 0.4

### Requirements
- Java 17 or higher
- Kotlin 2.1.0
- Gradle 8.12

### Build
```bash
./gradlew build
./gradlew jar
```

The JAR file will be generated in `build/libs/psu-serverless-0.4.jar`

### Configuration
Edit `src/main/resources/psu-serverless.config`:

```properties
game.savedir=/path/to/valheim/saves
game.savefiles=world.db;world.fwl;WorldData
game.exe=/path/to/valheim.exe
share.dir=/path/to/shared/folder
share.lockname=your_name
backup.dir=/path/to/backup/folder
```

**Note**: `game.savefiles` can contain both files and directories separated by semicolons.

### Features
- ✅ Automatic backup of local saves
- ✅ Cloud save synchronization
- ✅ Lock mechanism to prevent conflicts
- ✅ Support for both files and directories
- ✅ Complete directory replacement (no merge) for data integrity

