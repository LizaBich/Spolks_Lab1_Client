package com.bsuir.spolks.command;

import com.bsuir.spolks.exception.WrongCommandFormatException;
import org.apache.logging.log4j.Level;

class DisconnectCommand extends AbstractCommand {

    /**
     * Execute command.
     */
    @Override
    public void execute() {
        try {
            checkTokenCount();

            // todo add connection string

            if(connection != null) {
                connection.close();
                LOGGER.log(Level.INFO, "You've been disconnected from server.");
            } else {
                LOGGER.log(Level.WARN, "You're not connected to server.");
            }
        } catch (WrongCommandFormatException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
        }
    }

    /**
     * Build command instance.
     *
     * @return instance
     */
    @Override
    public ICommand build() {
        return new DisconnectCommand();
    }

    private void checkTokenCount() throws WrongCommandFormatException {
        if(getTokens().size() > 0) {
            throw new WrongCommandFormatException("Command hasn't any tokens. See -help");
        }
    }
}
