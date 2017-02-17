package anton.tcpclient;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by user on 2017-02-17.
 */

public class TCPClient extends AsyncTask{

    public static final String SERVER_IP = "192.168.43.27"; //server IP address
    public static final int SERVER_PORT = 5000;


    public void runTcpClient() {
        try {
            Socket s = new Socket(SERVER_IP, SERVER_PORT);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            //send output msg
            //String outMsg = "TCP connecting to " + SERVER_PORT + System.getProperty("line.separator");

            String filepath = "C:\\Users\\user\\Pictures\\cryptimage.png";

            File file = new File(filepath);
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            try {

                out.write(filepath.toString());
                out.write((char)42);
                for (int i = 0; i <bytes.length ; i++) {
                    out.write(bytes[i]);
                }
                out.flush();
                out.close();
                s.close();

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            //  out.write(outMsg);
            // out.flush();

            //s.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Object doInBackground(Object[] params) {
        runTcpClient();
        return null;
    }
}

