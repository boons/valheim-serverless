package org.boons.psu.sl.service

/**
 * Manages the launch of the game executable.
 */
class GameLauncher(private val gameExecutable: String) {

    /**
     * Launches the game and waits for it to complete.
     *
     * @return The exit code of the game process
     */
    fun launchAndWait(): Int {
        return ProcessBuilder(parseCommand(gameExecutable))
            .inheritIO()
            .start()
            .waitFor()
    }

    private fun parseCommand(command: String): List<String> {
        return command.split(" ")
    }
}
