package task_2_3_4.service.managers;

import task_2_3_4.annotations.*;
import task_2_3_4.dao.ResidenceDao;
import task_2_3_4.exceptions.DaoException;
import task_2_3_4.exceptions.HotelException;
import task_2_3_4.model.Residence;
import task_2_3_4.util.IdGenerator;
import task_2_3_4.util.constants.BusinessMessages;
import task_2_3_4.util.constants.ConfigConstants;

import java.time.LocalDate;
import java.util.List;

@Component
@Singleton
public class ResidenceManager {

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
            return List.of();
        }
    }
}
