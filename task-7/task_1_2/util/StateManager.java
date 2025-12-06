package util;

import exceptions.HotelException;
import service.HotelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StateManager {

    private final Path SERIALIZATION_FILE_PATH =
            Paths.get(System.getProperty("user.home"), "hotel_state.ser");

    public void saveState(HotelService hotelService) throws HotelException {
        try (ObjectOutputStream object =
                     new ObjectOutputStream(Files.newOutputStream(SERIALIZATION_FILE_PATH))) {

            object.writeObject(hotelService);
            System.out.println("Состояние программы сохранено в " + SERIALIZATION_FILE_PATH.toAbsolutePath());

        } catch (IOException e) {
            throw new HotelException("Ошибка при сохранении состояния: " + e.getMessage(), e);
        }
    }

    public HotelService loadState() throws HotelException {
        File file = SERIALIZATION_FILE_PATH.toFile();

        if (!file.exists() || file.length() == 0) {
            System.out.println("Файл состояния не найден или он пуст (" + file.getAbsolutePath() + ").");
            HotelService initialService = HotelService.getInstance();
            System.out.println("Создан новый файл состояния");
            saveState(initialService);
            return initialService;
        }

        try (ObjectInputStream object =
                     new ObjectInputStream(Files.newInputStream(SERIALIZATION_FILE_PATH))) {

            HotelService loadedHotelService = (HotelService) object.readObject();
            System.out.println("Состояние программы загружено из " + file.getAbsolutePath());
            return loadedHotelService;

        } catch (IOException | ClassNotFoundException e) {
            throw new HotelException("Ошибка при загрузке состояния: " + e.getMessage(), e);
        }
    }
}
