package models;

import util.SQLConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

public class StudyGroup {
    private int id;
    private int courseId;
    private int ownerId;
    private int capacity;
    private int size;
    private String location;
    private int topic;
    private String professor;
    private Timestamp start;
    private Timestamp end;
    // joined is not a normalized database entry - it's a generated value by checking the joinedgroups table
    private boolean joined;

    StudyGroup(int id, int courseId, int ownerId, int capacity, int size,
               String location, int topic, String professor,
               Timestamp start, Timestamp end, boolean joined) {
        this.id = id;
        this.courseId = courseId;
        this.ownerId = ownerId;
        this.capacity = capacity;
        this.size = size;
        this.location = location;
        this.topic = topic;
        this.professor = professor;
        this.start = start;
        this.end = end;
        this.joined = joined;
    }

    /**
     * Filters study groups with the following options:
     *      courseId: required exact match
     *      capacityMin: optional capacity >= value
     *      capacityMax: optional capacity <= value
     *      hideFull: optional hide where size == capacity
     *      location: optional LIKE
     *      topic: optional exact match
     *      professor: optional LIKE
     *      after: optional start >= value
     *      before: optional start <= value
     *
     * @param filterParams from http query parameters
     * @param userId used to check if user is joined to the group
     * @return All study groups that math the filter parameters
     */
    public static ArrayList<StudyGroup> dbSelect(Map<String, String> filterParams, int userId) {
        ArrayList<StudyGroup> studyGroups = new ArrayList<>();
        SQLConnection sql = new SQLConnection();

        // Generate the sql filters, mapping the key to the appropriate sql filter type
        ArrayList<String> sqlFilters = new ArrayList<>();
        for (Map.Entry<String, String> entry : filterParams.entrySet()) {
            switch (entry.getKey()) {
                case "capacityMin":
                    sqlFilters.add("capacity >= ?");
                    break;
                case "capacityMax":
                    sqlFilters.add("capacity <= ?");
                    break;
                case "hideFull":
                    sqlFilters.add("size != capacity");
                    break;
                case "location":
                case "professor":
                    sqlFilters.add("LOWER(" + entry.getKey() + ")" + " LIKE LOWER(?)");
                    break;
                case "after":
                    sqlFilters.add("start >= ?");
                    break;
                case "before":
                    sqlFilters.add("start <= ?");
                    break;
                default:
                    sqlFilters.add(entry.getKey() + " = ?");
            }
        }

        try {
            // Join the sql filters with "AND"
            PreparedStatement statement = sql.prepareStatement(
                "SELECT *, " +
                    "(SELECT COUNT(*) FROM joinedgroups WHERE userId=? AND groupId=studygroups.id) AS joined " +
                    "FROM studygroups WHERE " + String.join(" AND ", sqlFilters)
            );

            // Parse the values to the correct type and match to the prepared statement
            statement.setInt(1, userId);
            int i = 2;
            for (Map.Entry<String, String> entry : filterParams.entrySet()) {
                switch (entry.getKey()) {
                    case "location":
                    case "professor":
                        statement.setString(i, entry.getValue());
                        break;
                    case "after":
                    case "before":
                        statement.setTimestamp(i, Timestamp.from(Instant.parse(entry.getValue())));
                        break;
                    case "hideFull":
                        i--;
                        break;
                    default:
                        statement.setInt(i, Integer.parseInt(entry.getValue()));
                }
                i++;
            }

            // Execute the statement and serialize the result set into ArrayList<StudyGroup>
            sql.setStatement(statement);
            sql.executeQuery();
            ResultSet results = sql.getResults();
            while (results.next()) {
                studyGroups.add(
                    new StudyGroup(
                        results.getInt(1),
                        results.getInt(2),
                        results.getInt(3),
                        results.getInt(4),
                        results.getInt(5),
                        results.getString(6),
                        results.getInt(7),
                        results.getString(8),
                        results.getTimestamp(9),
                        results.getTimestamp(10),
                        results.getBoolean(11)
                    )
                );
            }
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
            studyGroups = null;
        }
        finally {
            sql.close();
        }

        return studyGroups;
    }

    /**
     * Creates a study group with the following parameters:
     *      courseId: required fk to associated university course
     *      ownerId: required fk to creator of this group
     *      capacity: optional upper limit of group size
     *      location: optional free fill location
     *      topic: optional serialized topic choices
     *      professor: optional free fill professor name
     *      start: optional start time
     *      end: optional end time
     *
     *  Note the following member variables are not used:
     *      id: pk is automatically generated by id
     *      size: db defaults to 1 (the owner creating this group)
     * @return true if successful
     */
    public boolean dbInsert() {
        boolean success = true;
        SQLConnection sql = new SQLConnection();

        try {
            PreparedStatement statement = sql.prepareStatement(
                "INSERT INTO studygroups " +
                    "(courseId, ownerId, capacity, location, topic, professor, start, end) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );
            statement.setInt(1, this.courseId);
            statement.setInt(2, this.ownerId);
            statement.setInt(3, this.capacity);
            statement.setString(4, this.location);
            statement.setInt(5, this.topic);
            statement.setString(6, this.professor);
            statement.setTimestamp(7, this.start);
            statement.setTimestamp(8, this.end);

            sql.setStatement(statement);
            sql.executeUpdate();
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
            success = false;
        }
        finally {
            sql.close();
        }

        return success;
    }

    /**
     * Allows updating of the following fields:
     *      capacity (student joins or leaves meeting),
     *      topic, professor, start, end
     * @return true if successful
     */
    public boolean dbUpdate() {
        boolean success = true;
        SQLConnection sql = new SQLConnection();

        try {
            PreparedStatement statement = sql.prepareStatement(
                "UPDATE studygroups " +
                    "SET capacity=?, location=?, topic=?, professor=?, start=?, end=? " +
                    "WHERE id=?"
            );

            statement.setInt(1, this.capacity);
            statement.setString(2, this.location);
            statement.setInt(3, this.topic);
            statement.setString(4, this.professor);
            statement.setTimestamp(5, this.start);
            statement.setTimestamp(6, this.end);
            statement.setInt(7, this.id);

            sql.setStatement(statement);
            sql.executeUpdate();
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
            success = false;
        }
        finally {
            sql.close();
        }

        return success;
    }

    /**
     * Deletes study group by id
     *
     * @return true if successful
     */
    public boolean dbDelete() {
        boolean success = true;
        SQLConnection sql = new SQLConnection();

        try {
            PreparedStatement statement = sql.prepareStatement(
                "DELETE FROM studygroups WHERE id=?"
            );

            statement.setInt(1, this.id);
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
            success = false;
        }
        finally {
            sql.close();
        }

        return success;
    }
}
