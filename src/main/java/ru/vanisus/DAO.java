package ru.vanisus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public interface DAO {

        static Connection connectDB() throws IOException, SQLException {

            Properties props = new Properties();
            try(InputStream in = Files.newInputStream(Path.of(("src/main/resources/database.properties")))) {
                props.load(in);
            }
            String url = props.getProperty("url");
            String username = props.getProperty("username");
            String password = props.getProperty("password");

            return DriverManager.getConnection(url, username, password);
        }

        static boolean findId(Long userId) {
            boolean checkUser = false;
            int row = 0;
            try{
                Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
                try (Connection conn = connectDB()){
                    java.sql.Statement statement = conn.createStatement();
                    ResultSet resultSet = statement.executeQuery("select * from timetable.User where UserId = " + userId);
                    while (resultSet.next()) {
                        row++;
                    }
                    if(row >  0) {
                        System.out.println("User was found");
                        checkUser = true;
                    }
                }
            }
            catch(Exception ex){
                System.out.println("Connection failed...");
                System.out.println(ex);
            }
            return checkUser;
        }

        static void addRow(Long userId, String url) {
            try{
                Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
                try (Connection conn = connectDB()){
                    java.sql.Statement statement = conn.createStatement();
                    int rows = statement.executeUpdate("INSERT INTO timetable.User(UserID, URL) values (" + userId + ", '" + url + "')");
                    System.out.println("Added: " + rows);
                }
            }
            catch(Exception ex){
                System.out.println("Connection failed...");
                System.out.println(ex);
            }
        }

        static String getURL(Long userId) {
            String url = "";
            try{
                Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
                try (Connection conn = connectDB()){
                    java.sql.Statement statement = conn.createStatement();
                    ResultSet resultSet = (statement.executeQuery("select URL from timetable.User where UserId = " + userId));
                    resultSet.next();
                    url = resultSet.getString(1);
                    System.out.println("URL was found");
                }
            }
            catch(Exception ex){
                System.out.println("Connection failed...");
                System.out.println(ex);
            }
            return url;
        }

        static void updateURL(Long userId, String url) {
            try{
                Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
                try (Connection conn = connectDB()){
                    java.sql.Statement statement = conn.createStatement();
                    int row = statement.executeUpdate("update timetable.User set URL = " + url + " where UserId = " + userId);
                    System.out.println("URL was updated");
                }
            }
            catch(Exception ex){
                System.out.println("Connection failed...");
                System.out.println(ex);
            }
        }
    }

