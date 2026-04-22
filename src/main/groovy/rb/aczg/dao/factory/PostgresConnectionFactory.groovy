package rb.aczg.dao.factory

import rb.aczg.interfaces.factory.IConnectionFactory

import java.sql.Connection
import java.sql.DriverManager

class PostgresConnectionFactory implements IConnectionFactory {

    private static final String PROPERTIES_FILE = '/database.properties'
    private static final String DRIVER           = 'org.postgresql.Driver'

    private final Properties props = carregarPropriedades()

    @Override
    Connection criar() {
        try {
            Class.forName(DRIVER)
            return DriverManager.getConnection(
                props.getProperty('url'),
                props.getProperty('usuario'),
                props.getProperty('senha')
            )
        } catch (Exception e) {
            throw new RuntimeException("Falha ao conectar ao PostgreSQL: ${e.message}", e)
        }
    }

    private Properties carregarPropriedades() {
        InputStream stream = getClass().getResourceAsStream(PROPERTIES_FILE)
        if (!stream) {
            throw new RuntimeException("Arquivo ${PROPERTIES_FILE} nao encontrado no classpath.")
        }
        Properties p = new Properties()
        p.load(stream)
        return p
    }
}
