package gg.mineral.practice.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLManager {

    public static ConnectionPoolManager manager;

    public static void initialize(String host, String port, String database, String username, String password) {
        manager = new ConnectionPoolManager(host, port, database, username, password);
    }

    public static void close(AutoCloseable... toClose) {
        if (manager != null)
            manager.close(toClose);
    }

    public static void disconnect() {
        if (manager != null)
            manager.disconnect();
    }

    /**
     * Returns a prepared statement. Values must be manually set after retrieved
     * from this method.
     * 
     * @return an array of [PreparedStatement, Connection], which MUST be closed at
     *         some point
     */
    public static AutoCloseable[] prepare(String statement) {
        try {
            Connection conn = manager.getConnection();
            PreparedStatement ps = conn.prepareStatement(statement);
            return new AutoCloseable[] { ps, conn };
        } catch (Exception e) {
            System.out.println("FAILED TO CONNECT TO DATABASE");
        }
        return null;
    }

    /**
     * Execute any SQL query and cleans up the connection afterwards.
     * 
     * @param ac - an array of [PreparedStatement, Connection]
     * @return true if statement was successfully run, false if an error occurred
     */
    public static boolean execute(AutoCloseable[] ac) {

        if (ac == null || ac.length != 2 || !(ac[0] instanceof PreparedStatement) || !(ac[1] instanceof Connection)) {
            try {
                throw new Exception("Invalid SQL execute() input");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            ((PreparedStatement) ac[0]).execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(ac);
        }
        return true;
    }

    /**
     * Execute an SQL query that returns a ResultSet
     * 
     * @return array of [ResultSet, PreparedStatement, Connection], which MUST be
     *         closed at some point
     */
    public static AutoCloseable[] executeQuery(PreparedStatement statement) {
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = manager.getConnection();
            rs = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new AutoCloseable[] { rs, statement, conn };
    }

}
