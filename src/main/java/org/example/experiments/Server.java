package org.example.experiments;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Unused class for networking.
 */
public class Server {

    Socket socket;
    OutputStreamWriter outputStreamWriter;
    BufferedWriter bufferedWriter;
    ServerSocket serverSocket;

    public Server() {
        try {
            serverSocket = new ServerSocket(5069);

            socket = serverSocket.accept();
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            bufferedWriter = new BufferedWriter(outputStreamWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void pambaLive(){
        try {
            bufferedWriter.write("1");
            bufferedWriter.newLine();
            bufferedWriter.flush();
//            System.out.println("poggers");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
