package rb.aczg.dao

import java.sql.Connection
import java.sql.DriverManager

class ConexaoBD {

    private static final String PROPERTIES_FILE = '/database.properties'

    static Connection obterConexao() {
        Properties props = new Properties()
        InputStream stream = ConexaoBD.class.getResourceAsStream(PROPERTIES_FILE)

        if (!stream) {
            throw new RuntimeException("Arquivo ${PROPERTIES_FILE} nao encontrado no classpath.")
        }

        props.load(stream)

        String url     = props.getProperty('url')
        String usuario = props.getProperty('usuario')
        String senha   = props.getProperty('senha')

        try {
            Class.forName('org.postgresql.Driver')
            return DriverManager.getConnection(url, usuario, senha)
        } catch (Exception e) {
            throw new RuntimeException("Falha ao conectar ao banco: ${e.message}", e)
        }
    }
}
