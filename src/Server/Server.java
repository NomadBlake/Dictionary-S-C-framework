/**
 * @author Zhuhan Qin, 988039
 */
package Server;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
    private int port = 1234;
    private DictionaryDB dict;
    private ServerSocket server;
    private int numOfClient = 0;
    private ServerUI ui;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            if (Integer.parseInt(args[0]) <= 1024 || Integer.parseInt(args[0]) >= 65535) {
                System.out.println("Invalid Port Number: Port number should be between 1024 and 65535!");
                System.exit(-1);
            } else if (args.length != 2) {
                System.out.println("Lack of Parameters:\nPlease run like \"java - jar DictServer.jar <port> <dictionary-file>\"!");
            }
            Server dictServer = new Server(args[0], args[1]);
            dictServer.run();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Lack of Parameters:\nPlease run like \"java - jar DictServer.jar <port> <dictionary-file>\"!");
        } catch (NumberFormatException e) {
            System.out.println("Invalid Port Number: Port number should be between 1024 and 49151!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Server(String p, String dictPath) {
        this.port = Integer.parseInt(p);
        this.dict = new DictionaryDB(dictPath);
        this.ui = null;
        this.server = null;
    }

    public void printOperationStatus(String str) {
        System.out.println(str);
        if (ui != null) ui.getlogArea().append(str + '\n');
    }

    private void printConnectionInfo() throws UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Server Running...");
        System.out.println("Current IP address : " + ip.getHostAddress());
        System.out.println("Port = " + port);
        System.out.println("Waiting for clinet connection...\n--------------");
    }

    public void run() {
        try {
            this.server = new ServerSocket(this.port);
            printConnectionInfo();
            this.ui = new ServerUI(InetAddress.getLocalHost().getHostAddress(), String.valueOf(port), dict.getPath());
            ui.getFrame().setVisible(true);
            while (true) {
                Socket clientSocket = server.accept();
                numOfClient++;
                printOperationStatus("Server: A client connect.\nCurrent Num of client: " + String.valueOf(numOfClient));
                ThreadHandler dcThread = new ThreadHandler(this, clientSocket, dict);
                dcThread.start();
            }
        } catch (UnknownHostException e) {
            System.out.println("UnknownHostException: " + e.getMessage());
        } catch (BindException e) {
            System.out.println("Address already in use (Bind failed), try another address!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void clientDisconnect() {
        printOperationStatus("Server: A client has disconnected");
        numOfClient--;
        printOperationStatus("Server: Number of clients: " + numOfClient + "\n");
    }

}

