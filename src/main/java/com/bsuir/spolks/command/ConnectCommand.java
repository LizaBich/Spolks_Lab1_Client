package com.bsuir.spolks.command;

import com.bsuir.spolks.connection.Connection;
import com.bsuir.spolks.controller.Controller;
import com.bsuir.spolks.exception.AvailableTokenNotPresentException;
import com.bsuir.spolks.exception.WrongCommandFormatException;
import com.bsuir.spolks.util.Printer;
import org.apache.logging.log4j.Level;

import java.util.Arrays;
import java.util.Map;

public class ConnectCommand extends AbstractCommand {

    public static final String SERVER_IP_REGEX = "^(\\d{1,3}\\.){3}\\d{1,3}$";

    ConnectCommand() {
        Arrays.stream(AvailableToken.values()).forEach(t -> availableTokens.put(t.getName(), t.getRegex()));
    }

    /**
     * Execute command.
     */
    @Override
    public void execute() {
        try {
            validateRequired();
            validateTokens();

            String firstKey = String.valueOf(getTokens().keySet().toArray()[0]);
            AvailableToken currentToken = AvailableToken.find(firstKey);

            switch (currentToken) {
                case IP:
                    executeConnect();
                    break;
                case HELP:
                    executeHelp();
                    break;
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
        return new ConnectCommand();
    }

    private void validateRequired() throws WrongCommandFormatException {
        Map<String, String> tokens = getTokens();

        if (tokens.size() > 1) {
            throw new WrongCommandFormatException("This command should have only one token.");
        }

        if (tokens.containsKey(AvailableToken.HELP.getName())) {
            return;
        }

        for (AvailableToken t : AvailableToken.values()) {
            if (t.isRequired()) {
                String value = tokens.get(t.getName());

                if (value == null || value.isEmpty()) {
                    throw new WrongCommandFormatException("'" + t.getName() + "' token required. Check -help.");
                }
            }
        }
    }

    private void executeConnect() {
        String address = getTokens().get(AvailableToken.IP.getName());
        Connection connection = new Connection(address);

        if (connection.connect()) {
            Controller.getInstance().setConnection(connection);
        }
    }

    private void executeHelp() {
        Printer.println("Command format:");
        Printer.println("   connect -ip='192.168.0.1' [-help]");
    }

    public enum AvailableToken {
        IP("ip", "^(\\d{1,3}\\.){3}\\d{1,3}$", true),
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
