package task.dao.hibernate;

import org.springframework.stereotype.Repository;
import task.dao.ResidenceDao;
import task.exceptions.DaoException;
import task.model.Residence;

import org.hibernate.query.Query;
import java.util.List;

@Repository
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
