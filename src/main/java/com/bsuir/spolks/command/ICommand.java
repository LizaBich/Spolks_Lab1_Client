package com.bsuir.spolks.command;

import com.bsuir.spolks.exception.WrongCommandFormatException;

import java.util.Map;

public interface ICommand {

    /**
     * Execute command.
     */
    void execute();

    /**
     * Put token to command.
     *
     * @param name
     * @param value
     */
    void putToken(String name, String value);

    /**
     * Get all command tokens.
     *
     * @return hash map
     */
    Map<String, String> getTokens();

    /**
     * Verify inputted tokens.
     */
    void verifyTokens() throws WrongCommandFormatException;

    /**
     * Build command instance.
     *
     * @return instance
     */
    ICommand build();

    /**
     * Validate tokens by regex.
     *
     * @throws WrongCommandFormatException
     */
    void validateTokens() throws WrongCommandFormatException;

    /**
     * Validate single token by regex.
     *
     * @param tokenValue
     * @param regex
     * @return boolean
     */
    boolean validateToken(String tokenValue, String regex);

    /**
     * Set cmd.
     *
     * @param cmd
     */
    void setCmd(String cmd);
}
