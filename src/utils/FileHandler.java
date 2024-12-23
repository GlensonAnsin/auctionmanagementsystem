package utils;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String DATA_DIR = "data";

    static {
        try {
            // Create data directory if it doesn't exist
            File dir = new File(DATA_DIR);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("Failed to create data directory");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to initialize data directory",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void saveToFile(String filename, Object obj) {
        try {
            File file = new File(DATA_DIR, filename);
            // Create parent directories if they don't exist
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(obj);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error saving to file: " + filename + "\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static Object readFromFile(String filename) {
        File file = new File(DATA_DIR, filename);

        // If file doesn't exist, create a new empty ArrayList
        if (!file.exists()) {
            ArrayList<Object> emptyList = new ArrayList<>();
            saveToFile(filename, emptyList);
            return emptyList;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return ois.readObject();
        } catch (InvalidClassException e) {
            // Handle case where file format has changed
            file.delete();
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            // Create backup of corrupted file
            if (file.exists()) {
                File backup = new File(file.getPath() + ".bak");
                file.renameTo(backup);
            }
            return new ArrayList<>();
        }
    }

    public static List<String> readLinesFromFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void writeLinesToFile(String filename, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to ensure data directory exists
    public static void initializeDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                JOptionPane.showMessageDialog(null,
                        "Failed to create data directory",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
