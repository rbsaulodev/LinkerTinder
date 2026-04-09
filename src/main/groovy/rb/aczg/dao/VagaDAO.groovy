package rb.aczg.dao

import rb.aczg.model.Endereco
import rb.aczg.model.Match
import rb.aczg.model.Vaga
import java.sql.*

class VagaDAO {

    private final CompetenciaDAO competenciaDAO = new CompetenciaDAO()

    Vaga inserir(Vaga vaga) {
        String sql = """
            INSERT INTO vagas (empresa_id, titulo, descricao, status, endereco_id)
            VALUES (?, ?, ?, ?, ?)
        """
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            stmt.setInt(1, vaga.empresaId)
            stmt.setString(2, vaga.titulo)
            stmt.setString(3, vaga.descricao)
            stmt.setString(4, vaga.status ?: 'Aberta')
            if (vaga.endereco?.id) {
                stmt.setInt(5, vaga.endereco.id)
            } else {
                stmt.setNull(5, Types.INTEGER)
            }
            stmt.executeUpdate()
            ResultSet keys = stmt.getGeneratedKeys()
            if (keys.next()) vaga.id = keys.getInt(1)
        } finally { conexao.close() }

        vaga.competencias.each { comp ->
            comp = competenciaDAO.inserir(comp)
            competenciaDAO.vincularVaga(vaga.id, comp.id)
        }
        println "Vaga '${vaga.titulo}' inserida com ID ${vaga.id}."
        return vaga
    }

    List<Vaga> listarTodas() {
        String sql = """
            SELECT v.*, em.nome AS nome_empresa,
                   e.id as eid, e.cep, e.logradouro, e.numero, e.complemento,
                   e.bairro, e.cidade, e.estado, e.pais
            FROM vagas v
            JOIN empresas em           ON em.id = v.empresa_id
            LEFT JOIN enderecos e      ON e.id = v.endereco_id
            ORDER BY v.titulo
        """
        List<Vaga> lista = []
        Connection conexao = ConexaoBD.obterConexao()
        try {
            ResultSet rs = conexao.createStatement().executeQuery(sql)
            while (rs.next()) {
                Vaga v = mapear(rs)
                v.competencias = competenciaDAO.buscarPorVaga(v.id)
                lista << v
            }
        } finally { conexao.close() }
        return lista
    }

    List<Vaga> listarPorEmpresa(int empresaId) {
        String sql = """
            SELECT v.*, em.nome AS nome_empresa,
                   e.id as eid, e.cep, e.logradouro, e.numero, e.complemento,
                   e.bairro, e.cidade, e.estado, e.pais
            FROM vagas v
            JOIN empresas em       ON em.id = v.empresa_id
            LEFT JOIN enderecos e  ON e.id = v.endereco_id
            WHERE v.empresa_id = ?
            ORDER BY v.titulo
        """
        List<Vaga> lista = []
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, empresaId)
            ResultSet rs = stmt.executeQuery()
            while (rs.next()) {
                Vaga v = mapear(rs)
                v.competencias = competenciaDAO.buscarPorVaga(v.id)
                lista << v
            }
        } finally { conexao.close() }
        return lista
    }

    Vaga buscarPorId(int id) {
        String sql = """
            SELECT v.*, em.nome AS nome_empresa,
                   e.id as eid, e.cep, e.logradouro, e.numero, e.complemento,
                   e.bairro, e.cidade, e.estado, e.pais
            FROM vagas v
            JOIN empresas em       ON em.id = v.empresa_id
            LEFT JOIN enderecos e  ON e.id = v.endereco_id
            WHERE v.id = ?
        """
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) {
                Vaga v = mapear(rs)
                v.competencias = competenciaDAO.buscarPorVaga(id)
                return v
            }
        } finally { conexao.close() }
        return null
    }

    boolean atualizar(Vaga vaga) {
        String sql = "UPDATE vagas SET titulo=?, descricao=?, status=? WHERE id=? AND empresa_id=?"
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, vaga.titulo)
            stmt.setString(2, vaga.descricao)
            stmt.setString(3, vaga.status ?: 'Aberta')
            stmt.setInt(4, vaga.id)
            stmt.setInt(5, vaga.empresaId)
            boolean ok = stmt.executeUpdate() > 0
            if (ok) println "Vaga #${vaga.id} atualizada."
            return ok
        } finally { conexao.close() }
    }

    boolean deletar(int id) {
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement("DELETE FROM vagas WHERE id=?")
            stmt.setInt(1, id)
            boolean ok = stmt.executeUpdate() > 0
            if (ok) println "Vaga #${id} removida."
            return ok
        } finally { conexao.close() }
    }

    void curtirCandidato(int vagaId, int candidatoId) {
        String sql = "INSERT INTO curtidas_vaga (vaga_id, candidato_id) VALUES (?, ?)"
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, vagaId); stmt.setInt(2, candidatoId)
            stmt.executeUpdate()
            println "Vaga #${vagaId} curtiu candidato #${candidatoId}."
        } finally { conexao.close() }
    }

    List<Vaga> matchPorCandidato(int candidatoId) {
        String sql = """
            SELECT DISTINCT v.*, em.nome AS nome_empresa,
                   e.id as eid, e.cep, e.logradouro, e.numero, e.complemento,
                   e.bairro, e.cidade, e.estado, e.pais
            FROM vagas v
            JOIN empresas em              ON em.id = v.empresa_id
            LEFT JOIN enderecos e         ON e.id = v.endereco_id
            JOIN vaga_competencias vc     ON vc.vaga_id = v.id
            JOIN candidato_competencias cc ON cc.competencia_id = vc.competencia_id
            WHERE cc.candidato_id = ?
            ORDER BY v.titulo
        """
        List<Vaga> lista = []
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, candidatoId)
            ResultSet rs = stmt.executeQuery()
            while (rs.next()) {
                Vaga v = mapear(rs)
                v.competencias = competenciaDAO.buscarPorVaga(v.id)
                lista << v
            }
        } finally { conexao.close() }
        return lista
    }

    Match gerarMatchSeAmbosCurtiram(int candidatoId, int vagaId) {
        String sqlC = "SELECT 1 FROM curtidas_candidato WHERE candidato_id=? AND vaga_id=?"
        String sqlV = "SELECT 1 FROM curtidas_vaga WHERE vaga_id=? AND candidato_id=?"

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmtC = conexao.prepareStatement(sqlC)
            stmtC.setInt(1, candidatoId); stmtC.setInt(2, vagaId)
            boolean candidatoCurtiu = stmtC.executeQuery().next()

            PreparedStatement stmtV = conexao.prepareStatement(sqlV)
            stmtV.setInt(1, vagaId); stmtV.setInt(2, candidatoId)
            boolean vagaCurtiu = stmtV.executeQuery().next()

            if (candidatoCurtiu && vagaCurtiu) {
                String sqlM = """
                    INSERT INTO matches (candidato_id, vaga_id)
                    VALUES (?, ?)
                    ON CONFLICT DO NOTHING
                    RETURNING id, matched_em
                """
                PreparedStatement stmtM = conexao.prepareStatement(sqlM)
                stmtM.setInt(1, candidatoId); stmtM.setInt(2, vagaId)
                ResultSet rs = stmtM.executeQuery()
                if (rs.next()) {
                    println "MATCH! Candidato #${candidatoId} x Vaga #${vagaId}"
                    return new Match(id: rs.getInt('id'), candidatoId: candidatoId,
                            vagaId: vagaId,
                            matchedEm: rs.getTimestamp('matched_em').toLocalDateTime())
                }
            }
        } finally { conexao.close() }
        return null
    }

    List<Match> listarMatchesPorCandidato(int candidatoId) {
        String sql = """
            SELECT m.*, c.nome AS nome_candidato, em.nome AS nome_empresa, v.titulo AS titulo_vaga
            FROM matches m
            JOIN candidatos c ON c.id = m.candidato_id
            JOIN vagas v      ON v.id = m.vaga_id
            JOIN empresas em  ON em.id = v.empresa_id
            WHERE m.candidato_id = ?
            ORDER BY m.matched_em DESC
        """
        List<Match> lista = []
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, candidatoId)
            ResultSet rs = stmt.executeQuery()
            while (rs.next()) lista << mapearMatch(rs)
        } finally { conexao.close() }
        return lista
    }

    List<Match> listarMatchesPorVaga(int vagaId) {
        String sql = """
            SELECT m.*, c.nome AS nome_candidato, em.nome AS nome_empresa, v.titulo AS titulo_vaga
            FROM matches m
            JOIN candidatos c ON c.id = m.candidato_id
            JOIN vagas v      ON v.id = m.vaga_id
            JOIN empresas em  ON em.id = v.empresa_id
            WHERE m.vaga_id = ?
            ORDER BY m.matched_em DESC
        """
        List<Match> lista = []
        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, vagaId)
            ResultSet rs = stmt.executeQuery()
            while (rs.next()) lista << mapearMatch(rs)
        } finally { conexao.close() }
        return lista
    }

    private Vaga mapear(ResultSet rs) {
        Endereco end = null
        try {
            int eid = rs.getInt('eid')
            if (eid > 0) {
                end = new Endereco(id: eid, cep: rs.getString('cep'),
                        logradouro: rs.getString('logradouro'), numero: rs.getString('numero'),
                        complemento: rs.getString('complemento'), bairro: rs.getString('bairro'),
                        cidade: rs.getString('cidade'), estado: rs.getString('estado'),
                        pais: rs.getString('pais'))
            }
        } catch (Exception ignored) {}

        new Vaga(id: rs.getInt('id'), empresaId: rs.getInt('empresa_id'),
                nomeEmpresa: rs.getString('nome_empresa'), titulo: rs.getString('titulo'),
                descricao: rs.getString('descricao'), status: rs.getString('status'),
                enderecoId: rs.getInt('endereco_id'), endereco: end ?: new Endereco())
    }

    private Match mapearMatch(ResultSet rs) {
        new Match(id: rs.getInt('id'),
                candidatoId:   rs.getInt('candidato_id'),
                nomeCanditado: rs.getString('nome_candidato'),
                vagaId:        rs.getInt('vaga_id'),
                tituloVaga:    rs.getString('titulo_vaga'),
                nomeEmpresa:   rs.getString('nome_empresa'),
                matchedEm:     rs.getTimestamp('matched_em').toLocalDateTime()
        )
    }
}
