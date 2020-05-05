package com.sotomaque.ppmtool.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class TestJdbc {
    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://localhost:3306/?user=root?useSSL=false&serverTimezone=UTC";
        Properties myProp = new Properties();
        myProp.put("user", "PPTadmin");
        myProp.put("password", "PPTadmin");


        try {
            System.out.println("Connecting to DB: " + jdbcUrl);
            Connection myConn = DriverManager.getConnection(jdbcUrl, myProp);
            System.out.println("Connection Successful!");

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
