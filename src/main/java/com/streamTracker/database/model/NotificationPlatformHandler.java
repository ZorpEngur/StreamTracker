package com.streamTracker.database.model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * Type handler for MyBatis for {@link NotificationPlatform}.
 */
public class NotificationPlatformHandler implements TypeHandler<NotificationPlatform> {

    @Override
    public void setParameter(PreparedStatement ps, int i, NotificationPlatform parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getId());
    }

    @Override
    public NotificationPlatform getResult(ResultSet rs, String columnName) throws SQLException {
        return NotificationPlatform.fromId(rs.getInt(columnName));
    }

    @Override
    public NotificationPlatform getResult(ResultSet rs, int columnIndex) throws SQLException {
        return NotificationPlatform.fromId(rs.getInt(columnIndex));
    }

    @Override
    public NotificationPlatform getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return NotificationPlatform.fromId(cs.getInt(columnIndex));
    }
}
