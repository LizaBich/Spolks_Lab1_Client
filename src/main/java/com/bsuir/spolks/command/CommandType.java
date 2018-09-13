package com.bsuir.spolks.command;

import com.bsuir.spolks.exception.CommandNotFoundException;

public enum CommandType {
    CONNECT("connect", "Connect to server", new ConnectCommand()),
    DISCONNECT("disconnect", "Disconnect from server", new DisconnectCommand()),
    DOWNLOAD("download", "Download file from server", new DownloadCommand()),
    ECHO("echo", "Check server echo", new EchoCommand()),
    TIME("time", "Get server time", new TimeCommand()),
    HELP("help", "Display help information about available commands", new HelpCommand()),
    EXIT("exit", "Terminate program", new ExitCommand());

    private String commandName;
    private String description;

    private ICommand command;

    /**
     * Constructor.
     *
     * @param commandName
     * @param description
     * @param command
     */
    CommandType(String commandName, String description, ICommand command) {
        this.commandName = commandName;
        this.description = description;
        this.command = command;
    }

    /**
     * Find command by command name.
     *
     * @param commandName
     * @return command interface
     * @throws CommandNotFoundException
     */
    public static ICommand findCommand(String commandName) throws CommandNotFoundException {
        for (CommandType type : CommandType.values()) {
            if (type.getName().equals(commandName)) {
                return type.getCommand();
            }
        }

        throw new CommandNotFoundException("Cannot find command by name[=" + commandName + "]");
    }

    /**
     * Check if has current command.
     *
     * @param commandName
     * @return boolean
     */
    public static boolean hasCommand(String commandName) {
        for (CommandType type : CommandType.values()) {
            if (type.getName().equals(commandName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get command name as String.
     *
     * @return command name
     */
    public String getName() {
        return commandName;
    }

    /**
     * Get command description.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get command by his type.
     *
     * @return command instance
     */
    public ICommand getCommand() {
        return command.build();
    }
}
