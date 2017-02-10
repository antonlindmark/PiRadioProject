package FileTransfer;


import java.io.*;
import java.net.*;

public class TCPEchoServer {

    public static ServerSocket myServerSocket;

    public static void main(String[] args) throws IOException {
        System.out.printf("Server has started \n");

        myServerSocket = new ServerSocket(4950);

        while (true) {

            Socket connectionSocket = myServerSocket.accept(); // Waits for client to send request

            Thread clientThread = new Thread(new TCPEchoServerThread(connectionSocket));
            clientThread.start();
        }
    }
}