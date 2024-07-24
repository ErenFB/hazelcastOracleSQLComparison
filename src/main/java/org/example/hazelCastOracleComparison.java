package org.example;

import com.hazelcast.collection.IList;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class hazelCastOracleComparison {
    public static void main(String[] args) throws SQLException {
        calculateTime(100000);
    }
    public static void calculateTime(int numberCount)throws SQLException{
        Random randomGenerator = new Random();
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance();
        IList<Integer> hazelcastList = hazelcastInstance.getList("randomNumbersList");

        long hazelcastPutStart = System.currentTimeMillis();
        for (int i = 0; i < numberCount; i++) {
            hazelcastList.add(randomGenerator.nextInt(100));
        }
        long hazelcastPutEnd = System.currentTimeMillis();

        long hazelcastGetStart = System.currentTimeMillis();
        List<Integer> hazelcastRetrievedNumbers = new ArrayList<>();
        for (int i = 0; i < numberCount; i++) {
            hazelcastRetrievedNumbers.add(hazelcastList.get(i));
        }
        long hazelcastGetEnd = System.currentTimeMillis();

        hazelcastList.destroy();
        hazelcastInstance.shutdown();


        Connection oracleConnection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:TEST", "EREN", "123");
        Statement oracleStatement = oracleConnection.createStatement();
        oracleStatement.executeUpdate("DELETE FROM DEMO"); // Clear existing data

        long oracleInsertStart = System.currentTimeMillis();
        int randomValue;
        for (int i = 0; i < numberCount; i++) {
            randomValue = randomGenerator.nextInt(100);
            oracleStatement.executeUpdate("INSERT INTO DEMO(NUMBERS, ID) VALUES(" + randomValue + " ," + i + ")");
        }
        long oracleInsertEnd = System.currentTimeMillis();

        long oracleSelectStart = System.currentTimeMillis();
        ResultSet resultSet;
        for (int i = 0; i < numberCount; i++) {
            resultSet = oracleStatement.executeQuery("SELECT NUMBERS FROM DEMO WHERE ID = " + i);
            resultSet.next();
        }
        long oracleSelectEnd = System.currentTimeMillis();


        System.out.println("   Number Count: "+numberCount);
        System.out.println("   Hazelcast put time: " + (hazelcastPutEnd - hazelcastPutStart) + " ms");
        System.out.println("   Hazelcast get time: " + (hazelcastGetEnd - hazelcastGetStart) + " ms");
        System.out.println("   Oracle insert time: " + (oracleInsertEnd - oracleInsertStart) + " ms");
        System.out.println("   Oracle select time: " + (oracleSelectEnd - oracleSelectStart) + " ms");
    }
}
