package anton.tcpclient;

import android.graphics.Color;
import android.os.AsyncTask;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Anton Lindmark & Fredrik Hall on 2017-02-17.
 */

public class TCPClient extends AsyncTask<Integer, Integer, String> {

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

            // Sends the path in  the beginnig of the bytestream
            out.write(3);
            out.write(fileType.getBytes());
            out.write((char)42); // terminates the path

            maxvalue = in.available();
            String maxval = String.valueOf(maxvalue);
            out.write(maxval.getBytes());
            out.write((char)42);
            publishProgress(maxvalue);

            int sendData;
            int counter=0;
            while((x= in.read())!=-1){
                counter++;
                sendData = in.available();
                if(counter>50000){ // Publishes the data left in the filetransfer by calling publishProgress which updates every 50000 byte
                    publishProgress(maxvalue-sendData);
                    counter=0;
                }
                out.write(x);
            }
            publishProgress(maxvalue);
            out.close();
            out.flush();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Integer... params) {
        // This method runs in the background due to its an AsyncTask class
        runTcpClient();
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // When this is called, 50000 bytes has beeen transfered and this will be updated
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