package anton.tcpclient;

import android.graphics.Color;
import android.os.AsyncTask;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by user on 2017-02-17.
 */

public class TCPClient extends AsyncTask<Integer, Integer, String> {

    public static final String SERVER_IP = "192.168.43.210"; //server IP address
    public static final int SERVER_PORT = 4555;
    public InputStream input=null;
    public String fileType="";
    public String ipAddress;
    public int portNr;
    public int maxvalue;
    public int globalcounter =0;
    MainActivity a = new MainActivity();


    public TCPClient(InputStream in, String filet, String ip,int port ){
        input=in;
        fileType=filet;
        ipAddress=ip;
        portNr=port;
    }

    public void runTcpClient() {

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

            out.write(fileType.getBytes());
            out.write((char)42); // terminates the path
            maxvalue = in.available();
            publishProgress(maxvalue);

            int sendData;
            while((x= in.read())!=-1){
                sendData = in.available();
                publishProgress(maxvalue-sendData);
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
    protected String doInBackground(Integer... params) {
        runTcpClient();
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        int currentvalue = values[0];

        if(globalcounter<1){
            a.progressBar.setMax(values[0]);
            globalcounter++;
        }
        else{
            a.progressBar.setProgress(currentvalue);
        }
        a.progressBar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
    }
}

