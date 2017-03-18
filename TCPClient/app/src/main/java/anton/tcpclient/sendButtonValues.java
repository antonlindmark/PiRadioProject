package anton.tcpclient;

import android.os.AsyncTask;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
/**
 * Created by user on 2017-03-10.
 */
// This is the class to write values, depending on what button that was clicked ( play or stop )

public class sendButtonValues extends AsyncTask<Integer, Integer, String> {
    public int dataValue;
    public String theIp;
    public int thePort;

    public sendButtonValues(int data,String ip,int port){
        dataValue=data;
        theIp=ip;
        thePort=port;
    }
    public void sendData() {
        try {
            Socket s2 = new Socket(theIp, thePort);
            DataOutputStream out = new DataOutputStream(s2.getOutputStream());
            out.write(dataValue);
            s2.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected String doInBackground(Integer... params) {
     sendData();
        return null;
    }
}
