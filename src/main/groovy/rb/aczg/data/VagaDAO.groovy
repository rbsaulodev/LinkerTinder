package rb.aczg.data

import rb.aczg.model.Competencia
import rb.aczg.model.Vaga

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class VagaDAO {

    private final CompetenciaDAO competenciaDAO = new CompetenciaDAO()

    //CREATE
    Vaga inserir(Vaga vaga) {
        String sql = """
            INSERT INTO vagas (empresa_id, titulo, descricao, local, salario)
            VALUES (?, ?, ?, ?, ?)
        """

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            stmt.setInt(1, vaga.empresaId)
            stmt.setString(2, vaga.titulo)
            stmt.setString(3, vaga.descricao)
            stmt.setString(4, vaga.local)
            if (vaga.salario != null) {
                stmt.setBigDecimal(5, vaga.salario)
            } else {
                stmt.setNull(5, java.sql.Types.NUMERIC)
            }
            stmt.executeUpdate()

            ResultSet keys = stmt.getGeneratedKeys()
            if (keys.next()) {
                vaga.id = keys.getInt(1)
            }
        } finally {
            conexao.close()
        }

        vaga.competencias.each { Competencia comp ->
            comp = competenciaDAO.inserir(comp)
            competenciaDAO.vincularVaga(vaga.id, comp.id)
        }

        println "Vaga '${vaga.titulo}' inserida com ID ${vaga.id}."
        return vaga
    }

    //READ
    List<Vaga> listarTodas() {
        String sql = """
            SELECT v.*, e.nome AS nome_empresa
            FROM vagas v
            JOIN empresas e ON e.id = v.empresa_id
            ORDER BY v.titulo
        """
        List<Vaga> lista = []

        Connection conexao = ConexaoBD.obterConexao()
        try {
            Statement stmt = conexao.createStatement()
            ResultSet rs   = stmt.executeQuery(sql)
            while (rs.next()) {
                Vaga v = mapearResultSet(rs)
                v.competencias = competenciaDAO.buscarPorVaga(v.id)
                lista << v
            }
        } finally {
            conexao.close()
        }
        return lista
    }

    List<Vaga> listarPorEmpresa(int empresaId) {
        String sql = """
            SELECT v.*, e.nome AS nome_empresa
            FROM vagas v
            JOIN empresas e ON e.id = v.empresa_id
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
                Vaga v = mapearResultSet(rs)
                v.competencias = competenciaDAO.buscarPorVaga(v.id)
                lista << v
            }
        } finally {
            conexao.close()
        }
        return lista
    }

    Vaga buscarPorId(int id) {
        String sql = """
            SELECT v.*, e.nome AS nome_empresa
            FROM vagas v
            JOIN empresas e ON e.id = v.empresa_id
            WHERE v.id = ?
        """
        Vaga vaga = null

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) {
                vaga = mapearResultSet(rs)
                vaga.competencias = competenciaDAO.buscarPorVaga(id)
            }
        } finally {
            conexao.close()
        }
        return vaga
    }

    //UPDATE
    boolean atualizar(Vaga vaga) {
        String sql = """
            UPDATE vagas
            SET titulo = ?, descricao = ?, local = ?, salario = ?
            WHERE id = ? AND empresa_id = ?
        """

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, vaga.titulo)
            stmt.setString(2, vaga.descricao)
            stmt.setString(3, vaga.local)
            if (vaga.salario != null) {
                stmt.setBigDecimal(4, vaga.salario)
            } else {
                stmt.setNull(4, java.sql.Types.NUMERIC)
            }
            stmt.setInt(5, vaga.id)
            stmt.setInt(6, vaga.empresaId)
            int linhas = stmt.executeUpdate()

            if (linhas > 0) {
                println "Vaga #${vaga.id} atualizada."
                return true
            }
            println "Vaga #${vaga.id} não encontrada ou não pertence à empresa."
            return false
        } finally {
            conexao.close()
        }
    }

    //DELETE
    boolean deletar(int id) {
        String sql = "DELETE FROM vagas WHERE id = ?"

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            int linhas = stmt.executeUpdate()
            if (linhas > 0) {
                println "Vaga #${id} removida."
                return true
            }
            println "Vaga #${id} não encontrada."
            return false
        } finally {
            conexao.close()
        }
    }

    //MATCH
    List<Vaga> matchPorCandidato(int candidatoId) {
        String sql = """
            SELECT DISTINCT v.*, e.nome AS nome_empresa
            FROM vagas v
            JOIN empresas e            ON e.id = v.empresa_id
            JOIN vaga_competencia vc   ON vc.vaga_id = v.id
            JOIN candidato_competencia cc ON cc.competencia_id = vc.competencia_id
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
                Vaga v = mapearResultSet(rs)
                v.competencias = competenciaDAO.buscarPorVaga(v.id)
                lista << v
            }
        } finally {
            conexao.close()
        }
        return lista
    }

    //PRIVADO
    private Vaga mapearResultSet(ResultSet rs) {
        new Vaga(
                id: rs.getInt('id'),
                empresaId: rs.getInt('empresa_id'),
                nomeEmpresa: rs.getString('nome_empresa'),
                titulo: rs.getString('titulo'),
                descricao: rs.getString('descricao'),
                local: rs.getString('local'),
                salario: rs.getBigDecimal('salario')
        )
    }
}