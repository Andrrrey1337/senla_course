package task.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvManager {

    public static List<List<String>> read(String filePath) throws IOException {
        List<List<String>> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(List.of(line.split(";")));
            }
        }
        return lines;
    }

    public static void write(String filePath, List<String> lines) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(filePath))) {
            for (String s : lines) {
                writer.write(s);
                writer.newLine();
            }
        }
    }
}
