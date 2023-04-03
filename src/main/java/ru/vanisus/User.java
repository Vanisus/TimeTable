package ru.vanisus;

import java.net.URL;
import java.sql.Connection;
import java.sql.Statement;

import static ru.vanisus.App.dbStatement;
import static ru.vanisus.App.getConnection;

public class User {
    private String name;
    private Long userID;
    private String url;


    public User(Long userID, String name, String url) {
        dbStatement("INSERT INTO timetable.User(UserID, URL) values (" + userID + ", '" + url + "')");
//        try{
//            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
//            try (Connection conn = getConnection()){
//                Statement statement = conn.createStatement();
//                int rows = statement.executeUpdate("INSERT INTO timetable.User(UserID, URL) values (" + UserID + ", '" + URL + "')");
//                System.out.println("Added: " + rows);
//            }
//        }
//        catch(Exception ex){
//            System.out.println("Connection failed...");
//            System.out.println(ex);
//        }
        this.name = name;
        this.url = url;
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }
}
