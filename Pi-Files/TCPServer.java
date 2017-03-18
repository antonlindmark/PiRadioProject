import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by user on 2017-02-13.
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
                //Call stop / play methods!
            }
            else{
                try {

                    d = new DataInputStream(connection.getInputStream());


                    // IF DATA =0 OR DATA =1 ??????????
                    int getType;
                    String otherString="";
                    while ( (getType = d.read()) != (char)42) {

                        otherString += (char)getType;
                    }

                    pathString = otherString.replaceAll("[^a-zA-Z0-9.]+","");
                    System.out.println("the type :"+pathString);
                    pathString = "music/"+pathString;
                    f = new FileOutputStream(pathString);


                    String maxval ="";
                    while ( (getType = d.read()) != (char)42) {

                        maxval += (char)getType;

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


    }
    public void run(){

        if(temp==0||temp==1){

            if(temp ==0){
System.out.println("Play Button Clicked");
                ShuffleSongs();
            }
            if(temp==1){
System.out.println("Stop Button Clicked");
                Stop();

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
                }
                else{
                    System.out.println("File totally recieved");

                    File [] files = new File("/home/pi/Desktop/Projekt/music").listFiles();
                    TCPServer.showFiles(files);

                }

                // Start random file


                // SHOULDNT PRINT THIS IF CLIENT STOPS CONNECTION

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void ShuffleSongs() {
        Random rand = new Random();
        int n = rand.nextInt(TCPServer.globallength);
        String songName = TCPServer.currentSongs.get(n).toString();

        String command = "lxterminal -e python PiStation.py -f 89.9 music/" + songName;


     
        try {
        Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
       
    }

    public void Stop(){
String command = "lxterminal -e sudo killall python";
try {
        Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping");

    }
}