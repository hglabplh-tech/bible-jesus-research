package org.harry.jesus.dbjoshuautils;

import org.hibernate.connection.ConnectionProvider;
import org.hibernate.connection.ConnectionProviderFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DBCreator {

    public static void main(String [] args) throws SQLException {
        Properties connProps = new Properties();
        connProps.setProperty("hibernate.connection.datasource", "");
        connProps.setProperty("hibernate.connection.username", "");
        connProps.setProperty("hibernate.connection.password", "");
        ConnectionProvider provider = ConnectionProviderFactory.newConnectionProvider(connProps);
        provider.configure(connProps);
        Connection conn = provider.getConnection();




    }
}
