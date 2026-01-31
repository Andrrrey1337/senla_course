package task_1_2_3.dao.jdbc;

import task_1_2_3.annotations.Component;
import task_1_2_3.annotations.Singleton;
import task_1_2_3.dao.GuestDao;
import task_1_2_3.dao.RowMapper;
import task_1_2_3.exceptions.DaoException;
import task_1_2_3.model.Guest;
import task_1_2_3.util.constants.ErrorMessages;
import task_1_2_3.util.constants.SqlConstants;

import java.util.List;
import java.util.Optional;

@Component
@Singleton
public class GuestJdbcDao extends AbstractJdbcDao<Guest> implements GuestDao {

    private static final String SQL_INSERT =
            "INSERT INTO " + SqlConstants.T_GUESTS + " (" + SqlConstants.C_ID + ", " + SqlConstants.C_NAME + ") VALUES (?, ?)";

    private static final String SQL_SELECT_BY_ID =
            "SELECT " + SqlConstants.C_ID + ", " + SqlConstants.C_NAME +
                    " FROM " + SqlConstants.T_GUESTS + " WHERE " + SqlConstants.C_ID + " = ?";

    private static final String SQL_SELECT_BY_NAME =
            "SELECT " + SqlConstants.C_ID + ", " + SqlConstants.C_NAME +
                    " FROM " + SqlConstants.T_GUESTS + " WHERE " + SqlConstants.C_NAME + " = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT " + SqlConstants.C_ID + ", " + SqlConstants.C_NAME +
                    " FROM " + SqlConstants.T_GUESTS + " ORDER BY " + SqlConstants.C_ID;

    private static final String SQL_UPDATE =
            "UPDATE " + SqlConstants.T_GUESTS + " SET " + SqlConstants.C_NAME + " = ? WHERE " + SqlConstants.C_ID + " = ?";

    private static final String SQL_DELETE =
            "DELETE FROM " + SqlConstants.T_GUESTS + " WHERE " + SqlConstants.C_ID + " = ?";

    private static final RowMapper<Guest> MAPPER = rs ->
            new Guest(rs.getLong(SqlConstants.C_ID), rs.getString(SqlConstants.C_NAME));

    @Override
    public Guest create(Guest entity) throws DaoException {
        executeUpdate(SQL_INSERT, ps -> {
            ps.setLong(1, entity.getId());
            ps.setString(2, entity.getName());
        }, ErrorMessages.DAO_CREATE_ERROR);
        return entity;
    }

    @Override
    public Optional<Guest> findById(Long id) throws DaoException {
        return queryOne(SQL_SELECT_BY_ID, ps -> ps.setLong(1, id), MAPPER);
    }

    @Override
    public Optional<Guest> findByName(String name) throws DaoException {
        return queryOne(SQL_SELECT_BY_NAME, ps -> ps.setString(1, name), MAPPER);
    }

    @Override
    public List<Guest> findAll() throws DaoException {
        return queryList(SQL_SELECT_ALL, null, MAPPER);
    }

    @Override
    public Guest update(Guest entity) throws DaoException {
        executeUpdate(SQL_UPDATE, ps -> {
            ps.setString(1, entity.getName());
            ps.setLong(2, entity.getId());
        }, ErrorMessages.DAO_UPDATE_ERROR);
        return entity;
    }

    @Override
    public boolean deleteById(Long id) throws DaoException {
        return executeUpdate(SQL_DELETE, ps -> ps.setLong(1, id), ErrorMessages.DAO_DELETE_ERROR) > 0;
    }
}
