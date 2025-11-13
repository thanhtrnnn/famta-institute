package com.famta.util;

import com.famta.database.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * Generates sequential identifiers with a fixed prefix by inspecting the
 * current maximum value stored in the database. This keeps ID generation
 * consistent across different modules without relying on auto-increment
 * columns.
 */
public final class SequentialIdGenerator {

    private SequentialIdGenerator() {
    }

    /**
     * Computes the next identifier for the given table and column.
     *
     * @param tableName  database table that stores the identifier
     * @param columnName column inside {@code tableName}
     * @param prefix     non-numeric prefix, e.g. {@code HS} or {@code GV}
     * @param width      number of digits to pad after the prefix
     * @return generated identifier such as {@code HS00000007}
     */
    public static String nextId(String tableName, String columnName, String prefix, int width) {
        String sql = "SELECT MAX(" + columnName + ") AS MaxId FROM " + tableName +
            " WHERE " + columnName + " LIKE ?";
        Connection connection = DatabaseManager.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, prefix + "%");
            try (ResultSet resultSet = statement.executeQuery()) {
                int nextNumber = 1;
                if (resultSet.next()) {
                    String current = resultSet.getString("MaxId");
                    if (current != null && current.startsWith(prefix)) {
                        String numericPart = current.substring(prefix.length());
                        try {
                            int value = Integer.parseInt(numericPart);
                            nextNumber = Math.max(value + 1, 1);
                        } catch (NumberFormatException ignored) {
                            // Fallback to default nextNumber = 1 when parsing fails
                        }
                    }
                }
                String format = "%0" + width + "d";
                return prefix + String.format(Locale.US, format, nextNumber);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Không thể sinh mã mới cho bảng " + tableName, ex);
        }
    }
}
