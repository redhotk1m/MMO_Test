package org.openjfx;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {

    Socket socket;
    public ServerThread(Socket socket){
        this.socket = socket;
        this.start();
    }


    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            String text;
            do {
                text = reader.readLine();
                String reverseText = new StringBuilder(text).reverse().toString();
                writer.println("Server replies: " + reverseText);
            } while (!text.equals("bye"));
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
