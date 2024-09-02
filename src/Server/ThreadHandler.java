/**
 * @author Zhuhan Qin, 988039
 */
//package Server;
//
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.net.Socket;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import StateCode.StateCode;
//
//public class ThreadHandler extends Thread{
//    private DictionaryDB dict;
//    private Socket clientSocket;
//    private Server server;
//
//    
//    private String toString(int state) {
//        String s = "UnKnown";
//        switch (state) {
//            case StateCode.QUERY:
//                s = "QUERY";
//                break;
//            case StateCode.ADD:
//                s = "ADD";
//                break;
//            case StateCode.REMOVE:
//                s = "REMOVE";
//                break;
//            default:
//                break;
//        }
//        return s;
//    }
//
//    public ThreadHandler(Server server, Socket client, DictionaryDB dict) {
//        this.server = server;
//        this.clientSocket = client;
//        this.dict = dict;
//    }
//
//    private JSONObject createResJSON(int state, String meaning) {
//        JSONObject requestJson = new JSONObject();
//        requestJson.put("state", String.valueOf(state));
//        requestJson.put("meaning", meaning);
//        return requestJson;
//    }
//
//    private JSONObject parseReqString(String res) {
//        JSONObject reqJSON = null;
//        try {
//            JSONParser parser = new JSONParser();
//            reqJSON = (JSONObject) parser.parse(res);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return reqJSON;
//    }
//
//    @Override
//    public void run() {
//        try {
//            DataInputStream reader = new DataInputStream(clientSocket.getInputStream());
//            DataOutputStream writer = new DataOutputStream(clientSocket.getOutputStream());
//            JSONObject reqJSON = parseReqString(reader.readUTF());
//            int command = Integer.parseInt(reqJSON.get("command").toString());
//            String word = (String) reqJSON.get("word");
//            server.printOnBoth("-- Get Request --\n  Command: " + toString(command) + "\n  word: " + word);
//            int state = StateCode.FAIL;
//            String meaning = (String) reqJSON.get("meaning");
//
//            switch (command) {
//                case StateCode.QUERY:
//                    if (dict.isWordExist(word)) {
//                        meaning = dict.query(word);
//                        state = StateCode.SUCCESS;
//                        server.printOnBoth("QUERY SUCCESS!");
//                    } else {
//                        state = StateCode.FAIL;
//                        server.printOnBoth("QUERY FAIL: Word Not Exist!");
//                    }
//                    writer.writeUTF(createResJSON(state, meaning).toJSONString());
//                    writer.flush();
//                    break;
//                case StateCode.ADD:
//                    if (!dict.isWordExist(word)) {
//                        dict.add(word, meaning);
//                        state = StateCode.SUCCESS;
//                        server.printOnBoth("ADD SUCCESS: " + word + "\nMeaning: " + meaning);
//                    } else {
//                        server.printOnBoth("ADD FAIL: Word Exist!");
//                        state = StateCode.FAIL;
//                    }
//                    writer.writeUTF(createResJSON(state, "").toJSONString());
//                    writer.flush();
//                    break;
//                case StateCode.REMOVE:
//                    if (dict.isWordExist(word)) {
//                        dict.remove(word);
//                        state = StateCode.SUCCESS;
//                        server.printOnBoth("REMOVE SUCCESS: " + word);
//                    } else {
//                        state = StateCode.FAIL;
//                        server.printOnBoth("REMOVE FAIL: Word Exist!");
//                    }
//                    writer.writeUTF(createResJSON(state, "").toJSONString());
//                    writer.flush();
//                    break;
//                case StateCode.ADD_MEANING:
//                    if (dict.isWordExist(word)) {
//                        if (dict.addMeaning(word, meaning)) {
//                            state = StateCode.SUCCESS;
//                            server.printOnBoth("ADD MEANING SUCCESS: " + word + "\nMeaning: " + meaning);
//                        } else {
//                            state = StateCode.MEANING_EXIST;
//                            server.printOnBoth("ADD MEANING FAIL: Meaning already exists! Please re-add a different meaning.");
//                        }
//                    } else {
//                        state = StateCode.FAIL;
//                        server.printOnBoth("ADD MEANING FAIL: Word Not Exist!");
//                    }
//                    writer.writeUTF(createResJSON(state, "").toJSONString());
//                    writer.flush();
//                    break;
//                case StateCode.UPDATE:
//                    if(dict.isWordExist(word)) {
//                        dict.update(word, meaning);
//                        state = StateCode.SUCCESS;
//                        server.printOnBoth("UPDATE SUCCESS: " + word + "\nMeaning: " + meaning);
//                    } else {
//                        state = StateCode.FAIL;
//                        server.printOnBoth("UPDATE FAIL: Word Not Exist!");
//                    }
//                    writer.writeUTF(createResJSON(state, "").toJSONString());
//                    writer.flush();
//                    break;
//                default:
//                    break;
//            }
//            reader.close();
//            writer.close();
//            clientSocket.close();
//            this.server.clientDisconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//}
package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import StateCode.StateCode;

public class ThreadHandler extends Thread {
    private DictionaryDB dict;
    private Socket clientSocket;
    private Server server;

    public ThreadHandler(Server server, Socket client, DictionaryDB dict) {
        this.server = server;
        this.clientSocket = client;
        this.dict = dict;
    }

