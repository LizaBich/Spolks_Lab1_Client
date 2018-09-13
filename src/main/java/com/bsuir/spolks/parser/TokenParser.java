package com.bsuir.spolks.parser;

import com.bsuir.spolks.command.CommandType;
import com.bsuir.spolks.command.ICommand;
import com.bsuir.spolks.exception.CommandNotFoundException;
import com.bsuir.spolks.exception.WrongCommandFormatException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenParser extends AbstractParser {

    private static final String CMD_TOKEN_REGEX = "(-([a-z]+)((?==)='([\\w .-:\\\\]+)')*)";

    private static final int TOKEN_NAME_GROUP_INDEX = 2;
    private static final int TOKEN_VALUE_GROUP_INDEX = 4;

    private ICommand command;

    public TokenParser(String commandName) throws CommandNotFoundException {
        this.command = CommandType.findCommand(commandName);
    }

    /**
     * Handle parse text from cmd.
     *
     * @param cmd
     * @return command instance
     */
    @Override
    public ICommand handle(String cmd) throws WrongCommandFormatException {
        command.setCmd(cmd);

        Pattern pattern = Pattern.compile(CMD_TOKEN_REGEX);
        Matcher matcher = pattern.matcher(cmd);

        while(matcher.find()) {
            final String tokenName = matcher.group(TOKEN_NAME_GROUP_INDEX);
            final String tokenValue = matcher.group(TOKEN_VALUE_GROUP_INDEX);

            command.putToken(tokenName, tokenValue);
        }

        command.verifyTokens();
        return command;
    }
}