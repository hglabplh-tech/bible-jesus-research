package org.harry.jesus.dbjoshuautils;

import org.hibernate.connection.ConnectionProvider;
import org.hibernate.connection.ConnectionProviderFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * The type Db creator.
 */
public class DBCreator {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws SQLException the sql exception
     */
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
