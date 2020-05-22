package org.openjfx;

import org.openjfx.Controllers.FXMLController;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    Socket socket;

    public ClientThread(){
        this.start();
    }
    @Override
    public void run() {
        {
            try {
                socket = new Socket("127.0.0.1", 1337);


                OutputStream output = socket.getOutputStream(); //Sender data
                PrintWriter writer = new PrintWriter(output,true); //Sender data som writer
                String inRead;
                do {
                    writer.println(FXMLController.usernameText); //Sender hva som står i textfield
                    InputStream input = socket.getInputStream(); //Henter bytes som mottas fra server
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input)); //Gjør bytes om til string
                    inRead = reader.readLine(); //Lagrer strengen i inRead
                    System.out.println(inRead);
                } while (!inRead.equals("bye"));
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }




}
