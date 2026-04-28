package rb.aczg.dao.factory

import rb.aczg.interfaces.factory.IConnectionFactory

import java.sql.Connection
import java.sql.DriverManager

class H2ConnectionFactory implements IConnectionFactory {

    private static final String URL = 'jdbc:h2:mem:linketinder;DB_CLOSE_DELAY=-1'
    private static final String DRIVER = 'org.h2.Driver'

    @Override
    Connection criar() {
        try {
            Class.forName(DRIVER)
            return DriverManager.getConnection(URL, 'sa', '')
        } catch (Exception e) {
            throw new RuntimeException("Falha ao conectar ao H2: ${e.message}", e)
        }
    }
}
