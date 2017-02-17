package TCPNEW;

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

        myServerSocket = new ServerSocket(4444);

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
            StringBuilder filepath  =  new StringBuilder();
            int y;
            while ( (y = d.read()) !=(char)42) {
                filepath.append((char)y);
            }
            String newString = filepath.toString();
            String lastString="";

            Pattern p = Pattern.compile(".*\\\\s*(.*)"); // Cuts string after last backslash
            Matcher m = p.matcher(newString);
            if (m.find()){
                System.out.println(m.group(1));
                lastString=m.group(1);
            }
            f = new FileOutputStream(lastString);
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
            System.out.println("File is totally recieved!");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
