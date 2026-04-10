package rb.aczg.dao

import rb.aczg.model.Endereco
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class EnderecoDAO {

    Endereco inserir(Endereco endereco) {
        String sql = """
            INSERT INTO enderecos (cep, logradouro, numero, complemento, bairro, cidade, estado, pais)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            stmt.setString(1, endereco.cep?.replaceAll('[^0-9]', '') ?: '')
            stmt.setString(2, endereco.logradouro)
            stmt.setString(3, endereco.numero)
            stmt.setString(4, endereco.complemento)
            stmt.setString(5, endereco.bairro)
            stmt.setString(6, endereco.cidade)
            stmt.setString(7, endereco.estado)
            stmt.setString(8, endereco.pais)
            stmt.executeUpdate()

            ResultSet keys = stmt.getGeneratedKeys()
            if (keys.next()) endereco.id = keys.getInt(1)
            return endereco
        } finally {
            conexao.close()
        }
    }

    Endereco buscarPorId(int id) {
        String sql = "SELECT * FROM enderecos WHERE id = ?"
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) return mapear(rs)
        } finally {
            conexao.close()
        }
        return null
    }

    Endereco buscarPorCep(String cep) {
        String sql = "SELECT * FROM enderecos WHERE cep = ? LIMIT 1"
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, cep.replaceAll('[^0-9]', ''))
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) return mapear(rs)
        } finally {
            conexao.close()
        }
        return null
    }

    boolean atualizar(Endereco endereco) {
        String sql = """
            UPDATE enderecos
            SET cep=?, logradouro=?, numero=?, complemento=?, bairro=?, cidade=?, estado=?, pais=?
            WHERE id=?
        """
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, endereco.cep?.replaceAll('[^0-9]', '') ?: '')
            stmt.setString(2, endereco.logradouro)
            stmt.setString(3, endereco.numero)
            stmt.setString(4, endereco.complemento)
            stmt.setString(5, endereco.bairro)
            stmt.setString(6, endereco.cidade)
            stmt.setString(7, endereco.estado)
            stmt.setString(8, endereco.pais)
            stmt.setInt(9, endereco.id)
            return stmt.executeUpdate() > 0
        } finally {
            conexao.close()
        }
    }

    List<Endereco> listarTodos() {
        String sql = "SELECT * FROM enderecos ORDER BY cidade"
        List<Endereco> lista = []
        Connection conexao = ConexaoBD.obterConexao()
        try {
            ResultSet rs = conexao.createStatement().executeQuery(sql)
            while (rs.next()) lista << mapear(rs)
        } finally {
            conexao.close()
        }
        return lista
    }

    private Endereco mapear(ResultSet rs) {
        new Endereco(
                id: rs.getInt('id'),
                cep: rs.getString('cep'),
                logradouro:  rs.getString('logradouro'),
                numero: rs.getString('numero'),
                complemento: rs.getString('complemento'),
                bairro: rs.getString('bairro'),
                cidade: rs.getString('cidade'),
                estado: rs.getString('estado'),
                pais: rs.getString('pais')
        )
    }
}
