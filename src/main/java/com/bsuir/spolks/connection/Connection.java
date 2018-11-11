package com.bsuir.spolks.connection;

import com.bsuir.spolks.controller.Controller;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Connection {

    /**
     * Logger to getCommand logs.
     */
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int PORT = 9999;

    private static final int SIZE_BUFF = 65000;
    /**
     * Default serverIP of server.
     */
    private String serverIP = "192.168.1.2";

    private Socket socket;

    private DataOutputStream os;
    private DataInputStream is;
    private byte clientMessage[];
    /**
     * Default constructor.
     */
    private Connection() {
        clientMessage = new byte[SIZE_BUFF];
    }
    /**
     * Constructor with server ip.
     *
     * @param serverIP
     */
    public Connection(String serverIP) {
        this();
        this.serverIP = serverIP;
    }

    /**
     * Connect to server.
     *
     * @return boolean
     */
    public boolean connect() {
        try {
            socket = new Socket(serverIP, PORT);
            socket.setKeepAlive(true);
            socket.setReuseAddress(true);
            LOGGER.log(Level.INFO, "Connected to server.");

            this.initStream();
            return true;
        } catch (SocketException e) {
            LOGGER.log(Level.ERROR, e.getMessage());
            return false;
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Couldn't connect to server. " + e.getMessage());
            return false;
        }
    }

    public boolean sendBytes(String data) {
        try{
            os.writeBytes(data);
            return true;

        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Couldn't send message. " + e.getMessage());
            return false;
        }
    }
    /**
     * Receive message from server.
     */

    public String receiveBytes() {
        int countBytes;
        try {
            countBytes = is.read(clientMessage);
            String data = new String(clientMessage, 0, countBytes);
            LOGGER.log(Level.INFO, "Server: " + data);
            return data;
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
            return  null;
        }

    }

    public int receive(byte[] buffer) {
        try {
            return is.read(buffer);
        } catch (SocketException e) {
            System.out.println();
            LOGGER.log(Level.ERROR, e.getMessage());
            System.exit(0);
            return  0;
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Close connection.
     */
    public void close() {
        try {
            is.close();
            os.close();

            socket.close();
            Controller.getInstance().setConnection(null);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
        }
    }


    private void initStream() throws IOException {
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
    }
}
