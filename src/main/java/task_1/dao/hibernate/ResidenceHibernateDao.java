package task_1.dao.hibernate;

import task_1.annotations.Component;
import task_1.annotations.Singleton;
import task_1.dao.ResidenceDao;
import task_1.exceptions.DaoException;
import task_1.model.Residence;

import org.hibernate.query.Query;
import java.util.List;

@Component
@Singleton
public class ResidenceHibernateDao extends AbstractHibernateDao<Residence, Long> implements ResidenceDao {
    public ResidenceHibernateDao() {
        super(Residence.class);
    }

    @Override
    public List<Residence> findLastByRoom(long roomId, int limit) throws DaoException {
        String hql = "from Residence where roomId = :roomId order by checkInDate desc";
        Query<Residence> query = getSession().createQuery(hql, Residence.class);
        query.setParameter("roomId", roomId);
        if (limit > 0) {
            query.setMaxResults(limit);
        }
        return query.list();
    }
}
