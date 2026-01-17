package task_1_2_3.service.managers;

import task_1_2_3.annotations.*;
import task_1_2_3.dao.ResidenceDao;
import task_1_2_3.exceptions.DaoException;
import task_1_2_3.exceptions.HotelException;
import task_1_2_3.model.Residence;
import task_1_2_3.util.IdGenerator;
import task_1_2_3.util.constants.BusinessMessages;
import task_1_2_3.util.constants.ConfigConstants;

import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@Singleton
public class ResidenceManager {
    private static final Logger logger = LoggerFactory.getLogger(ResidenceManager.class);

    @ConfigProperty(propertyName = ConfigConstants.ROOM_RESIDENCE_HISTORY_SIZE, type = ConfigType.INT)
    private int maxHistorySize;

    @Inject
    private IdGenerator idGenerator;

    @Inject
    private ResidenceDao residenceDao;

    public Residence createResidence(long guestId, long roomId, LocalDate checkIn, LocalDate checkOut) throws HotelException {
        if (checkIn != null && checkOut != null && checkIn.isAfter(checkOut)) {
            throw new HotelException(BusinessMessages.RESIDENCE_CHECKIN_AFTER_CHECKOUT);
        }
        try {
            Residence residence = new Residence(idGenerator.next(), guestId, roomId, checkIn, checkOut);
            return residenceDao.create(residence);
        } catch (DaoException e) {
            throw new HotelException(e.getMessage(), e);
        }
    }

    public List<Residence> getLastByRoom(long roomId) {
        try {
            int limit = Math.max(0, maxHistorySize);
            return residenceDao.findLastByRoom(roomId, limit);
        } catch (DaoException e) {
            logger.error("Ошибка при получении истории заселений для комнаты id={}: {}", roomId, e.getMessage(), e);
            return List.of();
        }
    }
}
