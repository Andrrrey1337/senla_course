package task_1.csv;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CsvManager {

    public static List<List<String>> read(String filePath) {
        List<List<String>> lines = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            while ((line = r.readLine()) != null) {
                lines.add(List.of(line.split(";")));
            }
        }
        catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
        return lines;
    }

    public static void write(String filePath, List<String> lines) {
        try (BufferedWriter w = Files.newBufferedWriter(Path.of(filePath))) {
            for (String s : lines) {
                w.write(s);
                w.newLine();
            }
        }
        catch (IOException e) {
            System.out.println("Ошибка записи файла: " + e.getMessage());
        }
    }
}
