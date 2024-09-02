/**
 * @author Zhuhan Qin, 988039
 */
package Client;

import java.util.concurrent.TimeoutException;
import StateCode.StateCode;

public class Client {
    private String address;
    private int port;
    private int operationCount = 0;
    private ClientUI ui;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            // Check port format.
            if (Integer.parseInt(args[1]) <= 1024 || Integer.parseInt(args[1]) >= 65535) {
                System.out.println("Invalid Port Number: Port number should be between 1024 and 65535!");
                System.exit(-1);
            } else if (args.length != 2) {
                System.out.println("Lack of Parameters:\nPlease run like \"java - jar DictClient.jar <server-adress> <server-port>\"!");
            }
            System.out.println("Dictionary Client");
            Client client = new Client(args[0], Integer.parseInt(args[1]));
            client.run();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Lack of Parameters:\nPlease run like \"java -java DictClient.java <server-adress> <server-port>");
        } catch (NumberFormatException e) {
            System.out.println("Invalid Port Number: Port number should be between 1024 and 49151!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Client(String address, int port) {
        this.address = address;
        this.port = port;
        this.operationCount = 0;
        this.ui = null;
    }

    public void run() {
        try {
            this.ui = new ClientUI(this);
            ui.getFrame().setVisible(true);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please enter <server-adress> <server-port>");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printLog(int state, String word, String meaning) {
        System.out.println("--LOG: " + String.valueOf(operationCount) + " ------");
        System.out.println("  Request:");
        switch (state) {
            case StateCode.ADD:
                System.out.println("-- Command: ADD");
                break;
            case StateCode.QUERY:
                System.out.println("-- Command: QUERY");
                break;
            case StateCode.REMOVE:
                System.out.println("-- Command: REMOVE");
                break;
            // NEW FUNCTION
            case StateCode.ADD_MEANING:
                System.out.println("-- Command: ADD MEANING");
                break;
            // NEW FUNCTION
            case StateCode.UPDATE:
                System.out.println("-- Command: UPDATE");
                break;
            default:
                System.out.println("--Error: Unknown Command");
                break;
        }
        System.out.println("  Word: " + word);
        if (state == StateCode.ADD) System.out.println("  Meaning:\n\t" + meaning);
        operationCount++;
    }

    private void printState(int state, String meaning) {
        System.out.println("  Response:");
        switch (state) {
            case StateCode.SUCCESS:
                System.out.println("  State: SUCCESS");
                break;
            case StateCode.FAIL:
                System.out.println("  State: FAIL");
                break;
            default:
                System.out.println("  Error: Unknown State");
                break;
        }
        System.out.println("  Meaning:\n\t" + meaning);
    }

    public int add(String word, String meaning) {
        String[] resultArr = execute(StateCode.ADD, word, meaning);
        return Integer.parseInt(resultArr[0]);
    }

    public int remove(String word) {
        String[] resultArr = execute(StateCode.REMOVE, word, "");
        return Integer.parseInt(resultArr[0]);
    }

    public String[] query(String word) {
        String[] resultArr = execute(StateCode.QUERY, word, "");
        return resultArr;
    }


    public int addMeaning(String word, String meaning) {
        String[] resultArr = execute(StateCode.ADD_MEANING, word, meaning);
        int state = Integer.parseInt(resultArr[0]);
        if (state == StateCode.FAIL && resultArr[1].equals("Meaning already exists!")) {
            return StateCode.MEANING_EXIST;
        }
        return state;
    }
    public int update(String word, String meaning) {
        String[] resultArr = execute(StateCode.UPDATE, word, meaning);
        return Integer.parseInt(resultArr[0]);
    }

    /*
     * Execute the command and return the result.
     */
    private String[] execute(int command, String word, String meaning) {
        int state = StateCode.FAIL;
        printLog(command, word, meaning);
        try {
            System.out.println("Trying to connect to server...");
            StartThread eThread = new StartThread(address, port, command, word, meaning);
            eThread.start();
            eThread.join(1000);
            if (eThread.isAlive()) {
                eThread.interrupt();
                throw new TimeoutException();
            }
            String[] eThreadResult = eThread.getResult();
            state = Integer.parseInt(eThreadResult[0]);
            meaning = eThreadResult[1];
            System.out.println("Connect Success!");
        } catch (TimeoutException e) {
            state = StateCode.TIMEOUT;
            meaning = "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        printState(state, meaning);
        String[] resultArr = {String.valueOf(state), meaning};
        return resultArr;
    }
}
