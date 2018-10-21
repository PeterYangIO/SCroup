package models;

public class User {
    private int id;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private int year;
    private String major;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
