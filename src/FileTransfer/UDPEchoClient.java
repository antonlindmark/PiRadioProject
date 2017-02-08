package FileTransfer;

import java.io.IOException;
import java.net.*;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class UDPEchoClient {

    public static final String MSG = "An Echo Message!";

    public static void main(String[] args) throws IOException, InterruptedException {

        ErrorHandler er = new ErrorHandler();

        er.argLength(args);
        er.portChecker(args);
        er.ipChecker(args);
        er.transferRateFormat(args);
        er.bufFormat(args);

        int MYPORT = Integer.parseInt(args[1]);
        final byte[] buf = new byte[Integer.parseInt(args[2])];
        int messages = Integer.parseInt(args[3]);

        messages += 1;   // Adds one to be able to send a message when tfr is 0
        for (int i = 0; i < messages; i++) {

            java.util.Date date = new java.util.Date();   //Sends out the current time when msg is sent
            System.out.println("Sendingtime : " + new Timestamp(date.getTime()));

            TimeUnit.MILLISECONDS.sleep((1000 / messages));

            /* Create socket */
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(null);
            } catch (SocketException e) {
                e.printStackTrace();
            }

    /* Create local endpoint using bind() */
            SocketAddress localBindPoint = new InetSocketAddress(MYPORT);
            socket.bind(localBindPoint);

	/* Create remote endpoint */
            SocketAddress remoteBindPoint = new InetSocketAddress(args[0], Integer.valueOf(args[1]));

	/* Create datagram packet for sending message */
            DatagramPacket sendPacket = new DatagramPacket(MSG.getBytes(), MSG.length(), remoteBindPoint);

	/* Create datagram packet for receiving echoed message */
            DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

	/* Send and receive message*/
            socket.send(sendPacket);
            socket.receive(receivePacket);

	/* Compare sent and received message */
            String receivedString = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
            System.out.println(receivedString);
            if (receivedString.compareTo(MSG) == 0) {
                System.out.printf("%d bytes sent and received\n", receivePacket.getLength());
            } else {
                System.out.printf("Sent and received msg not equal!\n");
            }
            socket.close();

            if (i == 0) messages -= 1;  //Sets back tfr to normal if its not "0"

            java.util.Date date2 = new java.util.Date(); // //Sends out the current time when msg is recieved
            System.out.println("Recievingtime : " + new Timestamp(date2.getTime()));
        }
    }
}