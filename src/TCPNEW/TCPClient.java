package TCPNEW;

import java.io.*;
import java.net.Socket;

/**
 * Created by user on 2017-02-13.
 */
public class TCPClient {

    public static void main(String args[]){
        FileInputStream file=null;
        int x;
        String filepath = "C:\\Users\\Anton\\Pictures\\beerpong.jpg";

        try {
            file = new FileInputStream(filepath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Socket s = new Socket("192.168.0.100",4555);
            DataOutputStream d = new DataOutputStream(s.getOutputStream());
            d.write(filepath.getBytes());
            d.write((char)42);
            while ((x= file.read())!=-1){
                d.write(x);
            }
            d.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
