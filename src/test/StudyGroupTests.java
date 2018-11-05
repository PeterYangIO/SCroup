import com.google.gson.Gson;
import models.JoinedGroup;
import models.StudyGroup;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.SQLConnection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StudyGroupTests {
    @BeforeAll
    static void initAll() {
        SQLConnection sql = new SQLConnection(true);

        try {
            PreparedStatement insertUsers = sql.prepareStatement(
                "INSERT INTO users " +
                    "(email, password, salt, firstName, lastName) VALUES " +
                    "('tommytrojan@usc.edu', 'letmein123', 'salt', 'Tommy', 'Trojan')," +
                    "('yangpete@usc.edu.com', 'letmein123', 'salt', 'Peter', 'Yang')"
            );
            sql.setStatement(insertUsers);
            sql.executeUpdate();

            PreparedStatement insertCourses = sql.prepareStatement(
                "INSERT INTO courses " +
                    "(department, number, name) VALUES " +
                    "('CSCI', 102, 'Fundamentals of Computation')," +
                    "('CSCI', 103, 'Introduction to Programming')," +
                    "('CSCI', 104, 'Data Structures and Object Oriented Design')," +
                    "('CSCI', 109, 'Introduction to Computer Science')," +
                    "('CSCI', 170, 'Discrete Methods in Computer Science')," +
                    "('CSCI', 201, 'Principles of Software Development')," +
                    "('CSCI', 270, 'Introduction to Algorithms and Theory of Computing')"
            );
            sql.setStatement(insertCourses);
            sql.executeUpdate();

            PreparedStatement insertStudyGroups = sql.prepareStatement(
                "INSERT INTO studygroups " +
                    "(courseId, ownerId, capacity, size, location, topic, professor, start, end) VALUES " +
                    // For get testing
                    "(1, 1, 4, 1, 'SAL 126', 1, 'Shindler', '2018-01-01 00:00:00', '2018-01-01 01:00:00')," +
                    "(1, 2, 6, 6, 'SAL 109', 2, 'Miller', '2018-01-01 02:00:00', '2018-01-01 03:00:00')," +
                    // For update testing
                    "(1, 1, 4, 1, 'SAL 126', 1, 'Shindler', '2018-01-02 00:00:00', '2018-01-02 01:00:00')," +
                    // For delete testing
                    "(1, 2, 6, 6, 'SAL 109', 2, 'Miller', '2018-01-02 02:00:00', '2018-01-02 03:00:00')"
            );
            sql.setStatement(insertStudyGroups);
            sql.executeUpdate();
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
            fail("Could not set up test data");
        }
        finally {
            sql.close();
        }
    }

    /**
     * StudyGroup.dbSelect() tests
     */

    @Test
    void getStudyGroupsAuthenticated() {
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("courseId", "1");
        int userId = 1;

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        assertTrue(studyGroups.size() > 0);
    }

    @Test
    void getStudyGroupsUnauthenticated() {
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("courseId", "1");
        int userId = -1;

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        assertTrue(studyGroups.size() > 0);
    }

    @Test
    void getCoursesInvalidCourseId() {
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("courseId", "-1");
        int userId = 1;

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        assertEquals(0, studyGroups.size());
    }

    @Test
    void filterParameterCapacityMin() {
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("courseId", "1");
        filterParams.put("capacityMin", "5");
        int userId = 1;

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        assertEquals(1, studyGroups.size());
        assertEquals(2, studyGroups.get(0).getOwnerId());
    }

    @Test
    void filterParameterCapacityMax() {
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("courseId", "1");
        filterParams.put("capacityMax", "5");
        int userId = 1;

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        assertTrue(studyGroups.size() > 0);
        for (StudyGroup studyGroup : studyGroups) {
            assertTrue(studyGroup.getCapacity() >= 5);
        }
    }

    @Test
    void filterParameterHideFull() {
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("courseId", "1");
        filterParams.put("hideFull", "true");
        int userId = 1;

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        int currentSize = studyGroups.size();
        assertTrue(studyGroups.size() > 0);
        for (StudyGroup studyGroup : studyGroups) {
            assertNotEquals(studyGroup.getCapacity(), studyGroup.getSize());
        }

        filterParams.put("hideFull", "false");
        studyGroups = StudyGroup.dbSelect(filterParams, userId);
        // This should reveal 0 or more previously hidden groups
        assertTrue(currentSize >= studyGroups.size());
    }

    @Test
    void filterParameterLocation() {
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("courseId", "1");
        filterParams.put("location", "SAL 126");
        int userId = 1;

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        assertEquals(1, studyGroups.size());
        assertEquals("SAL 126", studyGroups.get(0).getLocation());

        filterParams.put("location", "SAL 109");
        studyGroups = StudyGroup.dbSelect(filterParams, userId);
        assertEquals(1, studyGroups.size());
        assertEquals("SAL 109", studyGroups.get(0).getLocation());
    }

    @Test
    void filterParameterTopic() {
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("courseId", "1");
        filterParams.put("topic", "1");
        int userId = 1;

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        assertTrue(studyGroups.size() > 0);
        for (StudyGroup studyGroup : studyGroups) {
            assertEquals(1, studyGroup.getTopic());
        }
    }

    @Test
    void filterParameterProfessor() {
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("courseId", "1");
        filterParams.put("professor", "Miller");
        int userId = 1;

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        assertTrue(studyGroups.size() > 0);
        for (StudyGroup studyGroup : studyGroups) {
            assertEquals("Miller", studyGroup.getProfessor());
        }
        assertEquals("Miller", studyGroups.get(0).getProfessor());

        // TODO Better "LIKE" test cases
        filterParams.put("professor", "mill");
        studyGroups = StudyGroup.dbSelect(filterParams, userId);
        for (StudyGroup studyGroup : studyGroups) {
            assertEquals("Miller", studyGroup.getProfessor());
        }
    }

    @Test
    void filterParameterAfter() {
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("courseId", "1");
        filterParams.put("after", "2018-01-01 00:00:00");
        int userId = 1;

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        assertTrue(studyGroups.size() > 0);
    }

    @Test
    void filterParameterBefore() {
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("courseId", "1");
        filterParams.put("before", "2018-01-01 2:00:00");
        int userId = 1;

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        assertTrue(studyGroups.size() > 0);
    }

    /**
     * StudyGroup.dbInsert() tests
     */

    @Test
    void createMinimal() {
        StudyGroup group = new Gson().fromJson(
            "{courseId: 1, ownerId: 1}",
            StudyGroup.class
        );
        int userId = 1;
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("courseId", "1");

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        int currentSize = studyGroups.size();

        assertTrue(group.dbInsert());

        studyGroups = StudyGroup.dbSelect(filterParams, userId);
        assertEquals(currentSize + 1, studyGroups.size());
    }

    @Test
    void createMaximal() {
        StudyGroup group = new Gson().fromJson(
            "{courseId: 1, ownerId: 1, capacity: 4, location: 'SAL 127', topic: 1, professor: 'Miller', start: '2018-01-01T00:00:00Z', end: '2018-01-01T01:00:00Z'}",
            StudyGroup.class
        );
        int userId = 1;
        Map<String, String> filterParams = new HashMap<>();
        filterParams.put("courseId", "1");

        ArrayList<StudyGroup> studyGroups = StudyGroup.dbSelect(filterParams, userId);
        int currentSize = studyGroups.size();

        assertTrue(group.dbInsert());

        studyGroups = StudyGroup.dbSelect(filterParams, userId);
        assertEquals(currentSize + 1, studyGroups.size());
    }

    @Test
    void createWithInvalidCourse() {
        StudyGroup group = new Gson().fromJson(
            "{courseId: -1, ownerId: 1}",
            StudyGroup.class
        );
        assertFalse(group.dbInsert());
    }

    @Test
    void createStartAfterEnd() {
        StudyGroup group = new Gson().fromJson(
            "{courseId: 1, ownerId: 1, start: '2018-01-01T01:00:00Z', end: '2018-01-01T00:00:00Z'}",
            StudyGroup.class
        );
        assertFalse(group.dbInsert());
    }

    @Test
    void createUnauthenticated() {
        StudyGroup group = new Gson().fromJson(
            "{courseId: 1, ownerId: -1}",
            StudyGroup.class
        );
        assertFalse(group.dbInsert());
    }

    /**
     * dbUpdate() tests
     */

    @Test
    void update() {
        StudyGroup group = new Gson().fromJson(
            "{id: 3, capacity: 8, location: 'GFS 116', topic: 2, professor: 'Miller', start: '2018-01-03T00:00:00Z', end: '2018-01-03T01:00:00Z'}",
            StudyGroup.class
        );

        assertTrue(group.dbUpdate());
    }

    @Test
    void updateCapacitySmallerSize() {
        StudyGroup group = new Gson().fromJson(
            "{id: 3, capacity: 4, size: 5",
            StudyGroup.class
        );

        assertFalse(group.dbUpdate());
    }

    @Test
    void updateStartAfterEnd() {
        StudyGroup group = new Gson().fromJson(
            "{id: 3, start: '2018-01-03T01:00:00Z', end: '2018-01-03T00:00:00Z'}",
            StudyGroup.class
        );

        assertFalse(group.dbUpdate());
    }

    /**
     * StudyGroup.dbDelete() tests
     */

    @Test
    void deleteStudyGroup() {
        StudyGroup group = new Gson().fromJson(
            "{id: 4}",
            StudyGroup.class
        );

        assertTrue(group.dbDelete());
    }

    /**
     * JoinedGroup.dbSelect() tests
     */

    @Test
    void selectStudyGroupsById() {
        assertNotNull(JoinedGroup.dbSelectByUser(1));
    }

    @Test
    void selectUsersByStudyGroupId() {
        assertNotNull(JoinedGroup.dbSelectByGroup(1));
    }

    /**
     * JoinedGroup.dbInsert() tests
     */

    @Test
    void insertJoinGroup() {
        JoinedGroup joinedGroup = new Gson().fromJson(
            "{groupId: 1}",
            JoinedGroup.class
        );
        joinedGroup.setUserId(1);

        ArrayList<StudyGroup> joinedGroups = JoinedGroup.dbSelectByUser(1);
        int currentSize = joinedGroups.size();

        assertTrue(joinedGroup.dbInsert());
        joinedGroups = JoinedGroup.dbSelectByUser(1);
        assertTrue(joinedGroups.size() > currentSize);
    }

    @Test
    void insertJoinAlreadyJoined() {
        JoinedGroup joinedGroup = new Gson().fromJson(
            "{groupId: 1}",
            JoinedGroup.class
        );
        joinedGroup.setUserId(1);

        // Join now so user can try joining the joined group later
        assertTrue(joinedGroup.dbInsert());
        ArrayList<StudyGroup> joinedGroups = JoinedGroup.dbSelectByUser(1);
        int currentSize = joinedGroups.size();

        assertTrue(joinedGroup.dbInsert());
        joinedGroups = JoinedGroup.dbSelectByUser(1);
        assertEquals(currentSize, joinedGroups.size());
    }

    /**
     * JoinedGroup.dbDelete() tests
     */

    @Test
    void deleteLeaveGroup() {
        JoinedGroup joinedGroup = new Gson().fromJson(
            "{groupId: 1}",
            JoinedGroup.class
        );
        joinedGroup.setUserId(1);

        // First join group so user can leave it later
        assertTrue(joinedGroup.dbInsert());
        ArrayList<StudyGroup> joinedGroups = JoinedGroup.dbSelectByUser(1);
        int currentSize = joinedGroups.size();

        assertTrue(joinedGroup.dbDelete());
        joinedGroups = JoinedGroup.dbSelectByUser(1);
        assertEquals(currentSize - 1, joinedGroups.size());
    }

    @Test
    void deleteLeaveAlreadyLeft() {
        JoinedGroup joinedGroup = new Gson().fromJson(
            "{groupId: 1}",
            JoinedGroup.class
        );
        joinedGroup.setUserId(1);

        // Ensure user is not in group
        assertTrue(joinedGroup.dbDelete());
        ArrayList<StudyGroup> joinedGroups = JoinedGroup.dbSelectByUser(1);
        int currentSize = joinedGroups.size();

        assertTrue(joinedGroup.dbDelete());
        joinedGroups = JoinedGroup.dbSelectByUser(1);
        assertEquals(currentSize,  joinedGroups.size());
    }

    @AfterAll
    static void tearDownAll() {
        SQLConnection sql = new SQLConnection(true);

        try {
            PreparedStatement deleteStudyGroups = sql.prepareStatement(
                "DELETE FROM studygroups"
            );
            sql.setStatement(deleteStudyGroups);
            sql.executeUpdate();
            PreparedStatement resetStudyGroupsAutoIncrement = sql.prepareStatement(
                " ALTER TABLE studygroups AUTO_INCREMENT = 1"
            );
            sql.setStatement(resetStudyGroupsAutoIncrement);
            sql.executeUpdate();

            PreparedStatement deleteCourses = sql.prepareStatement(
                "DELETE FROM courses"
            );
            sql.setStatement(deleteCourses);
            sql.executeUpdate();
            PreparedStatement resetCoursesAutoIncrement = sql.prepareStatement(
                "ALTER TABLE courses AUTO_INCREMENT =1"
            );
            sql.setStatement(resetCoursesAutoIncrement);
            sql.executeUpdate();

            PreparedStatement deleteUsers = sql.prepareStatement(
                "DELETE FROM users"
            );
            sql.setStatement(deleteUsers);
            sql.executeUpdate();
            PreparedStatement resetUsersAutoIncrement = sql.prepareStatement(
                "ALTER TABLE users AUTO_INCREMENT = 1"
            );
            sql.setStatement(resetUsersAutoIncrement);
            sql.executeUpdate();
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
            fail("Could not tear down properly");
        }
        finally {
            sql.close();
        }
    }
}