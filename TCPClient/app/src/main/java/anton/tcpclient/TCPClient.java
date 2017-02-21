package anton.tcpclient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.SoundPool;
import android.os.AsyncTask;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by user on 2017-02-17.
 */

public class TCPClient extends AsyncTask{

    public static final String SERVER_IP = "192.168.43.27"; //server IP address
    public static final int SERVER_PORT = 4555;
    public InputStream input=null;
    public String fileType="";

    public TCPClient(InputStream in,String filet){
        input=in;
        fileType=filet;
    }

    public void runTcpClient() {
        try {
            Socket s = new Socket(SERVER_IP, SERVER_PORT);
            sendInputStreamToServer(input,s);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendInputStreamToServer(InputStream in, Socket s ) {
        int x;
        try {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

           // Read until end of file and then writes it to the server
            Intent resultIntent = new Intent();

            out.write(fileType.getBytes());
            out.write((char)42);
            while((x= in.read())!=-1){
                //resultIntent.putExtra("some_key", "String data");
                //setResult(Activity.RESULT_OK, resultIntent);
                //finish();
                out.write(x);
            }
            out.close();
            out.flush();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected Object doInBackground(Object[] params) {
        runTcpClient();
        return null;
    }
}

