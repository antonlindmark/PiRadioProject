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
        try {
            file = new FileInputStream("C:\\Users\\user\\Desktop\\slott.jpg");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Socket s = new Socket("localhost",4950);
            DataOutputStream d = new DataOutputStream(s.getOutputStream());
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
