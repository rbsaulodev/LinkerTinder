package rb.aczg.servlet

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import rb.aczg.model.Competencia

@WebServlet(urlPatterns = ['/api/competencias', '/api/competencias/*'])
class CompetenciaServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        try {
            ok(resp, competenciaDAO.listarTodas())
        } catch (Exception e) {
            serverError(resp, "Erro ao listar competências: ${e.message}")
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
}
