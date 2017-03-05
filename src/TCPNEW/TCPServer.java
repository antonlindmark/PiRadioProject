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
    Boolean transferFailed=false;
    String pathString;
    int maxvalue;

    public newClientThread(Socket connection){

        System.out.println("Connection from "+ connection.getInetAddress() +"accepted on port" +connection.getPort());

        try {

            d = new DataInputStream(connection.getInputStream());

            int getType;
            String otherString="";
            while ( (getType = d.read()) != (char)42) {

                otherString += (char)getType;
            }

            pathString = otherString.replaceAll("[^a-zA-Z0-9.]+","");
            System.out.println("the type :"+pathString);
            pathString = "music/"+pathString;
            f = new FileOutputStream(pathString);


            String maxval ="";
            while ( (getType = d.read()) != (char)42) {

                maxval += (char)getType;

            }
            maxvalue = Integer.parseInt(maxval);
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

            File test = new File(pathString);
            System.out.println("File Length = " + test.length()+ "  Max Value is = "+ maxvalue);
            if(maxvalue != test.length()){
                transferFailed = true;
            }

            d.close();
            f.close();

            if(transferFailed){
                System.out.println("Transfer error");
                File deletefile = new File(pathString);
                deletefile.delete();
            }
            else{
                System.out.println("File totally recieved");
            }

            // Start random file
            try{
                String command = "sudo python PiStation.py -f 89.9 "+pathString;
                Process p = Runtime.getRuntime().exec(command);
                p.waitFor();
            } catch (IOException | InterruptedException e){
                e.printStackTrace();
            }


            // SHOULDNT PRINT THIS IF CLIENT STOPS CONNECTION

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}