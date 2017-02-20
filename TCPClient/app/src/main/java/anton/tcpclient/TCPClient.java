package anton.tcpclient;

import android.content.Intent;
import android.os.AsyncTask;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by user on 2017-02-17.
 */

public class TCPClient extends AsyncTask{

    public static final String SERVER_IP = "192.168.43.27"; //server IP address
    public static final int SERVER_PORT = 4444;
    public String selectedPath="";

    public TCPClient(String path){
        selectedPath=path;
    }

    public void runTcpClient() {
        try {
            Socket s = new Socket(SERVER_IP, SERVER_PORT);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));

            String filepath=selectedPath;
            final File file = new File(filepath);
            

            FileInputStream input = new FileInputStream(file);
            try {
                int x;
                out.write(filepath.toString());
                out.write((char)42);
                while ((x= input.read())!=-1){
                    out.write(x);
                }
                out.flush();
                out.close();
                s.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

