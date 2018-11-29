package models;

import util.SQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

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
    // from JOIN
    private String ownerName;

    StudyGroup(int id, int courseId, int ownerId, int capacity, int size,
               String location, int topic, String professor,
               Timestamp start, Timestamp end, boolean joined, String ownerName) {
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
        this.ownerName = ownerName;
    }

    public int getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public int getCourseId() {
        return courseId;
    }

    public int getSize() {
        return size;
    }

    public int getTopic() {
        return topic;
    }

    public String getLocation() {
        return location;
    }

    public String getProfessor() {
        return professor;
    }

    public Timestamp getEnd() {
        return end;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * Filters study groups with the following options:
     * courseId: required exact match
     * capacityMin: optional capacity >= value
     * capacityMax: optional capacity <= value
     * hideFull: optional hide where size == capacity
     * location: optional LIKE
     * topic: optional exact match
     * professor: optional LIKE
     * afterHour: optional start's hour >= value
     * beforeHour: optional start's hour <= value
     *
     * @param filterParams from http query parameters
     * @param userId       used to check if user is joined to the group
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
                    sqlFilters.add("(capacity >= ? OR capacity = 0)");
                    break;
                case "capacityMax":
                    sqlFilters.add("(capacity <= ? AND capacity != 0)");
                    break;
                case "hideFull":
                    if (entry.getValue().equalsIgnoreCase("true")) {
                        sqlFilters.add("size != capacity");
                    }
                    break;
                case "location":
                case "professor":
                    sqlFilters.add("LOWER(" + entry.getKey() + ")" + " LIKE LOWER(?)");
                    break;
                case "afterTime":
                    sqlFilters.add("STRCMP(TIME(CONVERT_TZ(start, @@session.time_zone, ?)), ?) >= 0");
                    break;
                case "beforeTime":
                    sqlFilters.add("STRCMP(TIME(CONVERT_TZ(start, @@session.time_zone, ?)), ?) <= 0");
                    break;
                case "timeZone":
                    break;
                default:
                    sqlFilters.add(entry.getKey() + " = ?");
            }
        }

        try {
            // Join the sql filters with "AND"
            PreparedStatement statement = sql.prepareStatement(
                "SELECT s.id, s.courseId, s.ownerId, s.capacity, s.size, s.location, s.topic, s.professor, s.start, s.end, " +
                    "(SELECT COUNT(*) FROM joinedgroups WHERE userId=? AND groupId=s.id) AS joined, " +
                    "CONCAT(u.firstName, ' ', u.lastName) AS ownerName " +
                    "FROM studygroups AS s " +
                    "JOIN users AS u ON s.ownerId = u.id " +
                    "WHERE " + String.join(" AND ", sqlFilters) + " " +
                    "ORDER BY start, topic, ownerName, id"
            );

            // Parse the values to the correct type and match to the prepared statement
            statement.setInt(1, userId);
            int i = 2;
            for (Map.Entry<String, String> entry : filterParams.entrySet()) {
                switch (entry.getKey()) {
                    case "location":
                    case "professor":
                        statement.setString(i, "%" + entry.getValue() + "%");
                        break;
                    case "afterTime":
                    case "beforeTime":
                        statement.setString(i, filterParams.get("timeZone"));
                        statement.setString(++i, entry.getValue());
                        break;
                    case "hideFull":
                    case "timeZone":
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
                        results.getBoolean(11),
                        results.getString(12)
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

    public static int dbSelectOwnerId(int groupId) {
        int ownerId = -1;
        SQLConnection sql = new SQLConnection();

        try {
            PreparedStatement statement = sql.prepareStatement(
                "SELECT ownerId FROM studygroups WHERE id=?"
            );

            statement.setInt(1, groupId);
            sql.setStatement(statement);
            sql.executeQuery();
            ResultSet results = sql.getResults();
            if (results.next()) {
                ownerId = results.getInt(1);
            }
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
        }
        finally {
            sql.close();
        }

        return ownerId;
    }

    /**
     * Creates a study group with the following parameters:
     * courseId: required fk to associated university course
     * ownerId: required fk to creator of this group
     * capacity: optional upper limit of group size
     * location: optional free fill location
     * topic: optional serialized topic choices
     * professor: optional free fill professor name
     * start: optional start time
     * end: optional end time
     * <p>
     * Note the following member variables are not used:
     * id: pk is automatically generated by id
     * size: db defaults to 1 (the owner creating this group)
     *
     * @return true if successful
     */
    public boolean dbInsert() {
        boolean success = true;
        SQLConnection sql = new SQLConnection();

        try {
            PreparedStatement statement = sql.prepareStatement(
                "INSERT INTO studygroups " +
                    "(courseId, ownerId, capacity, location, topic, professor, start, end) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
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

            // After creating the study group join the owner to the group
            ResultSet results = statement.getGeneratedKeys();
            if (results.next()) {
                int groupId = results.getInt(1);
                JoinedGroup joinedGroup = new JoinedGroup(this.ownerId, groupId);
                joinedGroup.dbInsert();
            }
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
     * capacity, topic, professor, start, end
     *
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
            // 1. Unjoin all users in the study group
            PreparedStatement unjoinUsers = sql.prepareStatement(
                "DELETE FROM joinedgroups WHERE groupId=?"
            );
            unjoinUsers.setInt(1, this.id);
            sql.setStatement(unjoinUsers);
            sql.executeUpdate();

            // 2. Delete the study group
            PreparedStatement deleteStudyGroup = sql.prepareStatement(
                "DELETE FROM studygroups WHERE id=?"
            );

            deleteStudyGroup.setInt(1, this.id);
            sql.setStatement(deleteStudyGroup);
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

    public static ArrayList<HashMap<String, String>> getStudyGroupInfo(String id) {
        ArrayList<HashMap<String, String>> info = new ArrayList<>();

        SQLConnection sql = new SQLConnection();
        try {
            PreparedStatement statement = sql.prepareStatement(
                    "SELECT users.firstName, users.lastName, courses.department, courses.number, courses.name, studygroups.id " +
                            "FROM studygroups " +
                            "INNER JOIN users " +
                            "ON users.id = studygroups.ownerId " +
                            "INNER JOIN joinedgroups " +
                            "ON joinedgroups.groupId = studygroups.id " +
                            "INNER JOIN courses " +
                            "ON courses.id = studygroups.courseId " +
                            "WHERE userId = ?;"
            );
            statement.setInt(1, Integer.parseInt(id));
            sql.setStatement(statement);
            sql.executeQuery();
            ResultSet results = sql.getResults();
            while (results.next()) {
                HashMap<String, String> temp = new HashMap<>();
                temp.put("name", results.getString(1) + " " + results.getString(2));
                temp.put("department", results.getString(3));
                temp.put("courseNumber", results.getString(4));
                temp.put("courseName", results.getString(5));
                temp.put("id", Integer.toString(results.getInt(6)));
                info.add(temp);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            sql.close();
        }

        return info;
    }
}
