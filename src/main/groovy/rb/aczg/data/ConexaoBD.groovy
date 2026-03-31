package rb.aczg.data

import java.sql.Connection
import java.sql.DriverManager

class ConexaoBD {

    private static final String PROPERTIES_FILE = '/database.properties'

    static Connection obterConexao() {
        Properties props = new Properties()
        InputStream stream = ConexaoBD.class.getResourceAsStream(PROPERTIES_FILE)

        if (!stream) {
            throw new RuntimeException("Arquivo ${PROPERTIES_FILE} não encontrado no classpath.")
        }

        props.load(stream)

        String url = props.getProperty('url')
        String usuario = props.getProperty('usuario')
        String senha = props.getProperty('senha')

        try {
            Class.forName('org.postgresql.Driver')
            Connection conexao = DriverManager.getConnection(url, usuario, senha)
            return conexao
        } catch (Exception e) {
            throw new RuntimeException("Falha ao conectar ao banco de dados: ${e.message}", e)
        }
    }
}