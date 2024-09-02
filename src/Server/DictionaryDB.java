///**
// * @author Zhuhan Qin, 988039
// */
//package Server;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.util.HashMap;
//
//public class DictionaryDB {
//    private String path = "dictionary.dat";
//    private HashMap<String, String> dictMap;
//
//    public DictionaryDB(String dictPath) {
//        path = dictPath;
//        try {
//            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
//            dictMap = (HashMap<String, String>) ois.readObject();
//            ois.close();
//        } catch (ClassNotFoundException e) {
//            System.out.println("Error: Wrong file format! Run default dictionary.");
//            setDefaultDict();
//        } catch (FileNotFoundException e) {
//            System.out.println("Error: No such file! Run default dictionary.");
//            setDefaultDict();
//        } catch (Exception e) {
//            System.out.println("Error: Unknown error, " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    public String getPath() {
//        return path;
//    }
//
//    private void setDefaultDict() {
//        path = "dictionary.dat";
//        try {
//            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
//            dictMap = (HashMap<String, String>) ois.readObject();
//            ois.close();
//        } catch (FileNotFoundException e) {
//            System.out.println("Default Dictionary not Exist, Create a new one.");
//            createNewDict(this.path);
//        } catch (Exception e) {
//            System.out.println("Error: Unknown error, " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private void createNewDict(String dictPath) {
//        dictMap = new HashMap<String, String>();
//        try {
//            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dictPath));
//            oos.writeObject(dictMap);
//            oos.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public synchronized boolean isWordExist(String word) {
//        return dictMap.containsKey(word);
//    }
//
//    public synchronized String query(String word) {
//        return dictMap.get(word);
//    }
//
//    public synchronized boolean add(String word, String meaning) {
//        if (dictMap.containsKey(word)) {
//            return false;
//        } else {
//            dictMap.put(word, meaning);
//            try {
//                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
//                oos.writeObject(dictMap);
//                oos.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return true;
//        }
//    }
//
//
//    public synchronized boolean addMeaning(String word, String meaning) {
//        if (dictMap.containsKey(word)) {
//            String existingMeaning = dictMap.get(word);
//            if (existingMeaning.contains(meaning)) {
//                return false; // Meaning already exists
//            } else {
//                dictMap.put(word, existingMeaning + "\n" + meaning); // Add new meaning with newline
//                try {
//                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
//                    oos.writeObject(dictMap);
//                    oos.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return true;
//            }
//        }
//        return false; // Word not found
//    }
//
//
//
//    public synchronized boolean update(String word, String newMeaning) {
//        if (dictMap.containsKey(word)) {
//            dictMap.put(word, newMeaning);
//            try {
//                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
//                    oos.writeObject(dictMap);
//                    oos.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return true;
//            }
//        return false;
//        }
//
//
//    public synchronized boolean remove(String word) {
//        if (dictMap.containsKey(word)) {
//            dictMap.remove(word);
//            try {
//                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
//                oos.writeObject(dictMap);
//                oos.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return true;
//        } else {
//            return false;
//        }
//    }
//}
//




package Server;

import java.io.*;
import java.util.HashMap;

public class DictionaryDB {
    private String path = "dictionary.dat";
    private HashMap<String, String> dictMap;

    public DictionaryDB(String dictPath) {
        this.path = dictPath;
        loadDictionary();
    }

    public String getPath() {
        return path;
    }

    private void loadDictionary() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            dictMap = (HashMap<String, String>) ois.readObject();
        } catch (ClassNotFoundException | FileNotFoundException e) {
            handleLoadError(e);
        } catch (IOException e) {
            System.out.println("Error: Unknown error, " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleLoadError(Exception e) {
        if (e instanceof ClassNotFoundException) {
            System.out.println("Error: Wrong file format! Run default dictionary.");
        } else if (e instanceof FileNotFoundException) {
            System.out.println("Error: No such file! Run default dictionary.");
        }
        setDefaultDict();
    }

    private void setDefaultDict() {
        path = "dictionary.dat";
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            dictMap = (HashMap<String, String>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Default Dictionary not Exist, Create a new one.");
            createNewDict();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error: Unknown error, " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createNewDict() {
        dictMap = new HashMap<>();
        saveDictionary();
    }

    private void saveDictionary() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(dictMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized boolean isWordExist(String word) {
        return dictMap.containsKey(word);
    }

    public synchronized String query(String word) {
        return dictMap.get(word);
    }

    public synchronized boolean add(String word, String meaning) {
        if (dictMap.containsKey(word)) {
            return false;
        }
        dictMap.put(word, meaning);
        saveDictionary();
        return true;
    }

    public synchronized boolean addMeaning(String word, String meaning) {
        if (dictMap.containsKey(word)) {
            String existingMeaning = dictMap.get(word);
            if (existingMeaning.contains(meaning)) {
                return false; // Meaning already exists
            }
            dictMap.put(word, existingMeaning + "\n" + meaning); // Add new meaning with newline
            saveDictionary();
            return true;
        }
        return false; // Word not found
    }

    public synchronized boolean update(String word, String newMeaning) {
        if (dictMap.containsKey(word)) {
            dictMap.put(word, newMeaning);
            saveDictionary();
            return true;
        }
        return false;
    }

    public synchronized boolean remove(String word) {
        if (dictMap.containsKey(word)) {
            dictMap.remove(word);
            saveDictionary();
            return true;
        }
        return false;
    }
}