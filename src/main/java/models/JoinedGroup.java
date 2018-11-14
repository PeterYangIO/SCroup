package models;

import util.SQLConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class JoinedGroup {
    private int id;
    private int userId;
    private int groupId;

    JoinedGroup(int userId, int groupId) {
        this.userId = userId;
        this.groupId = groupId;
    }

    private JoinedGroup(int id, int userId, int groupId) {
        this.id = id;
        this.userId = userId;
        this.groupId = groupId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Selects all study groups a user has joined. This is useful for displaying
     * all the study groups a user belongs to on one page instead of
     * having to look for all of them.
     *
     * @param userId user.id
     * @return All study groups the user is in
     */
    public static ArrayList<StudyGroup> dbSelectByUser(int userId) {
        ArrayList<StudyGroup> studyGroups = new ArrayList<>();
        SQLConnection sql = new SQLConnection();

        try {
            PreparedStatement statement = sql.prepareStatement(
                    "SELECT g.id, g.courseId, g.ownerId, g.capacity, g.size, g.location, g.topic, g.professor, g.size, g.end, " +
                            "CONCAT(u.firstName, ' ', u.lastName) AS ownerName" +
                            " FROM studygroups AS g" +
                            " JOIN joinedgroups AS j on g.id = j.groupId" +
                            " JOIN users AS u ON g.ownerId = u.id" +
                            " WHERE g.ownerId=?"
            );
            statement.setInt(1, userId);

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
                                true,
                                ""
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
     * Returns all users in a study group. This is useful when in the
     * study group's dashboard to display all users
     *
     * @param groupId studygroup.id
     * @return All users in a specific study group
     */
    public static ArrayList<User> dbSelectByGroup(int groupId) {
        ArrayList<User> users = new ArrayList<>();
        SQLConnection sql = new SQLConnection();

        try {
            PreparedStatement statement = sql.prepareStatement(
                    "SELECT u.id, u.email, u.firstName, u.lastName, u.year, u.major" +
                            " FROM users AS u" +
                            " JOIN joinedgroups j on u.id = j.userId" +
                            " WHERE j.groupId=?"
            );
            statement.setInt(1, groupId);

            sql.setStatement(statement);
            sql.executeQuery();
            ResultSet results = sql.getResults();
            while (results.next()) {
                users.add(
                        new User(
                                results.getInt(1),
                                results.getString(2),
                                "",
                                results.getString(3),
                                results.getString(4),
                                results.getInt(5),
                                results.getString(6)
                        )
                );
            }
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
            users = null;
        }
        finally {
            sql.close();
        }

        return users;
    }

    /**
     * Inserts a join connection for a user into a group provided
     * the user is not already in the group
     *
     * @return true if success
     */
    public boolean dbInsert() {
        boolean success = true;
        SQLConnection sql = new SQLConnection();

        try {
            PreparedStatement statement = sql.prepareStatement(
                    "INSERT INTO joinedgroups (userId, groupId)" +
                            " SELECT ?, ?" +
                            " FROM joinedgroups" +
                            " WHERE userId=? AND groupId=?" +
                            " HAVING COUNT(*) = 0"
            );
            statement.setInt(1, this.userId);
            statement.setInt(2, this.groupId);
            statement.setInt(3, this.userId);
            statement.setInt(4, this.groupId);

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

        if (success) {
            // Increment amount of users in the group by 1
            sql = new SQLConnection();
            try {
                PreparedStatement statement = sql.prepareStatement(
                        "UPDATE studygroups SET size=size + 1 WHERE id=?"
                );
                statement.setInt(1, this.groupId);

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
        }

        return success;
    }

    /**
     * Deletes join connection where a user is joined to a group
     *
     * @return true if successful
     */
    public boolean dbDelete() {
        boolean success = true;
        SQLConnection sql = new SQLConnection();

        try {
            PreparedStatement statement = sql.prepareStatement(
                    "DELETE FROM joinedgroups WHERE userId=? AND groupId=?"
            );
            statement.setInt(1, this.userId);
            statement.setInt(2, this.groupId);

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

        if (success) {
            // Decrement amount of users in the group by 1
            sql = new SQLConnection();
            try {
                PreparedStatement statement = sql.prepareStatement(
                        "UPDATE studygroups SET size=size - 1 WHERE id=?"
                );
                statement.setInt(1, this.groupId);

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
        }

        return success;
    }
}
