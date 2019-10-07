package org.openjfx.Controllers;

import org.openjfx.PlayerMP;

import java.sql.*;

public class SQLite {

    private static Connection con;
    private static boolean hasData = false;

    public void displayUsers(){
        if (con == null){
            getConnection();
        }
        System.out.println("nå");
        Statement state = null;
        try {
            state = con.createStatement();
            ResultSet rs = state.executeQuery("SELECT uname, passwd, player_x, player_y FROM UserDB");
            while (rs.next()){
                System.out.println("Username: " + rs.getString("uname") +
                                " Password: " + rs.getString("passwd") +
                                " Position x: " + rs.getInt("player_x") +
                                " Position y: " + rs.getInt("player_y"));
            }
            //return res;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void getConnection(){
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:UserDB.db");
            initialize();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initialize() throws SQLException {
        if (!hasData){
            hasData = true;
        }
        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT name from sqlite_master WHERE type='table' AND name='UserDB'");
        if (!res.next()){
            System.out.println("CREATING THE DATABASE");
            Statement state2 = con.createStatement();
            state2.execute("CREATE TABLE UserDB(uname varchar(60),"
                            + "passwd varchar(60)," + "player_x integer," + "player_y integer,"
                    + "primary key(uname));");
        }
    }

    public void addUser(String username, String password){
        if (con == null){
            getConnection();
        }
        try {
            PreparedStatement prep = con.prepareStatement("INSERT OR IGNORE INTO UserDB values(?,?,?,?); ");
            prep.setString(1,username);
            prep.setString(2,password);
            prep.setInt(3,50);
            prep.setInt(4,35);
            prep.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getUser(String username, String password){
        if (con == null){
            getConnection();
        }
        ResultSet rs;
        String queryResult = null;
        try {
            String SQL = "SELECT uname, passwd FROM UserDB WHERE uname = " + "\"" +username + "\"" + " AND passwd = " + "\"" + password + "\"";
            System.out.println(SQL);
            PreparedStatement prep = con.prepareStatement(SQL);
            rs = prep.executeQuery();

            while (rs.next())
                queryResult = rs.getString("uname") + rs.getString("passwd");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(queryResult);
        return queryResult != null && queryResult.equals(username+password);
    }

    public PlayerMP loadUser(String username){
        if (con == null){
            getConnection();
        }
        ResultSet rs;
        int player_x = 0;
        int player_y = 0;
        try {
            String SQL = "SELECT uname, player_x, player_y FROM UserDB WHERE uname = " + "\"" +username + "\"";
            System.out.println(SQL);
            PreparedStatement prep = con.prepareStatement(SQL);
            rs = prep.executeQuery();
            while (rs.next()){
                username = rs.getString("uname");
                player_x = rs.getInt("player_x");
                player_y = rs.getInt("player_y");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return new PlayerMP(username,player_x,player_y,null,0);
    }
}
