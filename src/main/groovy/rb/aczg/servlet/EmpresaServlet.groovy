package rb.aczg.servlet

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import rb.aczg.model.Empresa
import rb.aczg.model.Endereco

@WebServlet(urlPatterns = ['/api/empresas', '/api/empresas/*'])
class EmpresaServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        int id = extractId(req)
        try {
            if (id > 0) {
                Empresa empresa = empresaDAO.buscarPorId(id)
                empresa ? ok(resp, empresa) : notFound(resp, "Empresa não encontrada: id=$id")
            } else {
                ok(resp, empresaDAO.listarTodas())
            }
        } catch (Exception e) {
            serverError(resp, "Erro ao buscar empresas: ${e.message}")
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Map dados = parseBody(req)

            if (!dados.nome){
                badRequest(resp, "Campo 'nome' é obrigatório.");
                return
            }
            if (!dados.cnpj){
                badRequest(resp, "Campo 'cnpj' é obrigatório.");
                return
            }

            if (!dados.email){
                badRequest(resp, "Campo 'email' é obrigatório.");
                return
            }

            Empresa empresa = mapToEmpresa(dados)
            empresa = empresaDAO.inserir(empresa)
            created(resp, empresa)

        } catch (Exception e) {
            serverError(resp, "Erro ao cadastrar empresa: ${e.message}")
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        int id = extractId(req)
        if (id <= 0) { badRequest(resp, "ID inválido. Use: /api/empresas/{id}"); return }

        try {
            Empresa empresa = mapToEmpresa(parseBody(req))
            empresa.id      = id

            empresaDAO.atualizar(empresa)
                ? ok(resp, empresaDAO.buscarPorId(id))
                : notFound(resp, "Empresa não encontrada: id=$id")

        } catch (Exception e) {
            serverError(resp, "Erro ao atualizar empresa: ${e.message}")
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        int id = extractId(req)
        if (id <= 0) { badRequest(resp, "ID inválido. Use: /api/empresas/{id}"); return }

        try {
            empresaDAO.deletar(id) ? noContent(resp) : notFound(resp, "Empresa não encontrada: id=$id")
        } catch (Exception e) {
            serverError(resp, "Erro ao remover empresa: ${e.message}")
        }
    }

    private static Empresa mapToEmpresa(Map d) {
        Empresa e = new Empresa(
            nome: d.nome as String,
            cnpj: d.cnpj as String,
            email: d.email as String,
            descricao: d.descricao as String,
            senhaHash: d.senhaHash as String
        )

        if (d.endereco instanceof Map) {
            Map end = d.endereco as Map
            e.endereco = new Endereco(
                id: (end.id ?: 0) as int,
                cep: end.cep as String,
                logradouro:  end.logradouro as String,
                numero: end.numero as String,
                complemento: end.complemento as String,
                bairro: end.bairro as String,
                cidade: end.cidade as String,
                estado: end.estado as String,
                pais: end.pais as String
            )
        }
        return e
    }
}
