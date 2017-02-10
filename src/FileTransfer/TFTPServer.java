package FileTransfer;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;

public class TFTPServer {
    public static final int TFTPPORT = 80;
    public static final int BUFSIZE = 28;
    public static final String READDIR = "C:/Github/Index/Read/";
    public static final String WRITEDIR = "C:/Github/Index/Write/";
    public static final int OP_RRQ = 1;
    public static final int OP_WRQ = 2;
    public static final int OP_DAT = 3;
    public static final int OP_ACK = 4;
    public static final int OP_ERR = 5;


    public static void main(String[] args) {
        if (args.length > 0) {
            System.err.printf("usage: java %s\n", TFTPServer.class.getCanonicalName());
            System.exit(1);
        }
        try {
            TFTPServer server = new TFTPServer();
            server.start();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void start() throws SocketException {
        byte[] buf = new byte[BUFSIZE];

		/* Create socket */
        DatagramSocket socket = new DatagramSocket(null);

		/* Create local bind point */
        SocketAddress localBindPoint = new InetSocketAddress(TFTPPORT);
        socket.bind(localBindPoint);

        System.out.printf("Listening at port %d for new requests\n", TFTPPORT);

        while (true) {        /* Loop to handle various requests */

            final InetSocketAddress clientAddress = receiveFrom(socket, buf);
            if (clientAddress == null) /* If clientAddress is null, an error occurred in receiveFrom() and we ignore it*/
                continue;

            final StringBuffer requestedFile = new StringBuffer();
            final int reqtype = ParseRQ(buf, requestedFile);	// Gets OPCode of the request.

            new Thread() {
                public void run() {
                    DatagramSocket sendSocket = null;
                    try {
                        sendSocket = new DatagramSocket(0);

                        System.out.printf("%s request for %s from %s using port %d\n",
                                (reqtype == OP_RRQ) ? "Read" : "Write",
                                sendSocket.getLocalAddress(), clientAddress.getAddress(), clientAddress.getPort());

                        if (reqtype == OP_RRQ) {      /* read request */
                            requestedFile.insert(0, READDIR);
                            HandleRQ(sendSocket, requestedFile.toString(), OP_RRQ, clientAddress);
                        } else {                       /* write request */
                            requestedFile.insert(0, WRITEDIR);
                            HandleRQ(sendSocket, requestedFile.toString(), OP_WRQ, clientAddress);
                        }
                        sendSocket.close();
                    } catch (IOException e) {
                        handleErr(2, "Access violation.", clientAddress, sendSocket);
                    }
                }
            }.start();
        }
    }

    /**
     * Reads the first block of data, i.e., the request for action (read or write).
     *
     * @param socket socket to read from
     * @param buf    where to store the read data
     * @return the Internet socket address of the client
     */
    private InetSocketAddress receiveFrom(DatagramSocket socket, byte[] buf) {

        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        packet.setPort(TFTPPORT);

        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Data: " + new String(packet.getData()));

        return new InetSocketAddress(packet.getAddress(), packet.getPort());
    }

    /**
     * Handles the request and returns the opcode
     *
     * @param buf	byte array containing the data
     * @param requestedFile	the file to append the data to
     * @return	the opcode of the request
     */
    private int ParseRQ(byte[] buf, StringBuffer requestedFile) {

        ByteBuffer wrap = ByteBuffer.wrap(buf);
        short opcode = wrap.getShort();

        int x = 2;
        while (buf[x] != 0) {	// Gets end position of the data.
            x++;
        }
        // We can now parse the request message for opcode and requested file as:
        requestedFile.append(new String(buf, 2, x - 2)); // where readBytes is the number of bytes read into the byte array buf.
        System.out.println("The filename: " + requestedFile.toString());
        while (buf[x] != 0) {
            x++;
        }
        String mode = new String(buf, x, x);
        System.out.println("The mode: " + mode);

        return opcode;
    }

    /**
     * Handles the request (READ or WRITE). Creates datagramPackets to send or be received.
     *
     * @param sendSocket	socket to handle reading and writing
     * @param path	path of the requested file
     * @param opRrq	opcode, data that determines the request
     * @param clientAddress	the address of the client
     */
    private void HandleRQ(DatagramSocket sendSocket, String path, int opRrq, InetSocketAddress clientAddress) {
        try {
            File file = new File(path);

            /* Handles READ requests */
            if (opRrq == OP_RRQ) {

                if (file.exists()) {	// Can only read if the file exists.
                    FileInputStream f = new FileInputStream(path);
                    byte[] buf = new byte[BUFSIZE];
                    int counter = 1;	// Block number counter must start at 1, 0 is ACK.

                    buf[0] = 0;
                    buf[1] = OP_DAT;
                    buf[2] = 0;
                    buf[3] = (byte) counter;

                    int n;
                    do {
                        DatagramPacket datagramPacket = new DatagramPacket(buf, BUFSIZE, clientAddress.getAddress(), clientAddress.getPort());

                        n = f.read(datagramPacket.getData(), 4, BUFSIZE - 4); // n is the data length.
                        if (n == -1)
                            n = 0;

                        System.out.println("n: " + n);
                        datagramPacket.setLength(n + 4);

                        buf[2] = (byte) ((counter >> 8) & 0xff);	// Shifts the counter and performs a bitwise 'and' operation.
                        buf[3] = (byte) (counter & 0xff);          	// Does the bitwise 'and' operation.
                        System.out.println(counter);

                        sendSocket.send(datagramPacket);
                        handleACK(sendSocket, datagramPacket, clientAddress);	// Handles the Acknowledgment to be received.

                        counter++;
                    } while (n == BUFSIZE - 4);
                    /*
                     *	Continues to send packages until there is no more to send, if
                     *	there are no more data to send the package wont be full
                     *	(less than 512 in size) and it will exit this loop.
                     */

                    f.close();
                } else {
                    handleErr(1, "File not found.", clientAddress, sendSocket);
                }

                /* Handles WRITE requests */
            } else if (opRrq == OP_WRQ) {

                if (!file.exists()) {	// File must not exist to be able to write.

                    FileOutputStream f = new FileOutputStream(path);
                    byte[] buf = new byte[BUFSIZE];

                    int counter = 0;	// Counter starts at 0 because ACK needs to be sent first.

                    buf[0] = 0;
                    buf[1] = OP_DAT;
                    buf[2] = 0;
                    buf[3] = (byte) counter;

                    int n;
                    sendACK(counter, clientAddress, sendSocket);	// Sends Acknowledgment.
                    counter = 1;

                    do {
                        DatagramPacket datagramPacket = new DatagramPacket(buf, BUFSIZE);

                        buf[2] = (byte) ((counter >> 8) & 0xff); 	// Shifts the counter and performs a bitwise 'and' operation.
                        buf[3] = (byte) (counter & 0xff);          	// Does the bitwise 'and' operation.
                        sendSocket.receive(datagramPacket);

                        n = datagramPacket.getLength() - 4;
                        f.write(datagramPacket.getData(), 4, n);  	//Writes data from the correct positions
                        System.out.println("n: " + n);
                        sendACK(counter, clientAddress, sendSocket);	// Sends Acknowledgment again.
                        counter++;
                    } while (n == BUFSIZE - 4);
                    /*
                     *	Continues to read packages until there is no more to read, if
                     *	there are no more data to read the package wont be full
                     *	(less than 512 in size) and it will exit this loop.
                     */

                    f.close();
                } else {
                    handleErr(6, "File already exists.", clientAddress, sendSocket);
                }
            }
        } catch (IOException e) {
            handleErr(2, "Access violation.", clientAddress, sendSocket);
        }
    }

    /**
     * Handles potential errors and sends a packet with valid error code and message.
     *
     * @param value	the error code
     * @param message	the error message
     * @param clientAddress	the address of the client
     * @param sendSocket	the socket to send the error from
     */
    private void handleErr(int value, String message, InetSocketAddress clientAddress, DatagramSocket sendSocket) {
        byte[] buf = new byte[4 + message.length() + 1]; // OPCode(2) + ErrCode(2) + Message(n) + Empty Byte(1)

        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length, clientAddress);

        buf[0] = 0;
        buf[1] = OP_ERR;
        buf[2] = 0;
        buf[3] = (byte) value;

        for (int i = 4; i < buf.length - 1; i++) {	// Gets the messages bytes and enters it in the buf array
            buf[i] = message.getBytes()[i - 4];		// from pos 4 to n-1 in.
        }
        buf[buf.length - 1] = 0;

        try {
            sendSocket.send(datagramPacket);
            System.out.println("\"" + message + "\" Error message sent.");
        } catch (IOException e) {
            System.err.println("Failed to send error datagram (\"" + message + "\"");
        }
    }
    /**
     * Method to receive Acknowledgment packet with error handling
     *
     * @param sendSocket	socket to receive packet from
     * @param datagramPacket	packet to control that data is correct with Ack packet
     * @param clientAdress	address to send potential errors to
     */
    private void handleACK(DatagramSocket sendSocket, DatagramPacket datagramPacket, InetSocketAddress clientAdress) {
        DatagramPacket msgACK = new DatagramPacket(new byte[1024], 1024);
        int retries = 3;
        try {
            sendSocket.setSoTimeout(5000);
            /*
             * Set a timeout to 5 seconds ,and retries to three, so max time
             * for receiving a acknowledgement will be 15 seconds.
             */
            do {
                try {
                    sendSocket.receive(msgACK);

                    if (msgACK.getData()[1] == OP_ACK &&
                            msgACK.getData()[2] == datagramPacket.getData()[2] &&
                            msgACK.getData()[3] == datagramPacket.getData()[3]) { // Check OP-block and block numbers
                        System.out.println("ACK received!");
                        break;	// If correct ACK is received, we exit the loop.

                    } else {
                        System.out.print("Retrying... ");
                    }
                } catch (SocketTimeoutException e) {
                    handleErr(0,"Socket Timeout.",clientAdress,sendSocket); //Here we send error-code 0 with a socket timeout error
                }
                retries--;

            } while (retries > 0); // Continues to try to get the acknowledgement until sent, or max retries reached
        } catch (IOException e1) {
            handleErr(2, "Access violation", clientAdress, sendSocket);
        }
    }
    /**
     * Method to send Acknowledgment packet
     *
     * @param number	block number
     * @param clientAddress	address to send packet to
     * @param sendSocket	socket to send from
     */
    private void sendACK(int number, InetSocketAddress clientAddress, DatagramSocket sendSocket) {
        byte[] buf = new byte[BUFSIZE];

        buf[0] = 0;
        buf[1] = OP_ACK;
        buf[2] = (byte) ((number >> 8) & 0xff); //Same shifting as before
        buf[3] = (byte) (number & 0xff);
        DatagramPacket datagramPacket = new DatagramPacket(buf, BUFSIZE, clientAddress.getAddress(), clientAddress.getPort());

        try {
            sendSocket.send(datagramPacket);
            System.out.println("ACK Sent.");
        } catch (IOException e) {
            System.out.println("ACK not sent");
            handleErr(2, "Access violation.", clientAddress, sendSocket);
        }
    }
}