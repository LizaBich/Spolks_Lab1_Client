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

    /**
     * Default serverIP of server.
     */
    private String serverIP = "192.168.1.2";

    private Socket socket;

    private DataOutputStream os;
    private DataInputStream is;

    /**
     * Default constructor.
     */
    private Connection() {
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

    /**
     * Send message to server.
     *
     * @param data message to server
     * @return boolean
     */
    public boolean sendMessage(String data) {
        try {
            os.writeUTF(data);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Couldn't send message. " + e.getMessage());
            return false;
        }
    }

    /**
     * Receive message from server.
     */
    public String receive() {
        try {
            return is.readUTF();
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
            return null;
        }
    }

    public int receive(byte[] buffer) {
        try {
            return is.read(buffer);
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
            removeUUID();

            socket.close();
            Controller.getInstance().setConnection(null);
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "Error: " + e.getMessage());
        }
    }

//    private void initStream() throws IOException {
//        is = socket.getInputStream();
//        os = socket.getOutputStream();
//    }

    private void initStream() throws IOException {
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
    }

    private void removeUUID() {
        File storeID = new File("uuid.txt");
        if (storeID.delete()) {
            LOGGER.log(Level.INFO, "File with uuid is deleted!");
        } else {
            LOGGER.log(Level.WARN, "Delete operation is failed.");
        }

    }
}
