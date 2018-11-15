package models;


import util.SQLConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Event {
    private String title;
    private String location;
    private String date;
    private String time;
    private int groupId;

    public Event(String title, String location, String date, String time, int groupId) {
        this.title = title;
        this.location = location;
        this.date = date;
        this.time = time;
        this.groupId = groupId;
    }

    public static ArrayList<Event> getEvent(int groupId) {
        ArrayList<Event> events = new ArrayList<>();
        SQLConnection sql = new SQLConnection();
        try {
            PreparedStatement statement = sql.prepareStatement(
                    "SELECT * FROM events WHERE group_id=" + groupId
            );
            sql.setStatement(statement);
            sql.executeQuery();
            ResultSet results = sql.getResults();
            while (results.next()) {
                events.add(new Event(
                        results.getString(3),
                        results.getString(4),
                        results.getString(5),
                        results.getString(6),
                        results.getInt(2)
                ));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            sql.close();
        }

        return events;
    }

    public boolean insertEvent() {
        boolean success = true;
        SQLConnection sql = new SQLConnection();
        try {
            PreparedStatement statement = sql.prepareStatement(
                    "INSERT INTO events " +
                            "(group_id, title, location, date, time) " +
                            "VALUES (?, ?, ?, ?, ?)"
            );
            statement.setInt(1, this.groupId);
            statement.setString(2, this.title);
            statement.setString(3, this.location);
            statement.setString(4, this.date);
            statement.setString(5, this.time);

            sql.setStatement(statement);
            sql.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }
        finally {
            sql.close();
        }

        return success;
    }

    @Override
    public String toString() {
        return "Event{" +
                "title='" + title + '\'' +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", groupId=" + groupId +
                '}';
    }

}
