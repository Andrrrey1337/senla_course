package task.service.managers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.dao.ResidenceDao;
import task.exceptions.HotelException;
import task.model.Residence;
import task.util.IdGenerator;
import task.util.constants.BusinessMessages;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
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
        Residence residence = new Residence(idGenerator.next(), guestId, roomId, checkIn, checkOut);
        LOGGER.info("Сохранена история проживания: гость ID={} в комнате ID={} с {} по {}",
                guestId, roomId, checkIn, checkOut);
        return residenceDao.create(residence);
    }

    public List<Residence> getLastByRoom(long roomId) {
        int limit = Math.max(0, maxHistorySize);
        return residenceDao.findLastByRoom(roomId, limit);
    }
}
