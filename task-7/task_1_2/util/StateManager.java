package task_1.util;

import task_1.exceptions.HotelException;
import task_1.model.Admin; // Импортируем Admin
import java.io.*;
import java.nio.file.Paths;

public class StateManager {

    private static final String SERIALIZATION_FILE_PATH = "task-7/task_1/resources/hotel_state.ser";

    public static void saveState(Admin admin) throws HotelException {
        try (ObjectOutputStream object = new ObjectOutputStream(new FileOutputStream(SERIALIZATION_FILE_PATH))) {
            object.writeObject(admin);
            System.out.println("Состояние программы сохранено в " + Paths.get(SERIALIZATION_FILE_PATH).toAbsolutePath());
        } catch (IOException e) {
            throw new HotelException("Ошибка при сохранении состояния: " + e.getMessage(), e);
        }
    }

    public static Admin loadState() throws HotelException {
        File file = new File(SERIALIZATION_FILE_PATH);
        if (!file.exists()) {
            System.out.println("Файл состояния не найден (" + file.getAbsolutePath() + "). Инициализация новой системы");
            return null;
        }

        try (ObjectInputStream object = new ObjectInputStream(new FileInputStream(SERIALIZATION_FILE_PATH))) {
            Admin loadedAdmin = (Admin) object.readObject();
            System.out.println("Состояние программы загружено из " + file.getAbsolutePath());

            return loadedAdmin;
        } catch (IOException | ClassNotFoundException e) {
            throw new HotelException("Ошибка при загрузке состояния: " + e.getMessage(), e);
        }
    }
}