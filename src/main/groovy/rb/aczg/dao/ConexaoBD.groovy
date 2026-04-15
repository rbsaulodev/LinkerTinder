package rb.aczg.dao

import rb.aczg.interfaces.IConexao

import java.sql.Connection
import java.sql.DriverManager

class ConexaoBD implements IConexao {

    private static final String PROPERTIES_FILE = '/database.properties'

    @Override
    Connection obterConexao() {
        Properties props = new Properties()
        InputStream stream = ConexaoBD.class.getResourceAsStream(PROPERTIES_FILE)

        if (!stream) {
            throw new RuntimeException("Arquivo ${PROPERTIES_FILE} nao encontrado no classpath.")
        }

        props.load(stream)

        try {
            Class.forName('org.postgresql.Driver')
            return DriverManager.getConnection(
                    props.getProperty('url'),
                    props.getProperty('usuario'),
                    props.getProperty('senha')
            )
        } catch (Exception e) {
            throw new RuntimeException("Falha ao conectar ao banco: ${e.message}", e)
        }
    }
}
