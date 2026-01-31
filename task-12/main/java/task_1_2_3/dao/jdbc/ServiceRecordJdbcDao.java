package task_1_2_3.dao.jdbc;

import task_1_2_3.annotations.Component;
import task_1_2_3.annotations.Singleton;
import task_1_2_3.dao.RowMapper;
import task_1_2_3.dao.ServiceRecordDao;
import task_1_2_3.db.JdbcUtils;
import task_1_2_3.exceptions.DaoException;
import task_1_2_3.model.ServiceRecord;
import task_1_2_3.util.constants.ErrorMessages;
import task_1_2_3.util.constants.SqlConstants;

import java.util.List;
import java.util.Optional;

@Component
@Singleton
public class ServiceRecordJdbcDao extends AbstractJdbcDao<ServiceRecord> implements ServiceRecordDao {

    private static final String SQL_INSERT =
            "INSERT INTO " + SqlConstants.T_SERVICE_RECORDS +
                    " (" + SqlConstants.C_ID + ", " + SqlConstants.C_SR_GUEST_ID + ", " + SqlConstants.C_SR_SERVICE_ID + ", " + SqlConstants.C_SR_DATE + ") " +
                    "VALUES (?, ?, ?, ?)";

    private static final String SQL_SELECT_BY_ID =
            "SELECT " + SqlConstants.C_ID + ", " + SqlConstants.C_SR_GUEST_ID + ", " + SqlConstants.C_SR_SERVICE_ID + ", " + SqlConstants.C_SR_DATE +
                    " FROM " + SqlConstants.T_SERVICE_RECORDS + " WHERE " + SqlConstants.C_ID + "=?";

    private static final String SQL_SELECT_ALL =
            "SELECT " + SqlConstants.C_ID + ", " + SqlConstants.C_SR_GUEST_ID + ", " + SqlConstants.C_SR_SERVICE_ID + ", " + SqlConstants.C_SR_DATE +
                    " FROM " + SqlConstants.T_SERVICE_RECORDS + " ORDER BY " + SqlConstants.C_ID;

    private static final String SQL_SELECT_BY_GUEST =
            "SELECT " + SqlConstants.C_ID + ", " + SqlConstants.C_SR_GUEST_ID + ", " + SqlConstants.C_SR_SERVICE_ID + ", " + SqlConstants.C_SR_DATE +
                    " FROM " + SqlConstants.T_SERVICE_RECORDS + " WHERE " + SqlConstants.C_SR_GUEST_ID + "=? ORDER BY " + SqlConstants.C_SR_DATE;

    private static final String SQL_UPDATE =
            "UPDATE " + SqlConstants.T_SERVICE_RECORDS +
                    " SET " + SqlConstants.C_SR_GUEST_ID + "=?, " + SqlConstants.C_SR_SERVICE_ID + "=?, " + SqlConstants.C_SR_DATE + "=? WHERE " + SqlConstants.C_ID + "=?";

    private static final String SQL_DELETE =
            "DELETE FROM " + SqlConstants.T_SERVICE_RECORDS + " WHERE " + SqlConstants.C_ID + "=?";

    private static final RowMapper<ServiceRecord> MAPPER = rs ->
            new ServiceRecord(
                    rs.getLong(SqlConstants.C_ID),
                    rs.getLong(SqlConstants.C_SR_GUEST_ID),
                    rs.getLong(SqlConstants.C_SR_SERVICE_ID),
                    JdbcUtils.toLocalDate(rs.getDate(SqlConstants.C_SR_DATE))
            );

    @Override
    public ServiceRecord create(ServiceRecord entity) throws DaoException {
        executeUpdate(SQL_INSERT, ps -> {
            ps.setLong(1, entity.getId());
            ps.setLong(2, entity.getGuestId());
            ps.setLong(3, entity.getServiceId());
            ps.setDate(4, JdbcUtils.toSqlDate(entity.getDate()));
        }, ErrorMessages.DAO_CREATE_ERROR);
        return entity;
    }

    @Override
    public Optional<ServiceRecord> findById(Long id) throws DaoException {
        return queryOne(SQL_SELECT_BY_ID, ps -> ps.setLong(1, id), MAPPER);
    }

    @Override
    public List<ServiceRecord> findAll() throws DaoException {
        return queryList(SQL_SELECT_ALL, null, MAPPER);
    }

    @Override
    public List<ServiceRecord> findByGuestId(long guestId) throws DaoException {
        return queryList(SQL_SELECT_BY_GUEST, ps -> ps.setLong(1, guestId), MAPPER);
    }

    @Override
    public ServiceRecord update(ServiceRecord entity) throws DaoException {
        executeUpdate(SQL_UPDATE, ps -> {
            ps.setLong(1, entity.getGuestId());
            ps.setLong(2, entity.getServiceId());
            ps.setDate(3, JdbcUtils.toSqlDate(entity.getDate()));
            ps.setLong(4, entity.getId());
        }, ErrorMessages.DAO_UPDATE_ERROR);
        return entity;
    }

    @Override
    public boolean deleteById(Long id) throws DaoException {
        return executeUpdate(SQL_DELETE, ps -> ps.setLong(1, id), ErrorMessages.DAO_DELETE_ERROR) > 0;
    }
}
