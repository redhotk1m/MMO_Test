package org.openjfx;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class Server extends Thread {

    DatagramSocket socket;
    Boolean running;
    private byte[] buf = new byte[256];
    ArrayList<DatagramPacket> packetList = new ArrayList();
    ArrayList<Integer> IPList = new ArrayList();

    public Server() throws IOException{
        System.out.println("I AM SERVER");
        // Step 1 : Create a socket to listen at port 1234
        DatagramSocket socket = new DatagramSocket(1234);

        DatagramPacket packet = null;
        while (true) {
            // Step 2 : create a DatgramPacket to receive the data.
            packet = new DatagramPacket(buf, buf.length);

            // Step 3 : receive the data in byte buffer.
            socket.receive(packet); //DET SOM MOTTAS
            System.out.println("Received: [" + data(buf) + "] from: " + packet.getAddress());
            if (!IPList.contains(packet.getPort())){
                IPList.add(packet.getPort());
                System.out.println("Adding: " + packet.getPort());
            }
            InetAddress address = packet.getAddress();
            for (Integer p : IPList){
                int port = p;
                packet = new DatagramPacket(buf, buf.length,address,port);
                System.out.println("Sending: [" + data(buf) + "] to: " + port);
                socket.send(packet);
            }


            // Exit the server if the client sends "bye"
            if (data(buf).toString().equals("bye")) {
                System.out.println("Client sent bye.....EXITING");
                socket.close();
                break;
            }

            // Clear the buffer after every message.
            buf = new byte[256];
        }

    }

    // A utility method to convert the byte array
    // data into a string representation.
    public static StringBuilder data(byte[] a)
    {
        if (a == null)
            return null;
        StringBuilder ret = new StringBuilder();
        int i = 0;
        while (a[i] != 0)
        {
            ret.append((char) a[i]);
            i++;
        }
        return ret;
    }
}
