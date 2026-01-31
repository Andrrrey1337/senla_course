package task_1_2_3.dao.jdbc;

import task_1_2_3.annotations.Component;
import task_1_2_3.annotations.Singleton;
import task_1_2_3.dao.RoomDao;
import task_1_2_3.dao.RowMapper;
import task_1_2_3.db.JdbcUtils;
import task_1_2_3.exceptions.DaoException;
import task_1_2_3.model.Guest;
import task_1_2_3.model.Room;
import task_1_2_3.model.RoomStatus;
import task_1_2_3.util.constants.CommonConstants;
import task_1_2_3.util.constants.DbConstants;
import task_1_2_3.util.constants.ErrorMessages;
import task_1_2_3.util.constants.SqlConstants;

import java.util.List;
import java.util.Optional;


@Component
@Singleton
public class RoomJdbcDao extends AbstractJdbcDao<Room> implements RoomDao {

    private static final String SQL_INSERT =
            "INSERT INTO " + SqlConstants.T_ROOMS +
                    " (" + SqlConstants.C_ID + ", " + SqlConstants.C_NUMBER + ", " + SqlConstants.C_CAPACITY + ", " +
                    SqlConstants.C_STARS + ", " + SqlConstants.C_PRICE + ", " + SqlConstants.C_STATUS + ", " +
                    SqlConstants.C_CHECK_IN_DATE + ", " + SqlConstants.C_CHECK_OUT_DATE + ") " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_JOIN_BASE =
            "SELECT r." + SqlConstants.C_ID + ", r." + SqlConstants.C_NUMBER + ", r." + SqlConstants.C_CAPACITY + ", " +
                    "r." + SqlConstants.C_STARS + ", r." + SqlConstants.C_PRICE + ", r." + SqlConstants.C_STATUS + ", " +
                    "r." + SqlConstants.C_CHECK_IN_DATE + ", r." + SqlConstants.C_CHECK_OUT_DATE + ", " +
                    "g." + SqlConstants.C_ID + " AS " + DbConstants.ALIAS_GUEST_ID + ", g." + SqlConstants.C_NAME + " AS " + DbConstants.ALIAS_GUEST_NAME + " " +
                    "FROM " + SqlConstants.T_ROOMS + " r " +
                    "LEFT JOIN " + SqlConstants.T_RESIDENCES + " res " +
                    "ON res." + SqlConstants.C_ROOM_ID + " = r." + SqlConstants.C_ID + " " +
                    "AND res." + SqlConstants.C_RES_CHECK_IN_DATE + " = r." + SqlConstants.C_CHECK_IN_DATE + " " +
                    "AND res." + SqlConstants.C_RES_CHECK_OUT_DATE + " = r." + SqlConstants.C_CHECK_OUT_DATE + " " +
                    "LEFT JOIN " + SqlConstants.T_GUESTS + " g ON g." + SqlConstants.C_ID + " = res." + SqlConstants.C_GUEST_ID + " ";

    private static final String SQL_SELECT_BY_ID = SQL_SELECT_JOIN_BASE + "WHERE r." + SqlConstants.C_ID + " = ?";
    private static final String SQL_SELECT_BY_NUMBER = SQL_SELECT_JOIN_BASE + "WHERE r." + SqlConstants.C_NUMBER + " = ?";
    private static final String SQL_SELECT_ALL = SQL_SELECT_JOIN_BASE + "ORDER BY r." + SqlConstants.C_NUMBER;

    private static final String SQL_UPDATE =
            "UPDATE " + SqlConstants.T_ROOMS + " SET " +
                    SqlConstants.C_NUMBER + "=?, " +
                    SqlConstants.C_CAPACITY + "=?, " +
                    SqlConstants.C_STARS + "=?, " +
                    SqlConstants.C_PRICE + "=?, " +
                    SqlConstants.C_STATUS + "=?, " +
                    SqlConstants.C_CHECK_IN_DATE + "=?, " +
                    SqlConstants.C_CHECK_OUT_DATE + "=? " +
                    "WHERE " + SqlConstants.C_ID + "=?";

    private static final String SQL_DELETE =
            "DELETE FROM " + SqlConstants.T_ROOMS + " WHERE " + SqlConstants.C_ID + "=?";

    private static final RowMapper<Room> MAPPER = rs -> {
        Room room = new Room(
                rs.getLong(SqlConstants.C_ID),
                rs.getInt(SqlConstants.C_NUMBER),
                rs.getDouble(SqlConstants.C_PRICE),
                rs.getInt(SqlConstants.C_CAPACITY),
                rs.getInt(SqlConstants.C_STARS),
                RoomStatus.valueOf(rs.getString(SqlConstants.C_STATUS)),
                JdbcUtils.toLocalDate(rs.getDate(SqlConstants.C_CHECK_IN_DATE)),
                JdbcUtils.toLocalDate(rs.getDate(SqlConstants.C_CHECK_OUT_DATE))
        );

        long guestId = rs.getLong(DbConstants.ALIAS_GUEST_ID);
        if (!rs.wasNull()) {
            String guestName = rs.getString(DbConstants.ALIAS_GUEST_NAME);
            if (guestName == null) guestName = CommonConstants.EMPTY_STRING;
            room.setGuest(new Guest(guestId, guestName));
        }
        return room;
    };

    @Override
    public Room create(Room entity) throws DaoException {
        executeUpdate(SQL_INSERT, ps -> {
            ps.setLong(1, entity.getId());
            ps.setInt(2, entity.getNumber());
            ps.setInt(3, entity.getCapacity());
            ps.setInt(4, entity.getStars());
            ps.setDouble(5, entity.getPrice());
            ps.setString(6, entity.getStatus().name());
            ps.setDate(7, JdbcUtils.toSqlDate(entity.getCheckInDate()));
            ps.setDate(8, JdbcUtils.toSqlDate(entity.getCheckOutDate()));
        }, ErrorMessages.DAO_CREATE_ERROR);
        return entity;
    }

    @Override
    public Optional<Room> findById(Long id) throws DaoException {
        return queryOne(SQL_SELECT_BY_ID, ps -> ps.setLong(1, id), MAPPER);
    }

    @Override
    public Optional<Room> findByNumber(int number) throws DaoException {
        return queryOne(SQL_SELECT_BY_NUMBER, ps -> ps.setInt(1, number), MAPPER);
    }

    @Override
    public List<Room> findAll() throws DaoException {
        return queryList(SQL_SELECT_ALL, null, MAPPER);
    }

    @Override
    public Room update(Room entity) throws DaoException {
        executeUpdate(SQL_UPDATE, ps -> {
            ps.setInt(1, entity.getNumber());
            ps.setInt(2, entity.getCapacity());
            ps.setInt(3, entity.getStars());
            ps.setDouble(4, entity.getPrice());
            ps.setString(5, entity.getStatus().name());
            ps.setDate(6, JdbcUtils.toSqlDate(entity.getCheckInDate()));
            ps.setDate(7, JdbcUtils.toSqlDate(entity.getCheckOutDate()));
            ps.setLong(8, entity.getId());
        }, ErrorMessages.DAO_UPDATE_ERROR);
        return entity;
    }

    @Override
    public boolean deleteById(Long id) throws DaoException {
        return executeUpdate(SQL_DELETE, ps -> ps.setLong(1, id), ErrorMessages.DAO_DELETE_ERROR) > 0;
    }
}
