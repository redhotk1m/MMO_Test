package org.openjfx.Controllers;

import org.openjfx.PlayerMP;

import java.net.InetAddress;
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
            state2.execute("CREATE TABLE UserDB(" +
                    "uname varchar(60) NOT NULL," +
                    "passwd varchar(60) NOT NULL," +
                    "player_x integer NOT NULL default 50," +
                    "player_y integer NOT NULL default 50," +
                    "primary key(uname));");
        }
    }

    public void addUser(String username, String password){
        if (con == null){
            getConnection();
        }
        try {
            PreparedStatement prep = con.prepareStatement("INSERT OR IGNORE INTO UserDB (uname,passwd) values(?,?); ");
            prep.setString(1,username);
            prep.setString(2,password);
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
            System.out.println("Heisann gutta! ------------------");
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

    public PlayerMP loadUser(String username, InetAddress address, int port){
        if (con == null){
            getConnection();
        }
        ResultSet rs;
        int player_x = 0;
        int player_y = 0;
        try {
            String SQL = "SELECT uname, player_x, player_y FROM UserDB WHERE uname = " + "\"" + username + "\"";
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
        return new PlayerMP(username,player_x,player_y,address,port);
    }

    public boolean userExists(String username){
        if (con == null){
            getConnection();
        }
        System.out.println("user exists brukes med brukernavn: " + username);
        ResultSet rs;
        String usernameDB = null;

        try {
            String SQL = "SELECT uname FROM UserDB WHERE uname = " + "\"" + username + "\"";
            System.out.println(SQL + "\n" + SQL);
            PreparedStatement prep = con.prepareStatement(SQL);
            rs = prep.executeQuery();
            while (rs.next()){
                usernameDB = rs.getString("uname");
                System.out.println("Dette er userDB " + usernameDB);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println(usernameDB != null);
        System.out.println("Forrige statement er true hvis brukeren eksisterer fra før, hvis ikke er den false");
        return usernameDB != null;
    }
}
