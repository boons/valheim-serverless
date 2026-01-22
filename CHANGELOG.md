# Changelog

## Version 0.4

### Version Upgrades
- **Kotlin**: 1.8.0 → 2.1.0 (latest stable)
- **Gradle**: 7.6 → 8.12 (latest stable)  
- **JVM Toolchain**: Kept at 17
- **Code improvements**: Replaced deprecated `Runtime.exec()` with `ProcessBuilder`

### Nouvelles fonctionnalités
- **Support des répertoires** : Le programme peut maintenant gérer des répertoires en plus des fichiers individuels
  - La propriété `game.savefiles` peut contenir des noms de fichiers ET de répertoires
  - Les répertoires sont copiés de manière récursive
  - Lors de la restauration/sauvegarde, les répertoires sont **complètement remplacés** (pas de merge)

### Exemple de configuration
```properties
game.savedir=/tmp/valheim/saves
game.savefiles=world.db;world.fwl;WorldData
game.exe=whoami
share.dir=/tmp/valheim-share
share.lockname=boons
backup.dir=/tmp/valheim-backup
```

Dans cet exemple, `world.db` et `world.fwl` sont des fichiers, et `WorldData` est un répertoire.
