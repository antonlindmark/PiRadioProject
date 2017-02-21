package TCPNEW;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by user on 2017-02-13.
 */
public class TCPServer {

    public static ServerSocket myServerSocket;

    public static void main(String[] args) throws IOException {
        System.out.printf("Server has started \n");

        myServerSocket = new ServerSocket(4555);

        while (true) {
            Socket connectionSocket = myServerSocket.accept(); // Waits for client to send request
            System.out.println("accepted");
            new newClientThread(connectionSocket);
        }
    }
}
class newClientThread extends Thread {
    DataInputStream d;
    FileOutputStream f;
    Socket client;

    public newClientThread(Socket connection){

        System.out.println("Connection from "+ connection.getInetAddress() +"accepted on port" +connection.getPort());

        try {

            d = new DataInputStream(connection.getInputStream());
            int getType;
            String otherString="";
            while ( (getType = d.read()) != (char)42) {

                otherString += (char)getType;
            }
            System.out.println("the type :"+otherString);
            f = new FileOutputStream(otherString);
            client = connection;
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){
        int x;
        try {

            while ( (x = d.read()) > -1) {
                f.write(x);
            }
            d.close();
            f.close();
            System.out.println("klar");
            // SHOULDNT PRINT THIS IF CLIENT STOPS CONNECTION

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
