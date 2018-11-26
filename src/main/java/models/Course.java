package models;

import util.SQLConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

public class Course {
    private int id;
    private String department;
    private int number;
    private String name;
    

    private Course(int id, String department, int number, String name) {
        this.id = id;
        this.department = department;
        this.number = number;
        this.name = name;
    }


    /**
     * Search for courses and query the following fields:
     *      department: associated university department: required
     *      number: official course number assigned by university: required
     *      name: description of the course: required

     * @return All courses that match the search parameters
     */
    public static ArrayList<Course> dbSelect(String [] Params) {
        ArrayList<Course> Courses = new ArrayList<>();
        SQLConnection sql = new SQLConnection();

        try {
            String statement = "SELECT DISTINCT * FROM courses "; 
            
            // add all of the search parameters to the statement
            for (int i =0; i< Params.length;i++) {
            	if(i==0) {
            		statement += "WHERE ";
            	}
            	statement +=  "(department LIKE ? OR ";
            	statement +=  "number LIKE ? OR ";
            	if(i == (Params.length-1)) {
            		statement +=  "name LIKE ? )";
            	}
            	else {
            		statement +=  "name LIKE ? ) AND ";
            	}
            }
           
            PreparedStatement ps = sql.prepareStatement(statement);
            for(int i =0; i<Params.length; i++) {
            	for(int j =1;j<4;j++) {
            		ps.setString((3*i+j), "%" + Params[i] + "%");
            	}
            	
            }
            
            // Execute the statement and serialize the result set into ArrayList<Courses>
            sql.setStatement(ps);
            sql.executeQuery();
            ResultSet results = sql.getResults();
            while (results.next()) {
                Courses.add(
                    new Course(
                        results.getInt(1),
                        results.getString(2),
                        results.getInt(3),
                        results.getString(4)
                    )
                );
            }
        }
        catch (SQLException sqle) {
            sqle.printStackTrace();
            Courses = null;
        }
        finally {
            sql.close();
        }

        return Courses;
    }

    /**
     * Creates a course with the following parameters:
     *      department: associated university department: required
     *      number: official course number assigned by university: required
     *      name: description of the course: required
     *
     *  Note the following member variable is not used:
     *      id: pk is automatically generated by id
     * @return true if successful
     */
    public boolean dbInsert() {
        boolean success = true;
        SQLConnection sql = new SQLConnection();

        try {
            PreparedStatement statement = sql.prepareStatement(
                "INSERT INTO courses " +
                    "(department, number, name) " +
                    "VALUES (?, ?, ?,)"
            );
            statement.setString(1, this.department);
            statement.setInt(2, this.number);
            statement.setString(3, this.name);

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
     *      department,number,name
     * @return true if successful
     */
    public boolean dbUpdate() {
        boolean success = true;
        SQLConnection sql = new SQLConnection();

        try {
            PreparedStatement statement = sql.prepareStatement(
                "UPDATE courses " +
                    "SET department=?, number=?, name=?" +
                    "WHERE id=?"
            );

            statement.setString(1, this.department);
            statement.setInt(2, this.number);
            statement.setString(3, this.name);
            statement.setInt(3, this.id);


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
}
