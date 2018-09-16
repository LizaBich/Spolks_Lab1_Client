package com.bsuir.spolks.command;

import com.bsuir.spolks.connection.Connection;
import com.bsuir.spolks.controller.Controller;
import com.bsuir.spolks.exception.WrongCommandFormatException;
import org.apache.logging.log4j.Level;

class TimeCommand extends AbstractCommand {

    /**
     * Execute command.
     */
    @Override
    public void execute() {
        try {
            checkTokenCount();
            Connection connection = Controller.getInstance().getConnection();

            if (connection != null) {
                executeGettingTime(connection);
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
        return new TimeCommand();
    }

    private void checkTokenCount() throws WrongCommandFormatException {
        if (getTokens().size() > 0) {
            throw new WrongCommandFormatException("Command hasn't any tokens.");
        }
    }

    private void executeGettingTime(Connection connection) {
        if(connection.sendMessage(cmd)) {
            String time = connection.receive();
            LOGGER.log(Level.INFO, "Server time: " + time);
        } else {
            LOGGER.log(Level.ERROR, "Cannot get server time...");
        }
    }
}
