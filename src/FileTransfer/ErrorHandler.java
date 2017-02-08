package FileTransfer;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * Created by user on 2016-02-02.
 */
public class ErrorHandler {

    public void portChecker(String[] args) {
        try {
            int MYPORT = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Portnumber must be an integer");
            System.exit(1);
        }
        int x = Integer.parseInt(args[1]);
        if (x < 0 || x > 65535) {     // Checks whether the port number range is corrects
            System.err.println("Port out of range");
            System.exit(1);
        }
    }

    public void ipChecker(String[] args) {
        final String ip = args[0];
        try {
            Inet4Address.getByName(ip);
        } catch (UnknownHostException e) {
            System.err.println("IP number is incorrect");
            System.exit(1);
        }
    }

    public void bufFormat(String[] args) {
        try {
            int buf = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Bufferrate must be an integer");
            System.exit(1);
        }

    }

    public void transferRateFormat(String[] args) {
        try {
            int messages = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.err.println("Transferrate must be an integer");
            System.exit(1);
        }
    }

    public void argLength(String[] args) {
        if (args.length != 4) {
            System.err.printf("usage: %s server_name port\n", args[1]);
            System.exit(1);
        }
    }
}
