package task.dao.hibernate;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import task.dao.RoomDao;
import task.exceptions.DaoException;
import task.model.Room;

import org.hibernate.query.Query;
import java.util.Optional;


@Repository
public class RoomHibernateDao extends AbstractHibernateDao<Room, Long> implements RoomDao {
    public RoomHibernateDao(SessionFactory sessionFactory) {
        super(Room.class, sessionFactory);
    }

    @Override
    public Optional<Room> findByNumber(int number) throws DaoException {
        String hql = "from Room r left join fetch r.guest where r.number = :number";
        Query<Room> query = getSession().createQuery(hql, Room.class);
        query.setParameter("number", number);
        return query.uniqueResultOptional();
    }
}
