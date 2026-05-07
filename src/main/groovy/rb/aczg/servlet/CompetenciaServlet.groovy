package rb.aczg.servlet

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import rb.aczg.model.Competencia

@WebServlet(urlPatterns = ['/api/competencias', '/api/competencias/*'])
class CompetenciaServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        int id = extractId(req)
        try {
            if (id > 0) {
                Competencia Competencia = competenciaDAO.buscarPorId(id)
                Competencia ? ok(resp, Competencia) : notFound(resp, "Competencia não encontrado: id=$id")
            } else {
                ok(resp, competenciaDAO.listarTodas())
            }
        } catch (Exception e) {
            serverError(resp, "Erro ao buscar competencias: ${e.message}")
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Map dados = parseBody(req)
            if (!dados.nome) { badRequest(resp, "Campo 'nome' é obrigatório."); return }

            Competencia comp = new Competencia(nome: dados.nome as String)
            comp = competenciaDAO.inserir(comp)
            created(resp, comp)

        } catch (Exception e) {
            serverError(resp, "Erro ao cadastrar competência: ${e.message}")
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        int id = extractId(req)
        if (id <= 0) {
            badRequest(resp, "ID inválido. Use: /api/competencias/{id}");
            return
        }

        try {
            Map dados = parseBody(req)
            Competencia compt = mapToCompetencia(dados)
            compt.id = id

            boolean ok = competenciaDAO.atualizar(compt)
            ok ? ok(resp, competenciaDAO.buscarPorId(id))
                    : notFound(resp, "Competencia não encontrado: id=$id")

        } catch (Exception e) {
            serverError(resp, "Erro ao atualizar competencia: ${e.message}")
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        int id = extractId(req)
        if (id <= 0) { badRequest(resp, "ID inválido. Use: /api/competencias/{id}"); return }

        try {
            competenciaDAO.deletar(id) ? noContent(resp) : notFound(resp, "Competencia não encontrado: id=$id")
        } catch (Exception e) {
            serverError(resp, "Erro ao remover competencia: ${e.message}")
        }
    }

    private static Competencia mapToCompetencia(Map d) {

        Competencia comp = new Competencia(
                nome: d.nome as String,
                nivel: d.nivel as String,
                obrigatorio: d.obrigatorio as boolean
        )

        return comp
    }
}
