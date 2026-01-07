package task_2_3_4.config;

import task_2_3_4.annotations.ConfigProperty;
import task_2_3_4.annotations.ConfigType;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigManager {
    private static final Map<String, Properties> propertiesMap = new HashMap<>();

    public static void config(Object target) {
        Class<?> clas = target.getClass();
        for (Field field : clas.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                try {
                    inject(target, field);
                } catch (Exception e) {
                    System.err.println("Ошибка конфигурирования поля " + field.getName() + " в классе " + clas.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    private static void inject(Object target, Field field) throws Exception {
        ConfigProperty annotation = field.getAnnotation(ConfigProperty.class);
        String fileName = annotation.configFileName();
        String key = annotation.propertyName().trim();
        Properties properties = getProperties(fileName);
        String value = null;
        for (String propertyName : properties.stringPropertyNames()) {
            if (propertyName.trim().equals(key)) {
                value = properties.getProperty(propertyName);
                break;
            }
        }
        if (value == null) {
            throw new RuntimeException("Ключ '" + key + "' не найден в файле " + fileName);
        }
        Object convertedValue = convert(value.trim(), field.getType(), annotation.type());
        field.setAccessible(true);
        field.set(target, convertedValue);
    }

    private static Properties getProperties(String fileName) throws Exception {
        if (propertiesMap.containsKey(fileName)) {
            return propertiesMap.get(fileName);
        }
        Properties properties = new Properties();

        try {
            InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(fileName);
            properties.load(input);
            System.out.println("Файл с конфигурациями загружен");
        } catch (java.io.FileNotFoundException e) {
            throw new RuntimeException("Файл конфигурации не найден", e);
        }
        propertiesMap.put(fileName, properties);
        return properties;
    }

    private static Object convert(String value, Class<?> fieldType, ConfigType configType) {
        if (configType == ConfigType.STRING || fieldType == String.class) {
            return value;
        }

        if (configType == ConfigType.INT || fieldType == int.class || fieldType == Integer.class) {
            return Integer.parseInt(value);
        }

        if (configType == ConfigType.BOOLEAN || fieldType == boolean.class || fieldType == Boolean.class) {
            return Boolean.parseBoolean(value);
        }

        return value;
    }
}