package models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import util.SQLConnection;
import util.EmailSender;

public class User {
	private int id;
	private String email;
	private String password;
	private String firstName;
	private String lastName;
	private int year;
	private String major;

	// Creating the account
	private User(int id, String email, String password, String firstName, String lastName, int year, String major) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.year = year;
		this.major = major;
	}

	// Log in the account
	public User(String email, String password) {
		this.email = email;
		this.password = password;
	}

	/** 0 means insertion success
	 	1 means no such algorithm exception
	 	2 means there's duplicate of email, which requires special handling outside
	 	3 means sqlexception **/
	public int insertToDatabase() {
		int errorCode = 0;
		SQLConnection sql = new SQLConnection();

		try {
			PreparedStatement statement = sql.prepareStatement("INSERT INTO users "
					+ "(email, password, salt, firstName, lastName, year, major) " + "VALUES (?, ?, ?, ?, ?, ?, ?)");

			byte[] salt = getSalt();
			String securedPassword = generateSecurePassword(this.password, salt);

			statement.setString(1, this.email);
			statement.setString(2, securedPassword);
			statement.setBytes(3, salt);
			statement.setString(4, this.firstName);
			statement.setString(5, this.lastName);
			statement.setInt(6, this.year);
			statement.setString(7, this.major);

			sql.setStatement(statement);
			sql.executeUpdate();
		} catch (NoSuchAlgorithmException nsae) {
			nsae.printStackTrace();
			errorCode = 1;
		} catch (SQLIntegrityConstraintViolationException scve) {
			scve.printStackTrace();
			errorCode = 2;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			errorCode = 3;
		} finally {
			sql.close();
		}

		return errorCode;
	}

	/* 	Authenticate user by password and email, and generate an access token
	 	if they are matched
	 	If returned string is empty, it means authorization failed
	 	Also, authenticate supports temporary password.
	 	If log in with temporary password, temporary password will be updated to permanent.
	 	If log in with permanent password, temporary password is erased.
	 	*/
	public String authenticate() {
		SQLConnection sql = new SQLConnection();
		String returnedToken = "";

		try {
			PreparedStatement statement = sql.prepareStatement("SELECT * FROM users WHERE email=?");

			statement.setString(1, this.email);

			sql.setStatement(statement);
			sql.executeQuery();
			ResultSet results = sql.getResults();
			if (results.next()) {
				byte[] salt = results.getBytes("salt");
				String hashedPassword = generateSecurePassword(this.password, salt);
				String tempPassword = results.getString("tempPassword");
				String permPassword = results.getString("password");
				Boolean authorized = false;
				if (tempPassword != null) {
					// If log in with temporary password, then update temporary to permanent
					// password
					if (tempPassword.equals(hashedPassword)) {
						authorized = true;
						updatePassword();
					}
				}

				if (!authorized) {
					if (permPassword.equals(hashedPassword)) {
						authorized = true;
						// Erase temporary password if log in with permanent password
						if (tempPassword != null) {
							updatePassword();
						}
					} else {
						return returnedToken;
					}
				}

				String token = results.getString("authToken");
				// No update token if available
				if (token != null) {
					returnedToken = token;
				} else {
					boolean inserted = false;
					while (!inserted) {
						statement = sql.prepareStatement("SELECT * FROM users WHERE authToken = ?");
						// Generate random string with length 256 as temporary access token
						token = generateRandomString(256);
						statement.setString(1, token);
						sql.setStatement(statement);
						sql.executeQuery();
						results = sql.getResults();

						// Ensure there's no repetition
						if (!results.next()) {
							returnedToken = token;
							inserted = true;
							statement = sql.prepareStatement("UPDATE users " + "SET authToken = ? " + "WHERE email=?");

							statement.setString(1, token);
							statement.setString(2, this.email);

							sql.setStatement(statement);
							sql.executeUpdate();
						}
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			sql.close();
		}

		return returnedToken;
	}

	// Update password
	public boolean updatePassword() {
		boolean success = true;
		SQLConnection sql = new SQLConnection();
		byte[] salt = null;

		// Get salt
		try {
			PreparedStatement statement = sql.prepareStatement("SELECT * FROM users WHERE email=?");

			statement.setString(1, this.email);

			sql.setStatement(statement);
			sql.executeQuery();
			ResultSet results = sql.getResults();
			if (results.next()) {
				salt = results.getBytes("salt");
			} else {
				return false;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			sql.close();
		}

		try {
			PreparedStatement statement = sql
					.prepareStatement("UPDATE users " + "SET password=?, tempPassword=? " + "WHERE email=?");

			String hashedPassword = generateSecurePassword(this.password, salt);

			statement.setString(1, hashedPassword);
			statement.setString(2, null);
			statement.setString(3, this.email);
			sql.setStatement(statement);
			sql.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			success = false;
		} finally {
			sql.close();
		}

		return success;
	}

	// Update profile
	public boolean updateProfile() {
		boolean success = true;
		SQLConnection sql = new SQLConnection();

		try {
			PreparedStatement statement = sql
					.prepareStatement("UPDATE users " + "SET firstName, lastName, year, major " + "WHERE email=?");

			statement.setString(1, this.firstName);
			statement.setString(2, this.lastName);
			statement.setInt(3, this.year);
			statement.setString(4, this.major);
			statement.setString(5, this.email);
			sql.setStatement(statement);
			sql.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			success = false;
		} finally {
			sql.close();
		}

		return success;
	}

	/** log out a user with authorization token
	 	Note: once log out on one machine, all machines of that account will be
	 	logged out
	 	because the auth token is reset to null.**/
	public static boolean onLogOut(String authToken) {
		boolean success = false;
		SQLConnection sql = new SQLConnection();

		try {
			PreparedStatement statement = sql.prepareStatement("SELECT * FROM users WHERE authToken=?");
			statement.setString(1, authToken);

			sql.setStatement(statement);
			sql.executeQuery();
			ResultSet results = sql.getResults();
			if (results.next()) {
				statement = sql.prepareStatement("UPDATE users " + "SET authToken = null " + "WHERE authToken=?");

				statement.setString(1, authToken);

				sql.setStatement(statement);
				sql.executeUpdate();
				success = true;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			sql.close();
		}

		return success;
	}

	public static void forgetPassword(String email) {
		String tempPassword = generateRandomString(20);

		SQLConnection sql = new SQLConnection();
		byte[] salt = null;

		// Get salt
		try {
			PreparedStatement statement = sql.prepareStatement("SELECT * FROM users WHERE email=?");

			statement.setString(1, email);

			sql.setStatement(statement);
			sql.executeQuery();
			ResultSet results = sql.getResults();
			if (results.next()) {
				salt = results.getBytes("salt");
			} else {
				return;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			sql.close();
		}

		// Set temporary password
		try {
			PreparedStatement statement = sql
					.prepareStatement("UPDATE users " + "SET tempPassword=? " + "WHERE email=?");

			String hashedPassword = generateSecurePassword(tempPassword, salt);

			statement.setString(1, hashedPassword);
			statement.setString(2, email);
			sql.setStatement(statement);
			sql.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			sql.close();
		}

		// Send email
		EmailSender.sendMessage(email, "Forget Password Confirmation from SCroup", "Dear customer:\n"
				+ "\tYou recently sent a forget password request on our SCroup website.\n"
				+ "Your temporary password is: " + tempPassword
				+ "\n If you didn't send such a request, please reset your password as soon as possible. The original password can still be used to log in your account."
				+ "\n\nRegards,\nScroup Support Team");

	}

	// Use authorization token to get access to user object
	public static User lookUpByAuthToken(String authToken) {
		if (authToken.isEmpty()) {
			return null;
		}

		User user = null;
		SQLConnection sql = new SQLConnection();

		try {
			PreparedStatement statement = sql.prepareStatement("SELECT * FROM users WHERE authToken=?");
			statement.setString(1, authToken);

			sql.setStatement(statement);
			sql.executeQuery();
			ResultSet results = sql.getResults();
			if (results.next()) {
				user = new User(results.getInt("id"), results.getString("email"), results.getString("password"),
						results.getString("firstName"), results.getString("lastName"), results.getInt("year"),
						results.getString("major"));
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			sql.close();
		}

		return user;
	}

	// Reference from
	// https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
	private static byte[] getSalt() throws NoSuchAlgorithmException {
		// Always use a SecureRandom generator
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		// Create array for salt
		byte[] salt = new byte[16];
		// Get a random salt
		sr.nextBytes(salt);
		// return salt
		return salt;
	}

	private static String generateSecurePassword(String passwordToHash, byte[] salt) {
		String generatedPassword = null;
		try {
			// Create MessageDigest instance for MD5
			MessageDigest md = MessageDigest.getInstance("MD5");
			// Add password bytes to digest
			md.update(salt);
			// Get the hash's bytes
			byte[] bytes = md.digest(passwordToHash.getBytes());
			// This bytes[] has bytes in decimal format;
			// Convert it to hexadecimal format
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			// Get complete hashed password in hex format
			generatedPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return generatedPassword;
	}

	// https://www.tutorialspoint.com/java/util/random_nextbytes.htm
	private static String generateRandomString(int length) {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[length];
		random.nextBytes(bytes);
		String token = bytes.toString();
		return token;
	}

	// Getter functions
	public int getID() {
		return this.id;
	}

	public String getEmail() {
		return this.email;
	}

	public String getFName() {
		return this.firstName;
	}

	public String getLName() {
		return this.lastName;
	}

	public String getMajor() {
		return this.major;
	}

	public int getYear() {
		return this.year;
	}

	// Setter functions
	public void setFName(String fName) {
		this.firstName = fName;
	}

	public void setLName(String lName) {
		this.lastName = lName;
	}

	public void setMajor(String m) {
		this.major = m;
	}

	public void setYear(int y) {
		this.year = y;
	}
}
