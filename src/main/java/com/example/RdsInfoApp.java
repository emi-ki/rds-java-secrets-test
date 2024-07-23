package com.example;

import java.sql.*;
import java.util.Properties;
import com.amazonaws.secretsmanager.sql.AWSSecretsManagerMySQLDriver;

public class RdsInfoApp {
    public static void main(String[] args) {
        try {
            // Secrets Manager SQL Connection JDBC ドライバーをロード
            try {

                Class.forName("com.amazonaws.secretsmanager.sql.AWSSecretsManagerMySQLDriver").newInstance();
            } catch (ClassNotFoundException e) {
                System.err.println("Secrets Manager JDBC ドライバーが見つかりません。");
                e.printStackTrace();
                System.exit(1);
            }

            // エンドポイント、ポート、データベース名の設定
            String URL = "jdbc-secretsmanager:mysql://database-1.colw41khta5t.ap-northeast-1.rds.amazonaws.com:3306/mysqldb";

            // userプロパティにsecret ARNを入力し、secretからユーザーとパスワードを取得
            Properties info = new Properties();
            info.put("user", "rds!db-5235e294-b7d2-47ba-8fe3-4362ea9fe7f0");

            System.out.println("Attempting to connect to: " + URL);

            // RDS for MySQL への接続
            try (Connection connection = DriverManager.getConnection(URL, info)) {
                System.out.println("Connection successful!");

                // データベースのメタデータを取得
                DatabaseMetaData metaData = connection.getMetaData();
                System.out.println("Connected as user: " + metaData.getUserName());

                // age_table にクエリ
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
            }
        } catch (SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}