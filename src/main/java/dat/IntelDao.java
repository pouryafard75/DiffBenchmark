package dat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public class IntelDAO {
    private Connection connection;

    public IntelDAO() throws Exception {
        // Establish database connection
        String url = "jdbc:postgresql://localhost:5432/DAT";
        String username = "pourya";
        String password = "";
        connection = DriverManager.getConnection(url, username, password);
    }

    public void createTable() throws Exception {
        if (true) return;
        Class<?> intelClass = Intel.class; // Assuming your Intel class is named Intel
        Field[] fields = intelClass.getDeclaredFields();

        StringBuilder sql = new StringBuilder("CREATE TABLE intels (id SERIAL PRIMARY KEY, ");

        for (int i = 0; i < fields.length; i++) {
            if (fields[i].isAnnotationPresent(JsonIgnore.class)) {
                // Skip fields annotated with @JsonIgnore
                continue;
            }
            fields[i].setAccessible(true); // Access private fields
            String fieldName = fields[i].getName();
            String fieldType = getSQLType(fields[i].getType());
            sql.append(fieldName).append(" ").append(fieldType);

            if (i < fields.length - 1) {
                sql.append(", ");
            }
        }

        sql.append(")");

        // Execute the SQL statement to create the table
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql.toString());
        statement.close();
    }

    private String getSQLType(Class<?> fieldType) {
        if (fieldType == String.class) {
            return "VARCHAR(255)";
        } else if (fieldType == int.class || fieldType == Integer.class) {
            return "INTEGER";
        } else if (fieldType == long.class || fieldType == Long.class) {
            return "BIGINT";
        } else if (fieldType == float.class || fieldType == Float.class) {
            return "REAL";
        } else if (fieldType == double.class || fieldType == Double.class) {
            return "DOUBLE PRECISION";
        }
        // Add more mappings for other data types as needed
        else {
            // Default to VARCHAR(255) for unknown types
            return "VARCHAR(255)";
        }
    }

    public void insertIntels(List<Intel> intels) throws Exception {
        for (Intel intel : intels) {
            String sql = "INSERT INTO intels (repo, commit, srcPath, matcher, conf, edSize, edSizeNonJavaDoc, " +
                    "trv_mappings, trv_programElements, tp_mappings, fp_mappings, fn_mappings, " +
                    "tp_programElements, fp_programElements, fn_programElements, precision, recall, f1) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            // Execute the INSERT statement
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, intel.getRepo());
            statement.setString(2, intel.getCommit());
            statement.setString(3, intel.getSrcPath());
            statement.setString(4, intel.getMatcher());
            statement.setString(5, intel.getConf());
            statement.setInt(6, intel.getEdSize());
            statement.setInt(7, intel.getEdSizeNonJavaDoc());
            statement.setInt(8, intel.getTrv_mappings());
            statement.setInt(9, intel.getTrv_programElements());
            statement.setInt(10, intel.getTp_mappings());
            statement.setInt(11, intel.getFp_mappings());
            statement.setInt(12, intel.getFn_mappings());
            statement.setInt(13, intel.getTp_programElements());
            statement.setInt(14, intel.getFp_programElements());
            statement.setInt(15, intel.getFn_programElements());

            if (Double.isNaN(intel.getPrecision())) {
                statement.setObject(16, null);
            } else {
                statement.setDouble(16, intel.getPrecision());
            }

            if (Double.isNaN(intel.getRecall())) {
                statement.setObject(17, null);
            } else {
                statement.setDouble(17, intel.getRecall());
            }

            if (Double.isNaN(intel.getF1())) {
                statement.setObject(18, null);
            } else {
                statement.setDouble(18, intel.getF1());
            }


            // Execute the statement
            statement.executeUpdate();
            statement.close();
        }
    }
}
