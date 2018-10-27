package models;

import util.SQLConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private int id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private int year;
    private String major;

    public User(int id, String email, String password, String firstName, String lastName, int year, String major) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.year = year;
        this.major = major;
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static User selectUser(int id) {
        User user = null;
        SQLConnection sql = new SQLConnection();

        try {
            PreparedStatement statement = sql.prepareStatement(
                "SELECT * FROM user WHERE id=?"
            );
            statement.setInt(1, id);

            sql.setStatement(statement);
            sql.executeQuery();
            ResultSet results = sql.getResults();
            if (results.next()) {
                user = new User(
                    results.getInt(1),
                    results.getString(2),
                    results.getString(3),
                    results.getString(4),
                    results.getString(5),
                    results.getInt(6),
                    results.getString(7)
                );
            }
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        finally {
            sql.close();
        }

        return user;
    }
}