    @Override
    public void run() {
        try (DataInputStream reader = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream writer = new DataOutputStream(clientSocket.getOutputStream())) {

            JSONObject reqJSON = parseRequest(reader.readUTF());
            int command = Integer.parseInt(reqJSON.get("command").toString());
            String word = (String) reqJSON.get("word");
            server.printOnBoth("-- Get Request --\n-- Command: " + toString(command) + "\n  word: " + word);

            int state = StateCode.FAIL;
            String meaning = (String) reqJSON.get("meaning");

            state = handleCommand(command, word, meaning, writer);

            writer.writeUTF(createResJSON(state, meaning).toJSONString());
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeClientSocket();
        }
    }

    private int handleCommand(int command, String word, String meaning, DataOutputStream writer) throws Exception {
        int state = StateCode.FAIL;
        switch (command) {
            case StateCode.QUERY:
                state = handleQuery(word, writer);
                break;
            case StateCode.ADD:
                state = handleAdd(word, meaning, writer);
                break;
            case StateCode.REMOVE:
                state = handleRemove(word, writer);
                break;
            case StateCode.ADD_MEANING:
                state = handleAddMeaning(word, meaning, writer);
                break;
            case StateCode.UPDATE:
                state = handleUpdate(word, meaning, writer);
                break;
            default:
                break;
        }
        return state;
    }

    private int handleQuery(String word, DataOutputStream writer) throws Exception {
        int state;
        String meaning = null;
        if (dict.isWordExist(word)) {
            meaning = dict.query(word);
            state = StateCode.SUCCESS;
            server.printOnBoth("QUERY SUCCESS!");
        } else {
            state = StateCode.FAIL;
            server.printOnBoth("QUERY FAIL: Word Not Exist!");
        }
        writer.writeUTF(createResJSON(state, meaning).toJSONString());
        writer.flush();
        return state;
    }

    private int handleAdd(String word, String meaning, DataOutputStream writer) throws Exception {
        int state;
        if (!dict.isWordExist(word)) {
            dict.add(word, meaning);
            state = StateCode.SUCCESS;
            server.printOnBoth("ADD SUCCESS: " + word + "\nMeaning: " + meaning);
        } else {
            state = StateCode.FAIL;
            server.printOnBoth("ADD FAIL: Word Exist!");
        }
        writer.writeUTF(createResJSON(state, "").toJSONString());
        writer.flush();
        return state;
    }

    private int handleRemove(String word, DataOutputStream writer) throws Exception {
        int state;
        if (dict.isWordExist(word)) {
            dict.remove(word);
            state = StateCode.SUCCESS;
            server.printOnBoth("REMOVE SUCCESS: " + word);
        } else {
            state = StateCode.FAIL;
            server.printOnBoth("REMOVE FAIL: Word Exist!");
        }
        writer.writeUTF(createResJSON(state, "").toJSONString());
        writer.flush();
        return state;
    }

    private int handleAddMeaning(String word, String meaning, DataOutputStream writer) throws Exception {
        int state;
        if (dict.isWordExist(word)) {
            if (dict.addMeaning(word, meaning)) {
                state = StateCode.SUCCESS;
                server.printOnBoth("ADD MEANING SUCCESS: " + word + "\nMeaning: " + meaning);
            } else {
                state = StateCode.MEANING_EXIST;
                server.printOnBoth("ADD MEANING FAIL: Meaning already exists! Please re-add a different meaning.");
            }
        } else {
            state = StateCode.FAIL;
            server.printOnBoth("ADD MEANING FAIL: Word Not Exist!");
        }
        writer.writeUTF(createResJSON(state, "").toJSONString());
        writer.flush();
        return state;
    }

    private int handleUpdate(String word, String meaning, DataOutputStream writer) throws Exception {
        int state;
        if (dict.isWordExist(word)) {
            dict.update(word, meaning);
            state = StateCode.SUCCESS;
            server.printOnBoth("UPDATE SUCCESS: " + word + "\nMeaning: " + meaning);
        } else {
            state = StateCode.FAIL;
            server.printOnBoth("UPDATE FAIL: Word Not Exist!");
        }
        writer.writeUTF(createResJSON(state, "").toJSONString());
        writer.flush();
        return state;
    }

    private JSONObject createResJSON(int state, String meaning) {
        JSONObject requestJson = new JSONObject();
        requestJson.put("state", String.valueOf(state));
        requestJson.put("meaning", meaning);
        return requestJson;
    }

    private JSONObject parseRequest(String req) {
        JSONObject reqJSON = null;
        try {
            JSONParser parser = new JSONParser();
            reqJSON = (JSONObject) parser.parse(req);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reqJSON;
    }

    private void closeClientSocket() {
        try {
            clientSocket.close();
            this.server.clientDisconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String toString(int state) {
        String s = "UnKnown";
        switch (state) {
            case StateCode.QUERY:
                s = "QUERY";
                break;
            case StateCode.ADD:
                s = "ADD";
                break;
            case StateCode.REMOVE:
                s = "REMOVE";
                break;
            case StateCode.ADD_MEANING:
                s = "ADD MEANING";
                break;
            case StateCode.UPDATE:
                s = "UPDATE";
                break;
            default:
                break;
        }
        return s;
    }
}
