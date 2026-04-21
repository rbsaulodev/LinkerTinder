package rb.aczg.dao

import rb.aczg.interfaces.dao.IConexao
import rb.aczg.interfaces.dao.IEnderecoDAO
import rb.aczg.model.Endereco

import java.sql.*

class EnderecoDAO implements IEnderecoDAO {

    private final IConexao conexao

    EnderecoDAO(IConexao conexao) {
        this.conexao = conexao
    }

    @Override
    Endereco inserir(Endereco endereco) {
        String sql = """
            INSERT INTO enderecos (cep, logradouro, numero, complemento, bairro, cidade, estado, pais)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
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
        } finally { con.close() }
    }

    @Override
    Endereco buscarPorId(int id) {
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM enderecos WHERE id = ?")
            stmt.setInt(1, id)
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) return mapear(rs)
        } finally { con.close() }
        return null
    }

    @Override
    Endereco buscarPorCep(String cep) {
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM enderecos WHERE cep = ? LIMIT 1")
            stmt.setString(1, cep.replaceAll('[^0-9]', ''))
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) return mapear(rs)
        } finally { con.close() }
        return null
    }

    @Override
    boolean atualizar(Endereco endereco) {
        String sql = """
            UPDATE enderecos
            SET cep=?, logradouro=?, numero=?, complemento=?, bairro=?, cidade=?, estado=?, pais=?
            WHERE id=?
        """
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(sql)
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
        } finally { con.close() }
    }

    @Override
    List<Endereco> listarTodos() {
        List<Endereco> lista = []
        Connection con = conexao.obterConexao()
        try {
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM enderecos ORDER BY cidade")
            while (rs.next()) lista << mapear(rs)
        } finally { con.close() }
        return lista
    }

    private Endereco mapear(ResultSet rs) {
        new Endereco(id: rs.getInt('id'), cep: rs.getString('cep'),
                logradouro: rs.getString('logradouro'), numero: rs.getString('numero'),
                complemento: rs.getString('complemento'), bairro: rs.getString('bairro'),
                cidade: rs.getString('cidade'), estado: rs.getString('estado'), pais: rs.getString('pais'))
    }
}
