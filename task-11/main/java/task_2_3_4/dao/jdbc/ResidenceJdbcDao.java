package task_2_3_4.dao.jdbc;

import task_2_3_4.annotations.Component;
import task_2_3_4.annotations.Singleton;
import task_2_3_4.dao.ResidenceDao;
import task_2_3_4.dao.RowMapper;
import task_2_3_4.db.JdbcUtils;
import task_2_3_4.exceptions.DaoException;
import task_2_3_4.model.Residence;
import task_2_3_4.util.constants.ErrorMessages;
import task_2_3_4.util.constants.SqlConstants;

import java.util.List;
import java.util.Optional;

@Component
@Singleton
public class ResidenceJdbcDao extends AbstractJdbcDao<Residence> implements ResidenceDao {

    private static final String SQL_INSERT =
            "INSERT INTO " + SqlConstants.T_RESIDENCES +
                    " (" + SqlConstants.C_ID + ", " + SqlConstants.C_GUEST_ID + ", " + SqlConstants.C_ROOM_ID + ", " +
                    SqlConstants.C_RES_CHECK_IN_DATE + ", " + SqlConstants.C_RES_CHECK_OUT_DATE + ") VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_BY_ID =
            "SELECT " + SqlConstants.C_ID + ", " + SqlConstants.C_GUEST_ID + ", " + SqlConstants.C_ROOM_ID + ", " +
                    SqlConstants.C_RES_CHECK_IN_DATE + ", " + SqlConstants.C_RES_CHECK_OUT_DATE +
                    " FROM " + SqlConstants.T_RESIDENCES + " WHERE " + SqlConstants.C_ID + " = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT " + SqlConstants.C_ID + ", " + SqlConstants.C_GUEST_ID + ", " + SqlConstants.C_ROOM_ID + ", " +
                    SqlConstants.C_RES_CHECK_IN_DATE + ", " + SqlConstants.C_RES_CHECK_OUT_DATE +
                    " FROM " + SqlConstants.T_RESIDENCES + " ORDER BY " + SqlConstants.C_ID;

    private static final String SQL_SELECT_LAST_BY_ROOM =
            "SELECT " + SqlConstants.C_ID + ", " + SqlConstants.C_GUEST_ID + ", " + SqlConstants.C_ROOM_ID + ", " +
                    SqlConstants.C_RES_CHECK_IN_DATE + ", " + SqlConstants.C_RES_CHECK_OUT_DATE +
                    " FROM " + SqlConstants.T_RESIDENCES +
                    " WHERE " + SqlConstants.C_ROOM_ID + "=? ORDER BY " + SqlConstants.C_RES_CHECK_IN_DATE + " DESC LIMIT ?";

    private static final String SQL_UPDATE =
            "UPDATE " + SqlConstants.T_RESIDENCES +
                    " SET " + SqlConstants.C_GUEST_ID + "=?, " + SqlConstants.C_ROOM_ID + "=?, " +
                    SqlConstants.C_RES_CHECK_IN_DATE + "=?, " + SqlConstants.C_RES_CHECK_OUT_DATE + "=? WHERE " + SqlConstants.C_ID + "=?";

    private static final String SQL_DELETE =
            "DELETE FROM " + SqlConstants.T_RESIDENCES + " WHERE " + SqlConstants.C_ID + "=?";

    private static final RowMapper<Residence> MAPPER = rs ->
            new Residence(
                    rs.getLong(SqlConstants.C_ID),
                    rs.getLong(SqlConstants.C_GUEST_ID),
                    rs.getLong(SqlConstants.C_ROOM_ID),
                    JdbcUtils.toLocalDate(rs.getDate(SqlConstants.C_RES_CHECK_IN_DATE)),
                    JdbcUtils.toLocalDate(rs.getDate(SqlConstants.C_RES_CHECK_OUT_DATE))
            );

    @Override
    public Residence create(Residence entity) throws DaoException {
        executeUpdate(SQL_INSERT, ps -> {
            ps.setLong(1, entity.getId());
            ps.setLong(2, entity.getGuestId());
            ps.setLong(3, entity.getRoomId());
            ps.setDate(4, JdbcUtils.toSqlDate(entity.getCheckInDate()));
            ps.setDate(5, JdbcUtils.toSqlDate(entity.getCheckOutDate()));
        }, ErrorMessages.DAO_CREATE_ERROR);
        return entity;
    }

    @Override
    public Optional<Residence> findById(Long id) throws DaoException {
        return queryOne(SQL_SELECT_BY_ID, ps -> ps.setLong(1, id), MAPPER);
    }

    @Override
    public List<Residence> findAll() throws DaoException {
        return queryList(SQL_SELECT_ALL, null, MAPPER);
    }

    @Override
    public List<Residence> findLastByRoom(long roomId, int limit) throws DaoException {
        return queryList(SQL_SELECT_LAST_BY_ROOM, ps -> {
            ps.setLong(1, roomId);
            ps.setInt(2, limit);
        }, MAPPER);
    }

    @Override
    public Residence update(Residence entity) throws DaoException {
        executeUpdate(SQL_UPDATE, ps -> {
            ps.setLong(1, entity.getGuestId());
            ps.setLong(2, entity.getRoomId());
            ps.setDate(3, JdbcUtils.toSqlDate(entity.getCheckInDate()));
            ps.setDate(4, JdbcUtils.toSqlDate(entity.getCheckOutDate()));
            ps.setLong(5, entity.getId());
        }, ErrorMessages.DAO_UPDATE_ERROR);
        return entity;
    }

    @Override
    public boolean deleteById(Long id) throws DaoException {
        return executeUpdate(SQL_DELETE, ps -> ps.setLong(1, id), ErrorMessages.DAO_DELETE_ERROR) > 0;
    }
}
