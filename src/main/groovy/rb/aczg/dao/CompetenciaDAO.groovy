package rb.aczg.dao

import rb.aczg.interfaces.dao.ICompetenciaDAO
import rb.aczg.interfaces.dao.IConexao
import rb.aczg.model.Competencia

import java.sql.*

class CompetenciaDAO implements ICompetenciaDAO {

    private final IConexao conexao

    CompetenciaDAO(IConexao conexao) {
        this.conexao = conexao
    }

    @Override
    Competencia inserir(Competencia competencia) {
        Competencia existente = buscarPorNome(competencia.nome)
        if (existente) return existente

        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "INSERT INTO competencias (nome) VALUES (?) RETURNING id, nome")
            stmt.setString(1, competencia.nome.trim())
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) {
                competencia.id   = rs.getInt('id')
                competencia.nome = rs.getString('nome')
            }
            return competencia
        } finally { con.close() }
    }

    @Override
    List<Competencia> listarTodas() {
        List<Competencia> lista = []
        Connection con = conexao.obterConexao()
        try {
            ResultSet rs = con.createStatement()
                    .executeQuery("SELECT id, nome FROM competencias ORDER BY nome")
            while (rs.next())
                lista << new Competencia(id: rs.getInt('id'), nome: rs.getString('nome'))
        } finally { con.close() }
        return lista
    }

    @Override
    Competencia buscarPorId(int id) {
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT id, nome FROM competencias WHERE id = ?")
            stmt.setInt(1, id)
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) return new Competencia(id: rs.getInt('id'), nome: rs.getString('nome'))
        } finally { con.close() }
        return null
    }

    @Override
    Competencia buscarPorNome(String nome) {
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT id, nome FROM competencias WHERE LOWER(nome) = LOWER(?)")
            stmt.setString(1, nome.trim())
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) return new Competencia(id: rs.getInt('id'), nome: rs.getString('nome'))
        } finally { con.close() }
        return null
    }

    @Override
    List<Competencia> buscarPorCandidato(int candidatoId) {
        String sql = """
            SELECT c.id, c.nome, cc.nivel FROM competencias c
            JOIN candidato_competencias cc ON cc.competencia_id = c.id
            WHERE cc.candidato_id = ? ORDER BY c.nome
        """
        List<Competencia> lista = []
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(sql)
            stmt.setInt(1, candidatoId)
            ResultSet rs = stmt.executeQuery()
            while (rs.next())
                lista << new Competencia(id: rs.getInt('id'), nome: rs.getString('nome'),
                        nivel: rs.getString('nivel'))
        } finally { con.close() }
        return lista
    }

    @Override
    List<Competencia> buscarPorVaga(int vagaId) {
        String sql = """
            SELECT c.id, c.nome, vc.obrigatorio FROM competencias c
            JOIN vaga_competencias vc ON vc.competencia_id = c.id
            WHERE vc.vaga_id = ? ORDER BY c.nome
        """
        List<Competencia> lista = []
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(sql)
            stmt.setInt(1, vagaId)
            ResultSet rs = stmt.executeQuery()
            while (rs.next())
                lista << new Competencia(id: rs.getInt('id'), nome: rs.getString('nome'),
                        obrigatorio: rs.getBoolean('obrigatorio'))
        } finally { con.close() }
        return lista
    }

    @Override
    boolean atualizar(Competencia competencia) {
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "UPDATE competencias SET nome = ? WHERE id = ?")
            stmt.setString(1, competencia.nome.trim())
            stmt.setInt(2, competencia.id)
            return stmt.executeUpdate() > 0
        } finally { con.close() }
    }

    @Override
    boolean deletar(int id) {
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM competencias WHERE id = ?")
            stmt.setInt(1, id)
            return stmt.executeUpdate() > 0
        } finally { con.close() }
    }

    @Override
    void vincularCandidato(int candidatoId, int competenciaId, String nivel = null) {
        String sql = """
            INSERT INTO candidato_competencias (candidato_id, competencia_id, nivel)
            VALUES (?, ?, ?) ON CONFLICT DO NOTHING
        """
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(sql)
            stmt.setInt(1, candidatoId); stmt.setInt(2, competenciaId)
            stmt.setString(3, nivel)
            stmt.executeUpdate()
        } finally { con.close() }
    }

    @Override
    void vincularVaga(int vagaId, int competenciaId, boolean obrigatorio = true) {
        String sql = """
            INSERT INTO vaga_competencias (vaga_id, competencia_id, obrigatorio)
            VALUES (?, ?, ?) ON CONFLICT DO NOTHING
        """
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(sql)
            stmt.setInt(1, vagaId); stmt.setInt(2, competenciaId)
            stmt.setBoolean(3, obrigatorio)
            stmt.executeUpdate()
        } finally { con.close() }
    }

    @Override
    void desvincularCandidato(int candidatoId, int competenciaId) {
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "DELETE FROM candidato_competencias WHERE candidato_id=? AND competencia_id=?")
            stmt.setInt(1, candidatoId); stmt.setInt(2, competenciaId)
            stmt.executeUpdate()
        } finally { con.close() }
    }


    void desvincularVaga(int vagaId, int competenciaId) {
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "DELETE FROM vaga_competencias WHERE vaga_id=? AND competencia_id=?")
            stmt.setInt(1, vagaId); stmt.setInt(2, competenciaId)
            stmt.executeUpdate()
        } finally { con.close() }
    }
}
