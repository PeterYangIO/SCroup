class UserUnitTests {

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
}