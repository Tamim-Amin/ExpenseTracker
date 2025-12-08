package com.example.expensetracker.patterns;

/**
 * COMMAND PATTERN:
 * The "Invoker" class. This class is what the client interacts with.
 * It holds a command and can be asked to execute it.
 */
public class CommandInvoker {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void executeCommand() {
        if (command != null) {
            command.execute();
        }
    }
}
