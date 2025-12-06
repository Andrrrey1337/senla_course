package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static final String CONFIG_FILE_PATH = "/hotel.properties";
    private static ConfigManager instance;
    private final Properties properties;

    private ConfigManager() throws IOException {
        properties = new Properties();
        try (InputStream input = ConfigManager.class.getResourceAsStream(CONFIG_FILE_PATH)) {
            if (input == null) {
                throw new IOException("Файл конфигурации не найден: " + CONFIG_FILE_PATH);
            }
            properties.load(input);
        }
    }

    public static ConfigManager getInstance() throws IOException {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public boolean isRoomStatusChangeEnabled() {
        String value = properties.getProperty("room.status.change.enabled", "true");
        return Boolean.parseBoolean(value);
    }

    public int getRoomResidenceHistorySize() {
        String value = properties.getProperty("room.residence.history.size", "3");
        try {
            int size = Integer.parseInt(value);
            if (size < 0) {
                System.err.println("Значение room.residence.history.size не может быть отрицательным, используется значение по умолчанию 3");
                return 3;
            }
            return size;
        } catch (NumberFormatException e) {
            System.err.println("Неверный формат для room.residence.history.size, используется значение по умолчанию 3");
            return 3;
        }
    }
}