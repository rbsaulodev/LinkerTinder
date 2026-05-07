package rb.aczg.servlet

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import rb.aczg.model.Competencia
import rb.aczg.model.Vaga

@WebServlet(urlPatterns = ['/api/vagas', '/api/vagas/*'])
class VagaServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        int id = extractId(req)
        try {
            if (id > 0) {
                Vaga vaga = vagaDAO.buscarPorId(id)
                vaga ? ok(resp, vaga) : notFound(resp, "Vaga não encontrada: id=$id")
            } else {
                String empresaParam = req.getParameter('empresa')
                List<Vaga> lista = empresaParam
                    ? vagaDAO.listarPorEmpresa(empresaParam.toInteger())
                    : vagaDAO.listarTodas()
                ok(resp, lista)
            }
        } catch (Exception e) {
            serverError(resp, "Erro ao buscar vagas: ${e.message}")
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Map dados = parseBody(req)

            if (!dados.empresaId) {
                badRequest(resp, "Campo 'empresaId' é obrigatório.");
                return
            }
            if (!dados.titulo){
                badRequest(resp, "Campo 'titulo' é obrigatório.");
                return
            }

            Vaga vaga = mapToVaga(dados)
            vaga = vagaDAO.inserir(vaga)
            created(resp, vaga)

        } catch (Exception e) {
            serverError(resp, "Erro ao inserir vaga: ${e.message}")
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        int id = extractId(req)
        if (id <= 0) { badRequest(resp, "ID inválido. Use: /api/vagas/{id}"); return }

        try {
            Vaga vaga = mapToVaga(parseBody(req))
            vaga.id   = id

            vagaDAO.atualizar(vaga)
                ? ok(resp, vagaDAO.buscarPorId(id))
                : notFound(resp, "Vaga não encontrada: id=$id")

        } catch (Exception e) {
            serverError(resp, "Erro ao atualizar vaga: ${e.message}")
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        int id = extractId(req)
        if (id <= 0) { badRequest(resp, "ID inválido. Use: /api/vagas/{id}"); return }

        try {
            vagaDAO.deletar(id) ? noContent(resp) : notFound(resp, "Vaga não encontrada: id=$id")
        } catch (Exception e) {
            serverError(resp, "Erro ao remover vaga: ${e.message}")
        }
    }

    private static Vaga mapToVaga(Map d) {
        Vaga v = new Vaga(
            empresaId: (d.empresaId ?: 0) as int,
            titulo: d.titulo as String,
            descricao: d.descricao as String,
            status: (d.status ?: 'Aberta') as String
        )

        if (d.competencias instanceof List) {
            v.competencias = (d.competencias as List).collect { Map comp ->
                new Competencia(nome: comp.nome as String, obrigatorio: (comp.obrigatorio ?: false) as boolean)
            }
        }
        return v
    }
}
