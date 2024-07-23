package com.example;

import java.sql.*;

public class RdsInfoApp {
    public static void main(String[] args) {
        // Secrets Manager SQL Connection JDBC ドライバーをロード
        try {
            Class.forName("com.amazonaws.secretsmanager.sql.AWSSecretsManagerMySQLDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Secrets Manager JDBC ドライバーが見つかりません。");
            e.printStackTrace();
            System.exit(1);
        }

        // JDBC URL（ホスト、ポート、データベース名を直接指定）
        String jdbcUrl = "jdbc-secretsmanager:mysql://" +
                         "database-1.colw41khta5t.ap-northeast-1.rds.amazonaws.com:3306" +
                         "/mysqldb" +
                         "?secretId=rds!db-5235e294-b7d2-47ba-8fe3-4362ea9fe7f0" +
                         "&region=ap-northeast-1";

        try {
            System.out.println("Attempting to connect to: " + jdbcUrl);

            // デバッグ情報を追加
            AWSSecretsManagerDriver driver = new AWSSecretsManagerDriver();
            Properties props = driver.getSecretProperties(jdbcUrl);
            System.out.println("Retrieved username: " + props.getProperty("user"));
            System.out.println("Password retrieved: " + (props.getProperty("password") != null ? "Yes" : "No"));

            // RDS for MySQL への接続
            Connection connection = DriverManager.getConnection(jdbcUrl);
            System.out.println("Connection successful!");

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT * FROM age_table")) {

                ResultSetMetaData rsmd = resultSet.getMetaData();
                int columnsNumber = rsmd.getColumnCount();

                System.out.println("age_table の内容:");
                // カラム名を出力
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(" | ");
                    System.out.print(rsmd.getColumnName(i));
                }
                System.out.println();

                // テーブルの内容を出力
                while (resultSet.next()) {
                    for (int i = 1; i <= columnsNumber; i++) {
                        if (i > 1) System.out.print(" | ");
                        System.out.print(resultSet.getString(i));
                    }
                    System.out.println();
                }
            }
        } catch (SQLException e) {
            System.err.println("RDS for MySQLへの接続または操作に失敗しました。");
            System.err.println("Error message: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace();
            System.exit(1);
        }
    }
}