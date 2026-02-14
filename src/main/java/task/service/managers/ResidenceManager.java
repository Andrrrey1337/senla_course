package task.service.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import task.dao.ResidenceDao;
import task.db.ConnectionManager;
import task.exceptions.DaoException;
import task.exceptions.HotelException;
import task.model.Residence;
import task.util.IdGenerator;
import task.util.constants.BusinessMessages;

import java.time.LocalDate;
import java.util.List;

@Service
public class ResidenceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResidenceManager.class);

    private final int maxHistorySize;
    private final IdGenerator idGenerator;
    private final ResidenceDao residenceDao;

    public ResidenceManager(@Value("${room.residence.history.size}") int maxHistorySize,
                            IdGenerator idGenerator, ResidenceDao residenceDao) {
        this.maxHistorySize = maxHistorySize;
        this.idGenerator = idGenerator;
        this.residenceDao = residenceDao;
    }

    public Residence createResidence(long guestId, long roomId, LocalDate checkIn, LocalDate checkOut) throws HotelException {
        if (checkIn != null && checkOut != null && checkIn.isAfter(checkOut)) {
            throw new HotelException(BusinessMessages.RESIDENCE_CHECKIN_AFTER_CHECKOUT);
        }
        try {
            ConnectionManager.getInstance().beginTransaction();

            Residence residence = new Residence(idGenerator.next(), guestId, roomId, checkIn, checkOut);
            Residence created = residenceDao.create(residence);

            ConnectionManager.getInstance().commitTransaction();
            return created;
        } catch (DaoException e) {
            rollbackQuietly();
            throw new HotelException(e.getMessage(), e);
        }
    }

    public List<Residence> getLastByRoom(long roomId) {
        try {
            int limit = Math.max(0, maxHistorySize);
            return residenceDao.findLastByRoom(roomId, limit);
        } catch (DaoException e) {
            LOGGER.error("Ошибка при получении истории заселений для комнаты id={}: {}", roomId, e.getMessage(), e);
            return List.of();
        }
    }

    private void rollbackQuietly() {
        try {
            ConnectionManager.getInstance().rollbackTransaction();
        } catch (DaoException e) {
            LOGGER.error("Не удалось выполнить откат транзакции: {}", e.getMessage());
        }
    }
}
