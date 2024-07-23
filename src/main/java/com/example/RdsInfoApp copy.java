package com.example;

import java.sql.*;

public class RdsInfoApp {

    public static void main(String[] args) {
        // Secrets Manager SQL Connection JDBC ドライバーを使って接続
        String jdbcUrl = "jdbc-secretsmanager:mysql://database-1.colw41khta5t.ap-northeast-1.rds.amazonaws.com:3306/<your-database-name>" +
                         "?serviceName=secretsmanager" +
                         "&serverName=<your-secret-name>" +
                         "&region=<your-region>";

        try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables("<your-database-name>", null, "%", null);

            System.out.println("テーブル一覧:");
            while (tables.next()) {
                System.out.println(tables.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            System.err.println("RDS for MySQLへの接続に失敗しました。");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}



// Load the JDBC driver
Class.forName( "com.amazonaws.secretsmanager.sql.AWSSecretsManagerMySQLDriver" ).newInstance();

// Retrieve the connection info from the secret using the secret ARN
String URL = "secretId";

// Populate the user property with the secret ARN to retrieve user and password from the secret
Properties info = new Properties( );
info.put( "user", "secretId" );

// Establish the connection
conn = DriverManager.getConnection(URL, info);