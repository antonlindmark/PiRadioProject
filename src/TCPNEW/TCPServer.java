package  TCPNEW;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Anton Lindmark & Fredrik Hall on 2017-02-13.
 */
public class TCPServer {

    public static ServerSocket myServerSocket;
    public static ArrayList<String> currentSongs = new ArrayList<String>();
    public static int globallength;

    public static void main(String[] args) throws IOException {
        System.out.printf("Server has started \n");
        File [] files = new File("/home/pi/Desktop/Projekt/music").listFiles();
        showFiles(files);

        myServerSocket = new ServerSocket(4555);

        while (true) {
            Socket connectionSocket = myServerSocket.accept(); // Waits for client to send request
            System.out.println("accepted");
            new newClientThread(connectionSocket);
        }
    }
    public static void showFiles(File[] files) {
        currentSongs.clear();

        for (File file : files) {
            if (file.isDirectory()) {
                System.out.println("Directory: " + file.getName());
                showFiles(file.listFiles()); // Calls same method again.
            } else {
                System.out.println("File: " + file.getName());
                currentSongs.add(file.getName());
            }
        }
        globallength = currentSongs.size();
    }
}
class newClientThread extends Thread {
    DataInputStream d,c;
    FileOutputStream f;
    Boolean transferFailed=false;
    String pathString;
    int maxvalue;
    int temp;

    public newClientThread(Socket connection){

        System.out.println("Connection from "+ connection.getInetAddress() +"accepted on port" +connection.getPort());

        try {
            c = new DataInputStream((connection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            temp = c.read();
            if(temp==1||temp==0){
                this.start();
                //Call stop/play methods!
            }
            else{
                try {
                    d = new DataInputStream(connection.getInputStream());

                    int getType;
                    String otherString="";
                    while ( (getType = d.read()) != (char)42) {

                        otherString += (char)getType;
                    }

                    pathString = otherString.replaceAll("[^a-zA-Z0-9.]+",""); // Replaces all specialchars etc
                    System.out.println("the type :"+pathString);
                    pathString = "music/"+pathString; // Path on the raspberry
                    f = new FileOutputStream(pathString);


                    String maxval ="";
                    while ( (getType = d.read()) != (char)42) {
                        maxval += (char)getType;  // Reads maxsize of file
                    }
                    maxvalue = Integer.parseInt(maxval);
                    this.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){

        if(temp==0||temp==1){
            if(temp ==0){
                ShuffleSongs();
                System.out.println("new thread is started,play");
            }
            if(temp==1){
                Stop();
                System.out.println("new thread is started,stop");
            }
        }
        else{
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
                    //Removes file if its not transferred completely
                }
                else{
                    System.out.println("File totally recieved");
                    File [] files = new File("/home/pi/Desktop/Projekt/music").listFiles();
                    TCPServer.showFiles(files);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.Stop();
    }

    public void ShuffleSongs() {
        Random rand = new Random();
        int n = rand.nextInt(TCPServer.globallength);
        String songName = TCPServer.currentSongs.get(n);
        String command = "lxterminal -e python PiStation.py -f 89.9 music/" + songName;
        // Starts command to call python code and which folder the music files is in
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void Stop(){
        String command = "lxterminal -e sudo killall python";
        // Kills all python processes to stop the song
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping");
    }
}
