package rb.aczg.interfaces.dao

import java.sql.Connection

interface IConexao {
    Connection obterConexao()
}
