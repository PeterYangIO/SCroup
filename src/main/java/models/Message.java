package models;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import util.SQLConnection;

public class Message {
	private int userid;
	private int groupid;
	private String message;
	
	public Message(int userid, int groupid, String message) {
		this.userid = userid;
		this.groupid = groupid;
		this.message = message;
	}
	
	public Boolean insertToDatabase() {
		SQLConnection sql = new SQLConnection();

		try {
			PreparedStatement statement = sql.prepareStatement("INSERT INTO messages "
					+ "(user_id, group_id, message, pinned) " + "VALUES (?, ?, ?, ?)");

			statement.setInt(1, this.userid);
			statement.setInt(2, this.groupid);
			statement.setString(3, this.message);
			statement.setBoolean(4, false);

			sql.setStatement(statement);
			sql.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return false;
		} finally {
			sql.close();
		}

		return true;
	}
}
