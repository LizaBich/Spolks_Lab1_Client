package com.bsuir.spolks.parser;

import com.bsuir.spolks.command.ICommand;
import com.bsuir.spolks.exception.CommandNotFoundException;
import com.bsuir.spolks.exception.WrongCommandFormatException;

public interface IParser {

    /**
     * Parse command from String and
     * return the CommandType instance.
     *
     * @param command as string
     * @return command instance
     */
    ICommand parse(String command) throws WrongCommandFormatException, CommandNotFoundException;
}
