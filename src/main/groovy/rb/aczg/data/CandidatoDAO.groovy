package rb.aczg.data

import rb.aczg.model.Candidato
import rb.aczg.model.Competencia

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class CandidatoDAO {
    private final CompetenciaDAO competenciaDAO = new CompetenciaDAO()

    //CREATE
    Candidato inserir(Candidato candidato) {
        String sql = """
            INSERT INTO candidatos (nome, sobrenome, email, cpf, idade, estado, cep, descricao)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            stmt.setString(1, candidato.nome)
            stmt.setString(2, candidato.sobrenome)
            stmt.setString(3, candidato.email)
            stmt.setString(4, candidato.cpf)
            stmt.setInt(5, candidato.idade)
            stmt.setString(6, candidato.estado)
            stmt.setString(7, candidato.cep)
            stmt.setString(8, candidato.descricao)
            stmt.executeUpdate()

            ResultSet keys = stmt.getGeneratedKeys()
            if (keys.next()) {
                candidato.id = keys.getInt(1)
            }
        } finally {
            conexao.close()
        }

        candidato.competencias.each { Competencia comp ->
            comp = competenciaDAO.inserir(comp)
            competenciaDAO.vincularCandidato(candidato.id, comp.id)
        }

        println "Candidato '${candidato.nome} ${candidato.sobrenome}' inserido com ID ${candidato.id}."
        return candidato
    }

    //READ
    List<Candidato> listarTodos() {
        String sql = "SELECT * FROM candidatos ORDER BY nome"
        List<Candidato> lista = []

        Connection conexao = ConexaoBD.obterConexao()
        try {
            Statement stmt = conexao.createStatement()
            ResultSet rs   = stmt.executeQuery(sql)
            while (rs.next()) {
                Candidato c = mapearResultSet(rs)
                c.competencias = competenciaDAO.buscarPorCandidato(c.id)
                lista << c
            }
        } finally {
            conexao.close()
        }
        return lista
    }

    Candidato buscarPorId(int id) {
        String sql = "SELECT * FROM candidatos WHERE id = ?"
        Candidato candidato = null

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) {
                candidato = mapearResultSet(rs)
                candidato.competencias = competenciaDAO.buscarPorCandidato(id)
            }
        } finally {
            conexao.close()
        }
        return candidato
    }

    //UPDATE
    boolean atualizar(Candidato candidato) {
        String sql = """
            UPDATE candidatos
            SET nome = ?, sobrenome = ?, email = ?, cpf = ?,
                idade = ?, estado = ?, cep = ?, descricao = ?
            WHERE id = ?
        """

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, candidato.nome)
            stmt.setString(2, candidato.sobrenome)
            stmt.setString(3, candidato.email)
            stmt.setString(4, candidato.cpf)
            stmt.setInt(5, candidato.idade)
            stmt.setString(6, candidato.estado)
            stmt.setString(7, candidato.cep)
            stmt.setString(8, candidato.descricao)
            stmt.setInt(9, candidato.id)
            int linhas = stmt.executeUpdate()

            if (linhas > 0) {
                println "✅ Candidato #${candidato.id} atualizado."
                return true
            }
            println "⚠️  Candidato #${candidato.id} não encontrado."
            return false
        } finally {
            conexao.close()
        }
    }

    //DELETE

    boolean deletar(int id) {
        String sql = "DELETE FROM candidatos WHERE id = ?"

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            int linhas = stmt.executeUpdate()
            if (linhas > 0) {
                println "✅ Candidato #${id} removido."
                return true
            }
            println "⚠️  Candidato #${id} não encontrado."
            return false
        } finally {
            conexao.close()
        }
    }

    //MATCH
    List<Candidato> matchPorVaga(int vagaId) {
        String sql = """
            SELECT DISTINCT c.*
            FROM candidatos c
            JOIN candidato_competencia cc ON cc.candidato_id = c.id
            JOIN vaga_competencia vc      ON vc.competencia_id = cc.competencia_id
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
                Candidato c = mapearResultSet(rs)
                c.competencias = competenciaDAO.buscarPorCandidato(c.id)
                lista << c
            }
        } finally {
            conexao.close()
        }
        return lista
    }

    //PRIVADO
    private Candidato mapearResultSet(ResultSet rs) {
        new Candidato(
                id: rs.getInt('id'),
                nome: rs.getString('nome'),
                sobrenome: rs.getString('sobrenome'),
                email: rs.getString('email'),
                cpf: rs.getString('cpf'),
                idade: rs.getInt('idade'),
                estado: rs.getString('estado'),
                cep: rs.getString('cep'),
                descricao: rs.getString('descricao')
        )
    }
}