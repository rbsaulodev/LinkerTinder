package rb.aczg.data

import rb.aczg.model.Empresa
import rb.aczg.model.Vaga

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Statement

class EmpresaDAO {

    private final VagaDAO vagaDAO = new VagaDAO()

    //CREATE
    Empresa inserir(Empresa empresa) {
        String sql = """
            INSERT INTO empresas (nome, email, cnpj, pais, estado, cep, descricao)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            stmt.setString(1, empresa.nome)
            stmt.setString(2, empresa.email)
            stmt.setString(3, empresa.cnpj)
            stmt.setString(4, empresa.pais)
            stmt.setString(5, empresa.estado)
            stmt.setString(6, empresa.cep)
            stmt.setString(7, empresa.descricao)
            stmt.executeUpdate()

            ResultSet keys = stmt.getGeneratedKeys()
            if (keys.next()) {
                empresa.id = keys.getInt(1)
            }
        } finally {
            conexao.close()
        }

        empresa.vagas.each { Vaga vaga ->
            vaga.empresaId = empresa.id
            vagaDAO.inserir(vaga)
        }

        println "Empresa '${empresa.nome}' inserida com ID ${empresa.id}."
        return empresa
    }

    //READ
    List<Empresa> listarTodas() {
        String sql = "SELECT * FROM empresas ORDER BY nome"
        List<Empresa> lista = []

        Connection conexao = ConexaoBD.obterConexao()
        try {
            Statement stmt = conexao.createStatement()
            ResultSet rs   = stmt.executeQuery(sql)
            while (rs.next()) {
                Empresa e = mapearResultSet(rs)
                e.vagas = vagaDAO.listarPorEmpresa(e.id)
                lista << e
            }
        } finally {
            conexao.close()
        }
        return lista
    }

    Empresa buscarPorId(int id) {
        String sql = "SELECT * FROM empresas WHERE id = ?"
        Empresa empresa = null

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) {
                empresa = mapearResultSet(rs)
                empresa.vagas = vagaDAO.listarPorEmpresa(id)
            }
        } finally {
            conexao.close()
        }
        return empresa
    }

    //UPDATE
    boolean atualizar(Empresa empresa) {
        String sql = """
            UPDATE empresas
            SET nome = ?, email = ?, cnpj = ?, pais = ?,
                estado = ?, cep = ?, descricao = ?
            WHERE id = ?
        """

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setString(1, empresa.nome)
            stmt.setString(2, empresa.email)
            stmt.setString(3, empresa.cnpj)
            stmt.setString(4, empresa.pais)
            stmt.setString(5, empresa.estado)
            stmt.setString(6, empresa.cep)
            stmt.setString(7, empresa.descricao)
            stmt.setInt(8, empresa.id)
            int linhas = stmt.executeUpdate()

            if (linhas > 0) {
                println "Empresa #${empresa.id} atualizada."
                return true
            }
            println "Empresa #${empresa.id} não encontrada."
            return false
        } finally {
            conexao.close()
        }
    }

    //DELETE
    boolean deletar(int id) {
        String sql = "DELETE FROM empresas WHERE id = ?"

        Connection conexao = ConexaoBD.obterConexao()
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql)
            stmt.setInt(1, id)
            int linhas = stmt.executeUpdate()
            if (linhas > 0) {
                println "Empresa #${id} removida."
                return true
            }
            println "Empresa #${id} não encontrada."
            return false
        } finally {
            conexao.close()
        }
    }

    //PRIVADO
    private Empresa mapearResultSet(ResultSet rs) {
        new Empresa(
                id: rs.getInt('id'),
                nome: rs.getString('nome'),
                email: rs.getString('email'),
                cnpj: rs.getString('cnpj'),
                pais: rs.getString('pais'),
                estado: rs.getString('estado'),
                cep: rs.getString('cep'),
                descricao: rs.getString('descricao')
        )
    }
}