package task_2_3_4.dao.jdbc;

import task_2_3_4.annotations.Component;
import task_2_3_4.annotations.Singleton;
import task_2_3_4.dao.RowMapper;
import task_2_3_4.dao.ServiceDao;
import task_2_3_4.exceptions.DaoException;
import task_2_3_4.model.Service;
import task_2_3_4.util.constants.ErrorMessages;
import task_2_3_4.util.constants.SqlConstants;

import java.util.List;
import java.util.Optional;

@Component
@Singleton
public class ServiceJdbcDao extends AbstractJdbcDao<Service> implements ServiceDao {

    private static final String SQL_INSERT =
            "INSERT INTO " + SqlConstants.T_SERVICES +
                    " (" + SqlConstants.C_SERVICE_ID + ", " + SqlConstants.C_SERVICE_NAME + ", " + SqlConstants.C_SERVICE_PRICE + ") VALUES (?, ?, ?)";

    private static final String SQL_SELECT_BY_ID =
            "SELECT " + SqlConstants.C_SERVICE_ID + ", " + SqlConstants.C_SERVICE_NAME + ", " + SqlConstants.C_SERVICE_PRICE +
                    " FROM " + SqlConstants.T_SERVICES + " WHERE " + SqlConstants.C_SERVICE_ID + " = ?";

    private static final String SQL_SELECT_BY_NAME =
            "SELECT " + SqlConstants.C_SERVICE_ID + ", " + SqlConstants.C_SERVICE_NAME + ", " + SqlConstants.C_SERVICE_PRICE +
                    " FROM " + SqlConstants.T_SERVICES + " WHERE " + SqlConstants.C_SERVICE_NAME + " = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT " + SqlConstants.C_SERVICE_ID + ", " + SqlConstants.C_SERVICE_NAME + ", " + SqlConstants.C_SERVICE_PRICE +
                    " FROM " + SqlConstants.T_SERVICES + " ORDER BY " + SqlConstants.C_SERVICE_ID;

    private static final String SQL_UPDATE =
            "UPDATE " + SqlConstants.T_SERVICES +
                    " SET " + SqlConstants.C_SERVICE_NAME + " = ?, " + SqlConstants.C_SERVICE_PRICE + " = ? WHERE " + SqlConstants.C_SERVICE_ID + " = ?";

    private static final String SQL_DELETE =
            "DELETE FROM " + SqlConstants.T_SERVICES + " WHERE " + SqlConstants.C_SERVICE_ID + " = ?";

    private static final RowMapper<Service> MAPPER = rs ->
            new Service(rs.getLong(SqlConstants.C_SERVICE_ID), rs.getString(SqlConstants.C_SERVICE_NAME), rs.getDouble(SqlConstants.C_SERVICE_PRICE));

    @Override
    public Service create(Service entity) throws DaoException {
        executeUpdate(SQL_INSERT, ps -> {
            ps.setLong(1, entity.getId());
            ps.setString(2, entity.getName());
            ps.setDouble(3, entity.getPrice());
        }, ErrorMessages.DAO_CREATE_ERROR);
        return entity;
    }

    @Override
    public Optional<Service> findById(Long id) throws DaoException {
        return queryOne(SQL_SELECT_BY_ID, ps -> ps.setLong(1, id), MAPPER);
    }

    @Override
    public Optional<Service> findByName(String name) throws DaoException {
        return queryOne(SQL_SELECT_BY_NAME, ps -> ps.setString(1, name), MAPPER);
    }

    @Override
    public List<Service> findAll() throws DaoException {
        return queryList(SQL_SELECT_ALL, null, MAPPER);
    }

    @Override
    public Service update(Service entity) throws DaoException {
        executeUpdate(SQL_UPDATE, ps -> {
            ps.setString(1, entity.getName());
            ps.setDouble(2, entity.getPrice());
            ps.setLong(3, entity.getId());
        }, ErrorMessages.DAO_UPDATE_ERROR);
        return entity;
    }

    @Override
    public boolean deleteById(Long id) throws DaoException {
        return executeUpdate(SQL_DELETE, ps -> ps.setLong(1, id), ErrorMessages.DAO_DELETE_ERROR) > 0;
    }
}
