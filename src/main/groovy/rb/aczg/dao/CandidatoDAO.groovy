package rb.aczg.dao

import rb.aczg.model.Candidato
import rb.aczg.model.Competencia
import rb.aczg.model.Endereco
import java.sql.*

class CandidatoDAO {

    private final CompetenciaDAO competenciaDAO = new CompetenciaDAO()

    Candidato inserir(Candidato candidato) {
        String sql = """
            INSERT INTO candidatos (nome, sobrenome, email, cpf, data_nasc, descricao, senha_hash, endereco_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            stmt.setString(1, candidato.nome)
            stmt.setString(2, candidato.sobrenome)
            stmt.setString(3, candidato.email)
            stmt.setString(4, candidato.cpf.replaceAll('[^0-9]', ''))
            stmt.setDate(5, Date.valueOf(candidato.dataNasc))
            stmt.setString(6, candidato.descricao)
            stmt.setString(7, candidato.senhaHash ?: 'hash_placeholder')

            if (candidato.endereco?.id) {
                stmt.setInt(8, candidato.endereco.id)
            } else {
                stmt.setNull(8, Types.INTEGER)
            }
            stmt.executeUpdate()
            ResultSet keys = stmt.getGeneratedKeys()
            if (keys.next()) candidato.id = keys.getInt(1)

            candidato.competencias.each { Competencia comp ->
                competenciaDAO.vincularCandidato(candidato.id, comp.id, comp.nivel)
            }
            println "Candidato '${candidato.nome} ${candidato.sobrenome}' inserido com ID ${candidato.id}."
            return candidato
        } finally {
            conexao.close()
        }
    }

    List<Candidato> listarTodos() {
        String sql = """
            SELECT c.*, e.id as eid, e.cep, e.logradouro, e.numero, e.complemento,
                   e.bairro, e.cidade, e.estado, e.pais
            FROM candidatos c
            LEFT JOIN enderecos e ON e.id = c.endereco_id
            ORDER BY c.nome
        """
        List<Candidato> lista = []
        Connection conexao = ConexaoBD.obterConexao()
        try {
            ResultSet rs = conexao.createStatement().executeQuery(sql)
            while (rs.next()) {
                Candidato c = mapear(rs)
                c.competencias = competenciaDAO.buscarPorCandidato(c.id)
                lista << c
            }
        } finally { conexao.close() }
        return lista
    }

    Candidato buscarPorId(int id) {
        String sql = """
            SELECT c.*, e.id as eid, e.cep, e.logradouro, e.numero, e.complemento,
                   e.bairro, e.cidade, e.estado, e.pais
            FROM candidatos c
            LEFT JOIN enderecos e ON e.id = c.endereco_id
            WHERE c.id = ?
        """
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) {
                Candidato c = mapear(rs)
                c.competencias = competenciaDAO.buscarPorCandidato(id)
                return c
            }
        } finally { conexao.close() }
        return null
    }

    boolean atualizar(Candidato candidato) {
        String sql = """
            UPDATE candidatos
            SET nome=?, sobrenome=?, email=?, cpf=?, data_nasc=?, descricao=?
            WHERE id=?
        """
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, candidato.nome)
            stmt.setString(2, candidato.sobrenome)
            stmt.setString(3, candidato.email)
            stmt.setString(4, candidato.cpf.replaceAll('[^0-9]', ''))
            stmt.setDate(5, Date.valueOf(candidato.dataNasc))
            stmt.setString(6, candidato.descricao)
            stmt.setInt(7, candidato.id)
            boolean ok = stmt.executeUpdate() > 0
            if (ok) println "Candidato #${candidato.id} atualizado."
            return ok
        } finally { conexao.close() }
    }

    boolean deletar(int id) {
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement("DELETE FROM candidatos WHERE id=?")
            stmt.setInt(1, id)
            boolean ok = stmt.executeUpdate() > 0
            if (ok) println "Candidato #${id} removido."
            return ok
        } finally { conexao.close() }
    }

    void curtirVaga(int candidatoId, int vagaId) {
        String sql = "INSERT INTO curtidas_candidato (candidato_id, vaga_id) VALUES (?, ?)"
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, candidatoId); stmt.setInt(2, vagaId)
            stmt.executeUpdate()
            println "Candidato #${candidatoId} curtiu vaga #${vagaId}."
        } finally { conexao.close() }
    }

    List<Candidato> matchPorVaga(int vagaId) {
        String sql = """
            SELECT DISTINCT c.*, e.id as eid, e.cep, e.logradouro, e.numero, e.complemento,
                   e.bairro, e.cidade, e.estado, e.pais
            FROM candidatos c
            LEFT JOIN enderecos e           ON e.id = c.endereco_id
            JOIN candidato_competencias cc  ON cc.candidato_id = c.id
            JOIN vaga_competencias vc       ON vc.competencia_id = cc.competencia_id
            WHERE vc.vaga_id = ?
            ORDER BY c.nome
        """
        List<Candidato> lista = []
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, vagaId)
            ResultSet rs = stmt.executeQuery()
            while (rs.next()) {
                Candidato c = mapear(rs)
                c.competencias = competenciaDAO.buscarPorCandidato(c.id)
                lista << c
            }
        } finally { conexao.close() }
        return lista
    }

    private Candidato mapear(ResultSet rs) {
        Endereco end = null
        try {
            int eid = rs.getInt('eid')
            if (eid > 0) {
                end = new Endereco(
                        id:          eid,
                        cep:         rs.getString('cep'),
                        logradouro:  rs.getString('logradouro'),
                        numero:      rs.getString('numero'),
                        complemento: rs.getString('complemento'),
                        bairro:      rs.getString('bairro'),
                        cidade:      rs.getString('cidade'),
                        estado:      rs.getString('estado'),
                        pais:        rs.getString('pais')
                )
            }
        } catch (Exception ignored) {}

        new Candidato(
                id:        rs.getInt('id'),
                nome:      rs.getString('nome'),
                sobrenome: rs.getString('sobrenome'),
                email:     rs.getString('email'),
                cpf:       rs.getString('cpf'),
                dataNasc:  rs.getDate('data_nasc').toLocalDate(),
                descricao: rs.getString('descricao'),
                senhaHash: rs.getString('senha_hash'),
                endereco:  end ?: new Endereco()
        )
    }
}
