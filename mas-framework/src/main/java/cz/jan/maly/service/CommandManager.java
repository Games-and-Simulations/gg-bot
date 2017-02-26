package cz.jan.maly.service;

import cz.jan.maly.model.metadata.CommandManagerKey;
import cz.jan.maly.model.planing.Command;
import lombok.Getter;

/**
 * Template for CommandManager. Each command manager should implement method execute for commands with same key as command manager
 * Created by Jan on 26-Feb-17.
 */
public abstract class CommandManager<K extends CommandManagerKey> {

    @Getter
    private final K commandManagerKey;

    protected CommandManager(K commandManagerKey) {
        this.commandManagerKey = commandManagerKey;
    }

    /**
     * Execute command and returns result of operation
     *
     * @param commandToExecute
     * @return
     */
    public abstract boolean executeCommand(Command<K> commandToExecute);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommandManager)) return false;

        CommandManager<?> that = (CommandManager<?>) o;

        return commandManagerKey.equals(that.commandManagerKey);
    }

    @Override
    public int hashCode() {
        return commandManagerKey.hashCode();
    }
}
