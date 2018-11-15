package util;

import java.sql.*;

public class SQLConnection {
    private static final String SCHEMA = "scroup";
    private static final String USER = "root";
    private static final String PASSWORD = "Youaremine2303!";

    private Connection connection = null;
    private ResultSet results = null;
    private PreparedStatement statement = null;

    /**
     * Opens connection to database
     */
    public SQLConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(
                String.format(
                    "jdbc:mysql://localhost:3306/%s?user=%s&password=%s&useSSL=false",
                    SCHEMA, USER, PASSWORD
                )
            );
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes database streams
     */
    public void close() {
        try {
            if (this.results != null) {
                this.results.close();
            }
            if (this.connection != null) {
                this.connection.close();
            }
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * Use when getting a data set (SELECT)
     */
    public void executeQuery() throws SQLException {
        this.results = this.statement.executeQuery();
    }

    /**
     * Use when modifying data (UPDATE, CREATE)
     */
    public void executeUpdate() throws SQLException {
        this.statement.executeUpdate();
    }

    public ResultSet getResults() {
        return results;
    }

    /**
     * Prepares a statement for this instance's database connection
     *
     * @param sqlStatement with ? placeholders
     * @return statement which can have variables set to it
     * @throws SQLException Statement may be invalid
     */
    public PreparedStatement prepareStatement(String sqlStatement) throws SQLException {
        return this.connection.prepareStatement(sqlStatement);
    }

    /**
     * Assigns a statement to the statement which is to be used
     * by this.execute()
     *
     * @param statement PreparedStatement to execute
     */
    public void setStatement(PreparedStatement statement) {
        this.statement = statement;
    }
}
