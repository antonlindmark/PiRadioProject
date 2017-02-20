package TCPNEW;

import com.sun.xml.internal.ws.api.addressing.WSEndpointReference;

import javax.activation.MimeType;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            int getMime;
            String otherString="";
            while ( (getMime = d.read()) != (char)42) {
                System.out.println((char)getMime);

                otherString += (char)getMime;
            }


            System.out.println("the type :"+otherString);
            f = new FileOutputStream("theNewBestFile"+"."+otherString);
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
                System.out.println((char)x);
                f.write(x);
            }
            System.out.println("File is totally recieved!");

            // SHOULDNT PRINT THIS IF CLIENT STOPS CONNECTION

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
