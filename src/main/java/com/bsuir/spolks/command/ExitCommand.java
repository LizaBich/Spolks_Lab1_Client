package com.bsuir.spolks.command;

import com.bsuir.spolks.connection.Connection;
import com.bsuir.spolks.controller.Controller;
import com.bsuir.spolks.exception.AvailableTokenNotPresentException;
import com.bsuir.spolks.exception.WrongCommandFormatException;
import com.bsuir.spolks.util.Printer;
import org.apache.logging.log4j.Level;

import java.util.Arrays;
import java.util.Map;

public class ExitCommand extends AbstractCommand {

    ExitCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> availableTokens.put(t.getName(), t.getRegex()));
    }

    /**
     * Execute command.
     */
    @Override
    public void execute() {
        try {
            validateTokens();
            checkTokenCount();

            Connection connection = Controller.getInstance().getConnection();
            Map<String, String> toks = getTokens();

            if (toks.size() > 0) {
                String firstKey = String.valueOf(getTokens().keySet().toArray()[0]);
                AvailableToken currentToken = AvailableToken.find(firstKey);

                switch (currentToken) {
                    case FORCE:
                        executeForceExit();
                        break;
                    case HELP:
                        executeHelp();
                        break;
                }
            } else {
                if (connection == null) {
                    Controller.getInstance().getKeyboard().wantExit(true);
                } else {
                    LOGGER.log(Level.WARN, "Connection is opened. Please, close connection to terminate program.");
                }
            }
        } catch (WrongCommandFormatException | AvailableTokenNotPresentException e) {
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
        return new ExitCommand();
    }

    private void checkTokenCount() throws WrongCommandFormatException {
        if (getTokens().size() > 1) {
            throw new WrongCommandFormatException("This command should have only one token.");
        }
    }

    private void executeForceExit() {
        Connection connection = Controller.getInstance().getConnection();

        if (connection != null) {
            connection.close();
        }

        Controller.getInstance().getKeyboard().wantExit(true);
    }

    private void executeHelp() {
        Printer.println("Command format:");
        Printer.println("   exit [-force] [-help]");
    }

    private enum AvailableToken {
        FORCE("force", null, false),
        HELP("help", null, false);

        private String name;
        private String regex;
        private boolean required;

        AvailableToken(String name, String regex, boolean required) {
            this.name = name;
            this.regex = regex;
            this.required = required;
        }

        public static AvailableToken find(String name) throws AvailableTokenNotPresentException {
            for (AvailableToken t : values()) {
                if (t.getName().equals(name)) {
                    return t;
                }
            }

            throw new AvailableTokenNotPresentException("Token '" + name + "' is not available.");
        }

        public String getName() {
            return name;
        }

        public String getRegex() {
            return regex;
        }

        public boolean isRequired() {
            return required;
        }
    }
}
