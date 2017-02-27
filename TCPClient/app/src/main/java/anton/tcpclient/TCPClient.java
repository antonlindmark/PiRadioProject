package anton.tcpclient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.widget.EditText;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by user on 2017-02-17.
 */

public class TCPClient extends AsyncTask{

    public static final String SERVER_IP = "192.168.43.210"; //server IP address
    public static final int SERVER_PORT = 4555;
    public InputStream input=null;
    public String fileType="";
    public String ipAddress;
    public int portNr;

    public TCPClient(InputStream in, String filet, String ip,int port ){
        input=in;
        fileType=filet;
        ipAddress=ip;
        portNr=port;
    }

    public void runTcpClient() {


        int size=0;
        try {
            size = input.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(size);

       // MainActivity a = new MainActivity();
        //a.getSize(size);

        System.out.println("The ip = "+ipAddress + "the port = "+portNr);

        try {
            Socket s = new Socket(ipAddress, portNr);
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
            out.write((char)42); // terminates the path
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

