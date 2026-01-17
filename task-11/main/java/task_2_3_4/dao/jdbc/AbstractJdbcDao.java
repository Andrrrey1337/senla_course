package task_2_3_4.dao.jdbc;

import task_2_3_4.dao.RowMapper;
import task_2_3_4.db.ConnectionManager;
import task_2_3_4.exceptions.DaoException;
import task_2_3_4.util.constants.ErrorMessages;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public abstract class AbstractJdbcDao<T> {

    protected Connection connection() throws DaoException {
        return ConnectionManager.getInstance().getConnection();
    }

    protected Optional<T> queryOne(String sql, StatementPreparer preparer, RowMapper<T> mapper) throws DaoException {
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            if (preparer != null) preparer.accept(ps);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(mapper.map(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException(ErrorMessages.DAO_FIND_ERROR + e.getMessage(), e);
        }
    }

    protected List<T> queryList(String sql, StatementPreparer preparer, RowMapper<T> mapper) throws DaoException {
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            if (preparer != null) preparer.accept(ps);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(mapper.map(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new DaoException(ErrorMessages.DAO_FIND_ERROR + e.getMessage(), e);
        }
    }

    protected int executeUpdate(String sql, StatementPreparer preparer, String errorPrefix) throws DaoException {
        try (PreparedStatement ps = connection().prepareStatement(sql)) {
            if (preparer != null) preparer.accept(ps);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException(errorPrefix + e.getMessage(), e);
        }
    }
}
