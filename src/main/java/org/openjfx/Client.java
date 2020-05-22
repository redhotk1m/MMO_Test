package org.openjfx;

import org.openjfx.Controllers.FXMLController;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {

    public Client() throws IOException {
        System.out.println("I AM CLIENT");
        Scanner sc = new Scanner(System.in);

        // Step 1:Create the socket object for
        // carrying the data.
        DatagramSocket socket = null;

        socket = new DatagramSocket();
        InetAddress ip = InetAddress.getLocalHost();

        byte buf[] = null;

        // loop while user not enters "bye"
        while (true) {
            String inp = FXMLController.usernameText; //DET SOM SKAL SENDES

            // convert the String input into the byte array.
            buf = inp.getBytes();

            // Step 2 : Create the datagramPacket for sending
            // the data.
            DatagramPacket packet = new DatagramPacket(buf, buf.length, ip, 1234);

            // Step 3 : invoke the send call to actually send
            // the data.
            System.out.println("Sending: [" + data(buf) + "] to: " + ip + " (server)");
            socket.send(packet);

            packet = new DatagramPacket(buf,buf.length);
            socket.receive(packet);
            System.out.println("Received [" + data(buf) + "] from: " + ip + " (server)");
            // break the loop if user enters "bye"
            if (inp.equals("bye"))
                break;
            buf = new byte[65535];
        }

    }
    public static StringBuilder data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        for (int j = 0; i<a.length; i++)
            ret.append((char) a[i]);
        return ret;
    }
}

