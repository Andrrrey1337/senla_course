package task_1.dao.hibernate;

import task_1.annotations.Component;
import task_1.annotations.Singleton;
import task_1.dao.RoomDao;
import task_1.exceptions.DaoException;
import task_1.model.Room;

import org.hibernate.query.Query;
import java.util.Optional;


@Component
@Singleton
public class RoomHibernateDao extends AbstractHibernateDao<Room, Long> implements RoomDao {
    public RoomHibernateDao() {
        super(Room.class);
    }

    @Override
    public Optional<Room> findByNumber(int number) throws DaoException {
        String hql = "from Room r left join fetch r.guest where r.number = :number";
        Query<Room> query = getSession().createQuery(hql, Room.class);
        query.setParameter("number", number);
        return query.uniqueResultOptional();
    }
}
