package com.bsuir.spolks.command;

import com.bsuir.spolks.exception.WrongCommandFormatException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

abstract class AbstractCommand implements ICommand {

    Map<String, String> availableTokens;
    private Map<String, String> tokens;

    String cmd;

    /**
     * Logger to getCommand logs.
     */
    static final Logger LOGGER = LogManager.getLogger();

    /**
     * Default constructor.
     */
    AbstractCommand() {
        tokens = new HashMap<>();
        availableTokens = new HashMap<>();
    }

    /**
     * Verify inputted tokens.
     */
    @Override
    public final void verifyTokens() throws WrongCommandFormatException {
        LOGGER.log(Level.DEBUG, "Tokens: " + tokens);

        if (!tokens.isEmpty()) {
            for (Map.Entry<String, String> fl : tokens.entrySet()) {
                final String key = fl.getKey();

                if (!availableTokens.containsKey(key)) {
                    throw new WrongCommandFormatException("The command does not contain '" + key + "' token.");
                }
            }
        }
    }

    /**
     * Get all command tokens.
     *
     * @return hash map
     */
    public final Map<String, String> getTokens() {
        return this.tokens;
    }

    /**
     * Put token to command.
     *
     * @param name
     * @param value
     */
    @Override
    public final void putToken(String name, String value) {
        this.tokens.put(name, value);
    }

    /**
     * Validate tokens by regex.
     *
     * @throws WrongCommandFormatException
     */
    @Override
    public final void validateTokens() throws WrongCommandFormatException {
        for (Map.Entry<String, String> token : getTokens().entrySet()) {
            String tokenKey = token.getKey();
            String tokenValue = token.getValue();

            String regex = availableTokens.get(tokenKey);

            if (!validateToken(tokenValue, regex)) {
                throw new WrongCommandFormatException("Token '" + tokenKey + "' is incorrect.");
            }
        }
    }

    /**
     * Validate single token by regex.
     *
     * @param tokenValue
     * @param regex
     * @return boolean
     */
    @Override
    public final boolean validateToken(String tokenValue, String regex) {
        return (tokenValue == null && regex == null)
                || (tokenValue != null && !tokenValue.isEmpty() && !regex.isEmpty() && tokenValue.matches(regex));
    }

    /**
     * Set cmd.
     *
     * @param cmd
     */
    public final void setCmd(String cmd) {
        this.cmd = cmd;
    }
}
