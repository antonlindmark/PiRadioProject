package FileTransfer;


import java.io.*;
import java.net.*;
import java.util.concurrent.TimeUnit;

public class TCPEchoClient {

    public static final String MSG = "An Echo Message!";

    public static void main(String[] args) throws IOException, InterruptedException {

        int MYPORT = Integer.parseInt(args[1]);
        int messages = Integer.parseInt(args[3]);
        byte[] buf = new byte[Integer.parseInt(args[2])];

        ErrorHandler er = new ErrorHandler();
        er.argLength(args);
        er.portChecker(args);
        er.ipChecker(args);
        er.transferRateFormat(args);
        er.bufFormat(args);

        while (true) {

            messages += 1; // To send 1 message at 0 transfer rate

            for (int i = 0; i < messages; i++) {
                Socket s = new Socket(args[0], MYPORT);

                TimeUnit.MILLISECONDS.sleep((5000 / messages) ); // Delay function, to send x messages / second.


                OutputStream out = s.getOutputStream();
                byte[] bufer = MSG.getBytes();  // Takes the string and converts it to bytes, then sends it away to server
                out.write(bufer);

                InputStream input = s.getInputStream();
                TimeUnit.MILLISECONDS.sleep(10); // If we don't delay here the output doesn't work properly
                int deleteEmptySpots = input.available() - MSG.length();
                /*
                 If the buffer array in the server is longer than the MSG size,
                 we will have spaces printed if we don't subtract this in the while loop below.
                  */
                while ((input.available()) - deleteEmptySpots > 0) { // While we still have space left in buffer array this happens
                    System.out.println(new String(buf, 0, input.read(buf)));  //Reads from buffer array from 0-buffersize, then loops this until we dont have any availiable input left
                }

                if (i == 0) messages -= 1;  //Sets back tranferrate to normal if its not "0"

                out.close();
                input.close();
                s.close();
                System.out.println();
            }
            System.out.println("New " + args[3] + " messages \n");
        }
    }
}