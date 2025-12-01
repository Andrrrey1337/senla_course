package util;

import exceptions.HotelException;
import model.HotelService;
import java.io.*;
import java.nio.file.Paths;

public class StateManager {

    private final String SERIALIZATION_FILE_PATH = StateManager.class.getResource("/hotel_state.ser").getPath();

    public void saveState(HotelService hotelService) throws HotelException {
        try (ObjectOutputStream object = new ObjectOutputStream(new FileOutputStream(SERIALIZATION_FILE_PATH))) {
            object.writeObject(hotelService);
            System.out.println("Состояние программы сохранено в " + Paths.get(SERIALIZATION_FILE_PATH).toAbsolutePath());
        } catch (IOException e) {
            throw new HotelException("Ошибка при сохранении состояния: " + e.getMessage(), e);
        }
    }

    public HotelService loadState() throws HotelException {
        File file = new File(SERIALIZATION_FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            System.out.println("Файл состояния не найден или он пуст (" + file.getAbsolutePath() + "). Создание нового файла с начальным состоянием.");
            HotelService initialService = HotelService.getInstance();
            saveState(initialService);
            System.out.println("Новый файл состояния создан");
            return initialService;
        }

        try (ObjectInputStream object = new ObjectInputStream(new FileInputStream(SERIALIZATION_FILE_PATH))) {
            HotelService loadedHotelService = (HotelService) object.readObject();
            System.out.println("Состояние программы загружено из " + file.getAbsolutePath());

            return loadedHotelService;
        } catch (IOException | ClassNotFoundException e) {
            throw new HotelException("Ошибка при загрузке состояния: " + e.getMessage(), e);
        }
    }
}