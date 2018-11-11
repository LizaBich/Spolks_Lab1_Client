package com.bsuir.spolks.command;

import com.bsuir.spolks.connection.Connection;
import com.bsuir.spolks.controller.Controller;
import com.bsuir.spolks.exception.AvailableTokenNotPresentException;
import com.bsuir.spolks.exception.WrongCommandFormatException;
import com.bsuir.spolks.util.Printer;
import org.apache.logging.log4j.Level;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

class DownloadCommand extends AbstractCommand {
    private static long previousProgress = 0;

    private static final String SUCCESS = "success";
    private static final String GET_PROGRESS = "progress";

    private static final int BUFF_SIZE = 65000;

    DownloadCommand() {
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

            Map<String, String> toks = getTokens();

            String firstKey = String.valueOf(toks.keySet().toArray()[0]);
            AvailableToken currentToken = AvailableToken.find(firstKey);

            switch (currentToken) {
                case HELP:
                    executeHelp();
                    break;
                default:
                    executeDownload();
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
        return new DownloadCommand();
    }

    private void validateRequired() throws WrongCommandFormatException {
        Map<String, String> tokens = getTokens();

        if (tokens.size() > 2) {
            throw new WrongCommandFormatException("This command should have only one or two tokens.");
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

    private void executeHelp() {
        Printer.println("Command format:");
        Printer.println("   download -path='path to file' -name='file name' [-help]");
    }

    private void executeDownload() {
        Connection connection = Controller.getInstance().getConnection();

        if (connection != null) {
            if (connection.sendBytes(cmd)) {
                String[] confirmation = connection.receiveBytes().split(" ");

                if (SUCCESS.equals(confirmation[0])) {
                    final long fileSize = Long.parseLong(confirmation[1]);
                    LOGGER.log(Level.INFO, "File size: " + fileSize + " bytes");

                    try {
                        File file = new File(getTokens().get(AvailableToken.NAME.getName()));
                        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file, true));

                        int progress = (int) file.length();
                        connection.sendBytes(String.valueOf(progress));

                        if(connection.receiveBytes().equals(GET_PROGRESS)) {
                            long receivedBytes = progress;
                            byte[] buff = new byte[BUFF_SIZE];

                            int count;
                            while ((count = connection.receive(buff)) != -1) {
                                receivedBytes += count;
                                dataOutputStream.write(buff, 0, count);
                                getCurrentProgress(receivedBytes, fileSize);

                                buff = new byte[BUFF_SIZE];

                                if (receivedBytes == fileSize) {
                                    System.out.println();
                                    LOGGER.log(Level.INFO, "File is downloaded. Total size: " + receivedBytes + " bytes.");
                                    break;
                                }
                            }
                            dataOutputStream.close();
                        }
                    } catch (IOException e) {
                        LOGGER.log(Level.ERROR, e.getMessage());
                    }
                }
            }
        } else {
            LOGGER.log(Level.WARN, "You're not connected to server.");
        }
    }


    private void getCurrentProgress(long length, long size) {
        long currentProgress = (length * 100 / size);
        if (currentProgress % 1 == 0 && currentProgress != previousProgress) {
            System.out.print('\r');
            System.out.print("Progress: " + currentProgress + "%");
            previousProgress = currentProgress;
        }
    }

    private enum AvailableToken {
        PATH("path", "^[\\w .-:\\\\]+$", true),
        NAME("name", "^[\\w .-:\\\\]+$", true),
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
