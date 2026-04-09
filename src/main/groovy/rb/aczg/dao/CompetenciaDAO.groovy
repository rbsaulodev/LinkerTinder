package rb.aczg.dao

import rb.aczg.model.Competencia
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class CompetenciaDAO {

    Competencia inserir(Competencia competencia) {
        Competencia existente = buscarPorNome(competencia.nome)
        if (existente) {
            println "Competencia '${existente.nome}' ja existe (ID ${existente.id})."
            return existente
        }

        String sql = "INSERT INTO competencias (nome) VALUES (?) RETURNING id, nome"
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, competencia.nome.trim())
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) {
                competencia.id   = rs.getInt('id')
                competencia.nome = rs.getString('nome')
                println "Competencia '${competencia.nome}' inserida com ID ${competencia.id}."
            }
            return competencia
        } finally {
            conexao.close()
        }
    }

    List<Competencia> listarTodas() {
        String sql = "SELECT id, nome FROM competencias ORDER BY nome"
        List<Competencia> lista = []
        Connection conexao = ConexaoBD.obterConexao()
        try {
            ResultSet rs = conexao.createStatement().executeQuery(sql)
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
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) return new Competencia(id: rs.getInt('id'), nome: rs.getString('nome'))
        } finally {
            conexao.close()
        }
        return null
    }

    Competencia buscarPorNome(String nome) {
        String sql = "SELECT id, nome FROM competencias WHERE LOWER(nome) = LOWER(?)"
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, nome.trim())
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) return new Competencia(id: rs.getInt('id'), nome: rs.getString('nome'))
        } finally {
            conexao.close()
        }
        return null
    }

    List<Competencia> buscarPorCandidato(int candidatoId) {
        String sql = """
            SELECT c.id, c.nome, cc.nivel
            FROM competencias c
            JOIN candidato_competencias cc ON cc.competencia_id = c.id
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
                lista << new Competencia(id: rs.getInt('id'), nome: rs.getString('nome'),
                        nivel: rs.getString('nivel'))
            }
        } finally {
            conexao.close()
        }
        return lista
    }

    List<Competencia> buscarPorVaga(int vagaId) {
        String sql = """
            SELECT c.id, c.nome, vc.obrigatorio
            FROM competencias c
            JOIN vaga_competencias vc ON vc.competencia_id = c.id
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
                lista << new Competencia(id: rs.getInt('id'), nome: rs.getString('nome'),
                        obrigatorio: rs.getBoolean('obrigatorio'))
            }
        } finally {
            conexao.close()
        }
        return lista
    }

    boolean atualizar(Competencia competencia) {
        String sql = "UPDATE competencias SET nome = ? WHERE id = ?"
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, competencia.nome.trim())
            stmt.setInt(2, competencia.id)
            boolean ok = stmt.executeUpdate() > 0
            if (ok) println "Competencia #${competencia.id} atualizada."
            return ok
        } finally {
            conexao.close()
        }
    }

    boolean deletar(int id) {
        String sql = "DELETE FROM competencias WHERE id = ?"
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            boolean ok = stmt.executeUpdate() > 0
            if (ok) println "Competencia #${id} removida."
            return ok
        } finally {
            conexao.close()
        }
    }

    void vincularCandidato(int candidatoId, int competenciaId, String nivel = null) {
        String sql = """
            INSERT INTO candidato_competencias (candidato_id, competencia_id, nivel)
            VALUES (?, ?, ?) ON CONFLICT DO NOTHING
        """
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, candidatoId)
            stmt.setInt(2, competenciaId)
            stmt.setString(3, nivel)
            stmt.executeUpdate()
        } finally {
            conexao.close()
        }
    }

    void vincularVaga(int vagaId, int competenciaId, boolean obrigatorio = true) {
        String sql = """
            INSERT INTO vaga_competencias (vaga_id, competencia_id, obrigatorio)
            VALUES (?, ?, ?) ON CONFLICT DO NOTHING
        """
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, vagaId)
            stmt.setInt(2, competenciaId)
            stmt.setBoolean(3, obrigatorio)
            stmt.executeUpdate()
        } finally {
            conexao.close()
        }
    }

    void desvincularCandidato(int candidatoId, int competenciaId) {
        String sql = "DELETE FROM candidato_competencias WHERE candidato_id=? AND competencia_id=?"
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, candidatoId); stmt.setInt(2, competenciaId)
            stmt.executeUpdate()
        } finally {
            conexao.close()
        }
    }

    void desvincularVaga(int vagaId, int competenciaId) {
        String sql = "DELETE FROM vaga_competencias WHERE vaga_id=? AND competencia_id=?"
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, vagaId); stmt.setInt(2, competenciaId)
            stmt.executeUpdate()
        } finally {
            conexao.close()
        }
    }
}
