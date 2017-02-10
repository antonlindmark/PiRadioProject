package FileTransfer;

import java.net.*;
import java.io.IOException;
import java.sql.Timestamp;

public class UDPEchoServer {
    public static final int BUFSIZE = 1024;
    public static final int MYPORT = 80;

    public static void main(String[] args) throws IOException {
        System.out.printf("Server has started");

        byte[] buf = new byte[BUFSIZE];

	/* Create socket */
        DatagramSocket socket = new DatagramSocket(null);

	/* Create local bind point */
        SocketAddress localBindPoint = new InetSocketAddress(MYPORT);
        socket.bind(localBindPoint);
        while (true) {
            /* Create datagram packet for receiving message */
            DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);

            /* Receiving message */
            socket.receive(receivePacket);
            String receivedString = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
            System.out.println(receivedString);

            /* Create datagram packet for sending message */
            DatagramPacket sendPacket =
                    new DatagramPacket(receivePacket.getData(),
                            receivePacket.getLength(),
                            receivePacket.getAddress(),
                            receivePacket.getPort());

            /* Send message*/
            socket.send(sendPacket);
            System.out.printf("UDP echo request from %s", receivePacket.getAddress().getHostAddress());
            System.out.printf(" using port %d\n", receivePacket.getPort());

            java.util.Date date = new java.util.Date();
            System.out.println(new Timestamp(date.getTime()));
        }
    }
}