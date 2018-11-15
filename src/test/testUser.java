import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.SQLConnection;

import static org.junit.jupiter.api.Assertions.fail;

import java.sql.PreparedStatement;
import java.sql.SQLException;

class UserUnitTests {
	
	@BeforeAll
    static void initAll() {
        SQLConnection sql = new SQLConnection(true);

        try {
            PreparedStatement insertUsers = sql.prepareStatement(
                "INSERT INTO users " +
                    "(email, password, salt, firstName, lastName) VALUES " +
                    "('tommytrojan@usc.edu', 'password', '12345678901', 'Tommy', 'Trojan')"
            );
            sql.setStatement(insertUsers);
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

    @Test
    void validAuthenticate() {
        User user = new User("tommytrojan@usc.edu", "password");
        String token = user.authenticate();
        assertTrue(token.length()>0);
    }

    @Test
    void invalidEmailAuthenticate() {
        User user = new User("dumbbruins@ucla.edu", "password");
        String token = user.authenticate();
        assertTrue(token.isEmpty());
    }

    @Test
    void invalidPasswordAuthenticate() {
        User user = new User("tommytrojan@usc.edu", "nopassword");
        String token = user.authenticate();
        assertTrue(token.isEmpty());
    }

    @Test
    void validTempAuthenticate() {
        User user = new User("tommytrojan@usc.edu", "temppassword");
        String token = user.authenticate();
        assertTrue(token.length()>0);
        User user2 = new User("tommytrojan@usc.edu", "temppassword");
        String token2 = user2.authenticate();
        assertTrue(token2.isEmpty());
    }

    @Test
    void validLookUpByAuthToken() {
        User user = new User("tommytrojan@usc.edu", "password");
        String token = user.authenticate();
        User populatedUser = User.lookUpByAuthToken(token);
        assertNotNull(populatedUser);
    }

    @Test
    void validLookUpByAuthToken() {
        User user = new User("dumbbruins@ucla.edu", "password");
        String token = user.authenticate();
        User populatedUser = User.lookUpByAuthToken(token);
        assertNull(populatedUser);
    }

    @Test
    void validLogOut() {
        User user = new User("tommytrojan@usc.edu", "password");
        String token = user.authenticate();
        boolean out = User.onLogOut(token);
        assertTrue(out);
        User populatedUser = User.lookUpByAuthToken(token);
        assertNull(populatedUser);
    }

    @Test
    void invalidLogOut() {
        User user = new User("dumbbruins@ucla.edu", "password");
        String token = user.authenticate();
        boolean out = User.onLogOut(token);
        assertFalse(out);
    }

    @Test
    void validUpdateProfile() {
        User user = new User(1, "tommytrojan@usc.edu", "password", 
            "tommy", "trojan", 18, "CSCI");
        String token = user.authenticate();
        user.setYear(19);
        boolean success = user.updateProfile();
        assertTrue(success);
    }

    @Test
    void invalidUpdateProfile() {
        User user = new User(1, "tommytrojan@usc.edu", "password", 
            "tommy", "trojan", 18, "CSCI");
        // Didn't log in
        user.setYear(19);
        boolean success = user.updateProfile();
        assertFalse(success);
    }

    @Test
    void validUpdatePassword() {
        User user = new User(1, "tommytrojan@usc.edu", "password");
        String token = user.authenticate();
        user.setPassword("newpassword");
        boolean success = user.updatePassword();
        assertTrue(success);
    }

    @Test
    void invalidUpdatePassword() {
        User user = new User(1, "tommytrojan@usc.edu", "password");
        // Didn't log in
        user.setPassword("newpassword");
        boolean success = user.updatePassword();
        assertFalse(success);
    }

    @Test
    void randomStringGenerator() {
        String newString1 = generateRandomString(10);
        String newString2 = generateRandomString(10);
        assertFalse(newString1.Equals(newString2));
        assertTrue(newString1.length() == 10);
    }

    @Test
    void validInsertToDatabase() {
        User user = new User(1, "jeffrey.miller@usc.edu", "password", 
            "Jeffrey", "Miller", 18, "CSCI");
        int code = user.insertToDatabase();
        assertTrue(code == 0);
    }

    @Test
    void duplicateInsertToDatabase() {
        User user = new User(1, "tommytrojan@usc.edu", "password", 
            "tommy", "trojan", 18, "CSCI");
        int code = user.insertToDatabase();
        assertTrue(code == 2);
    }

    @Test
    void nullInsertToDatabase() {
        User user = new User(1, "tommytrojan@usc.edu", "password", 
            "tommy", null, 18, "CSCI");
        int code = user.insertToDatabase();
        assertTrue(code == 2);
    }
    
    @AfterAll
    static void tearDownAll() {
        SQLConnection sql = new SQLConnection(true);

        try {
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