package rb.aczg.dao

import rb.aczg.dao.factory.PostgresConnectionFactory
import rb.aczg.interfaces.dao.IConexao
import rb.aczg.interfaces.factory.IConnectionFactory

import java.sql.Connection

class ConexaoBD implements IConexao {

    private static ConexaoBD instancia
    private IConnectionFactory factory

    private ConexaoBD(IConnectionFactory factory) {
        this.factory = factory
    }

    static synchronized ConexaoBD instancia() {
        if (!instancia) {
            instancia = new ConexaoBD(new PostgresConnectionFactory())
        }
        return instancia
    }

    static synchronized void configurar(IConnectionFactory novaFactory) {
        instancia = new ConexaoBD(novaFactory)
    }

    static synchronized void resetar() {
        instancia = null
    }

    @Override
    Connection obterConexao() {
        return factory.criar()
    }
}
