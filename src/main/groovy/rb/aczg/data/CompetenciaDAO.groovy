package rb.aczg.data

import rb.aczg.model.Competencia

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement


class CompetenciaDAO {

    Competencia inserir(Competencia competencia) {
        String sql = "INSERT INTO competencias (nome) VALUES (?) ON CONFLICT (nome) DO NOTHING RETURNING id, nome"

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, competencia.nome.trim())
            ResultSet rs = stmt.executeQuery()

            if (rs.next()) {
                competencia.id   = rs.getInt('id')
                competencia.nome = rs.getString('nome')
                println "Competência '${competencia.nome}' inserida com ID ${competencia.id}."
            } else {

                competencia = buscarPorNome(competencia.nome)
                println "Competência '${competencia.nome}' já existe (ID ${competencia.id})."
            }
        } finally {
            conexao.close()
        }
        return competencia
    }

    // READ

    List<Competencia> listarTodas() {
        String sql = "SELECT id, nome FROM competencias ORDER BY nome"
        List<Competencia> lista = []

        Connection conexao = ConexaoBD.obterConexao()
        try {
            Statement stmt = conexao.createStatement()
            ResultSet rs   = stmt.executeQuery(sql)
            while (rs.next()) {
                lista << new Competencia(id: rs.getInt('id'), nome: rs.getString('nome'))
            }
        } finally {
            conexao.close()
        }
        return lista
    }

    Competencia buscarPorId(int id) {
        String sql = "SELECT id, nome FROM competencias WHERE id = ?"
        Competencia competencia = null

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) {
                competencia = new Competencia(id: rs.getInt('id'), nome: rs.getString('nome'))
            }
        } finally {
            conexao.close()
        }
        return competencia
    }

    Competencia buscarPorNome(String nome) {
        String sql = "SELECT id, nome FROM competencias WHERE LOWER(nome) = LOWER(?)"
        Competencia competencia = null

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, nome.trim())
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) {
                competencia = new Competencia(id: rs.getInt('id'), nome: rs.getString('nome'))
            }
        } finally {
            conexao.close()
        }
        return competencia
    }

    List<Competencia> buscarPorCandidato(int candidatoId) {
        String sql = """
            SELECT c.id, c.nome
            FROM competencias c
            JOIN candidato_competencia cc ON cc.competencia_id = c.id
            WHERE cc.candidato_id = ?
            ORDER BY c.nome
        """
        List<Competencia> lista = []

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, candidatoId)
            ResultSet rs = stmt.executeQuery()
            while (rs.next()) {
                lista << new Competencia(id: rs.getInt('id'), nome: rs.getString('nome'))
            }
        } finally {
            conexao.close()
        }
        return lista
    }

    List<Competencia> buscarPorVaga(int vagaId) {
        String sql = """
            SELECT c.id, c.nome
            FROM competencias c
            JOIN vaga_competencia vc ON vc.competencia_id = c.id
            WHERE vc.vaga_id = ?
            ORDER BY c.nome
        """
        List<Competencia> lista = []

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, vagaId)
            ResultSet rs = stmt.executeQuery()
            while (rs.next()) {
                lista << new Competencia(id: rs.getInt('id'), nome: rs.getString('nome'))
            }
        } finally {
            conexao.close()
        }
        return lista
    }

    //UPDATE
    boolean atualizar(Competencia competencia) {
        String sql = "UPDATE competencias SET nome = ? WHERE id = ?"

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, competencia.nome.trim())
            stmt.setInt(2, competencia.id)
            int linhas = stmt.executeUpdate()
            if (linhas > 0) {
                println "Competência #${competencia.id} atualizada."
                return true
            }
            println "Competência #${competencia.id} não encontrada."
            return false
        } finally {
            conexao.close()
        }
    }

    //DELETE
    boolean deletar(int id) {
        String sql = "DELETE FROM competencias WHERE id = ?"

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            int linhas = stmt.executeUpdate()
            if (linhas > 0) {
                println "Competência #${id} removida."
                return true
            }
            println "Competência #${id} não encontrada."
            return false
        } finally {
            conexao.close()
        }
    }

    //RELACIONAMENTOS N:N
    void vincularCandidato(int candidatoId, int competenciaId) {
        String sql = """
            INSERT INTO candidato_competencia (candidato_id, competencia_id)
            VALUES (?, ?)
            ON CONFLICT DO NOTHING
        """
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, candidatoId)
            stmt.setInt(2, competenciaId)
            stmt.executeUpdate()
        } finally {
            conexao.close()
        }
    }

    void vincularVaga(int vagaId, int competenciaId) {
        String sql = """
            INSERT INTO vaga_competencia (vaga_id, competencia_id)
            VALUES (?, ?)
            ON CONFLICT DO NOTHING
        """
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, vagaId)
            stmt.setInt(2, competenciaId)
            stmt.executeUpdate()
        } finally {
            conexao.close()
        }
    }

    void desvincularCandidato(int candidatoId, int competenciaId) {
        String sql = "DELETE FROM candidato_competencia WHERE candidato_id = ? AND competencia_id = ?"
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, candidatoId)
            stmt.setInt(2, competenciaId)
            stmt.executeUpdate()
        } finally {
            conexao.close()
        }
    }

    void desvincularVaga(int vagaId, int competenciaId) {
        String sql = "DELETE FROM vaga_competencia WHERE vaga_id = ? AND competencia_id = ?"
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, vagaId)
            stmt.setInt(2, competenciaId)
            stmt.executeUpdate()
        } finally {
            conexao.close()
        }
    }
}