package rb.aczg.dao.impl

import rb.aczg.interfaces.IConexao
import rb.aczg.interfaces.IEmpresaDAO
import rb.aczg.interfaces.IVagaDAO
import rb.aczg.model.Empresa
import rb.aczg.model.Endereco

import java.sql.*

class EmpresaDAO implements IEmpresaDAO {

    private final IConexao  conexao
    private final IVagaDAO  vagaDAO

    EmpresaDAO (IConexao conexao, IVagaDAO vagaDAO) {
        this.conexao = conexao
        this.vagaDAO = vagaDAO
    }

    @Override
    Empresa inserir(Empresa empresa) {
        String sql = """
            INSERT INTO empresas (nome, cnpj, email, descricao, senha_hash, endereco_id)
            VALUES (?, ?, ?, ?, ?, ?)
        """
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            stmt.setString(1, empresa.nome)
            stmt.setString(2, empresa.cnpj.replaceAll('[^0-9]', ''))
            stmt.setString(3, empresa.email)
            stmt.setString(4, empresa.descricao)
            stmt.setString(5, empresa.senhaHash ?: 'hash_placeholder')
            if (empresa.endereco?.id) stmt.setInt(6, empresa.endereco.id)
            else stmt.setNull(6, Types.INTEGER)
            stmt.executeUpdate()
            ResultSet keys = stmt.getGeneratedKeys()
            if (keys.next()) empresa.id = keys.getInt(1)
            println "Empresa '${empresa.nome}' inserida com ID ${empresa.id}."
            return empresa
        } finally { con.close() }
    }

    @Override
    List<Empresa> listarTodas() {
        String sql = """
            SELECT em.*, e.id as eid, e.cep, e.logradouro, e.numero, e.complemento,
                   e.bairro, e.cidade, e.estado, e.pais
            FROM empresas em LEFT JOIN enderecos e ON e.id = em.endereco_id ORDER BY em.nome
        """
        List<Empresa> lista = []
        Connection con = conexao.obterConexao()
        try {
            ResultSet rs = con.createStatement().executeQuery(sql)
            while (rs.next()) lista << mapear(rs)
        } finally { con.close() }
        return lista
    }

    @Override
    Empresa buscarPorId(int id) {
        String sql = """
            SELECT em.*, e.id as eid, e.cep, e.logradouro, e.numero, e.complemento,
                   e.bairro, e.cidade, e.estado, e.pais
            FROM empresas em LEFT JOIN enderecos e ON e.id = em.endereco_id WHERE em.id = ?
        """
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(sql)
            stmt.setInt(1, id)
            ResultSet rs = stmt.executeQuery()
            if (rs.next()) {
                Empresa emp = mapear(rs)
                emp.vagas = vagaDAO.listarPorEmpresa(id)
                return emp
            }
        } finally { con.close() }
        return null
    }

    @Override
    boolean atualizar(Empresa empresa) {
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement(
                    "UPDATE empresas SET nome=?, cnpj=?, email=?, descricao=? WHERE id=?")
            stmt.setString(1, empresa.nome)
            stmt.setString(2, empresa.cnpj.replaceAll('[^0-9]', ''))
            stmt.setString(3, empresa.email)
            stmt.setString(4, empresa.descricao)
            stmt.setInt(5, empresa.id)
            boolean ok = stmt.executeUpdate() > 0
            if (ok) println "Empresa #${empresa.id} atualizada."
            return ok
        } finally { con.close() }
    }

    @Override
    boolean deletar(int id) {
        Connection con = conexao.obterConexao()
        try {
            PreparedStatement stmt = con.prepareStatement("DELETE FROM empresas WHERE id=?")
            stmt.setInt(1, id)
            boolean ok = stmt.executeUpdate() > 0
            if (ok) println "Empresa #${id} removida."
            return ok
        } finally { con.close() }
    }

    private Empresa mapear(ResultSet rs) {
        Endereco end = null
        try {
            int eid = rs.getInt('eid')
            if (eid > 0) end = new Endereco(id: eid, cep: rs.getString('cep'),
                    logradouro: rs.getString('logradouro'), numero: rs.getString('numero'),
                    complemento: rs.getString('complemento'), bairro: rs.getString('bairro'),
                    cidade: rs.getString('cidade'), estado: rs.getString('estado'),
                    pais: rs.getString('pais'))
        } catch (Exception ignored) {}

        new Empresa(id: rs.getInt('id'), nome: rs.getString('nome'),
                cnpj: rs.getString('cnpj'), email: rs.getString('email'),
                descricao: rs.getString('descricao'), senhaHash: rs.getString('senha_hash'),
                endereco: end ?: new Endereco())
    }
}
