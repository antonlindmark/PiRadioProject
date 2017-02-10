package FileTransfer;

import java.io.*;
import java.net.Socket;

public class TCPEchoServerThread implements Runnable {
    public Socket connectionSocket;

    public TCPEchoServerThread(Socket s) {

        connectionSocket = s;
    }

    @Override
    public void run() {
        try {
            System.out.println("Connection Accepted");

            DataInputStream in = new DataInputStream(new BufferedInputStream(connectionSocket.getInputStream()));

            byte[] bytes = new byte[1024];  /*
             Creates a bye array to read in the message from the client
            A big size of array will result in many empty places(depending om message size),
             though they are cut in the client side, so i doesn't really matter
           */
            in.read(bytes);

            try {
                String byteToString = new String(bytes, "UTF-8"); // Converts the byte array to a string to print it
                System.out.println("Text converted using UTF-8 : " + byteToString);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            System.out.println("Request from the ip : " + connectionSocket.getInetAddress() + "\n");

            DataOutputStream out = new DataOutputStream(new DataOutputStream(connectionSocket.getOutputStream()));
            out.write(bytes);
            out.flush();

            in.close();
            out.close();
        } catch (
                IOException e
                ) {
        }
    }
}
