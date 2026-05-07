package rb.aczg.servlet

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import rb.aczg.model.Candidato
import rb.aczg.model.Competencia
import rb.aczg.model.Endereco

import java.time.LocalDate

@WebServlet(urlPatterns = ['/api/candidatos', '/api/candidatos/*'])
class CandidatoServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        int id = extractId(req)
        try {
            if (id > 0) {
                Candidato candidato = candidatoDAO.buscarPorId(id)
                candidato ? ok(resp, candidato) : notFound(resp, "Candidato não encontrado: id=$id")
            } else {
                ok(resp, candidatoDAO.listarTodos())
            }
        } catch (Exception e) {
            serverError(resp, "Erro ao buscar candidatos: ${e.message}")
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Map dados = parseBody(req)

            if (!dados.nome)  {
                badRequest(resp, "Campo 'nome' é obrigatório."); return
            }
            if (!dados.email) {
                badRequest(resp, "Campo 'email' é obrigatório."); return
            }
            if (!dados.cpf)   {
                badRequest(resp, "Campo 'cpf' é obrigatório."); return
            }

            Candidato candidato = mapToCandidato(dados)
            candidato = candidatoDAO.inserir(candidato)
            created(resp, candidato)

        } catch (Exception e) {
            serverError(resp, "Erro ao cadastrar candidato: ${e.message}")
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        int id = extractId(req)
        if (id <= 0) {
            badRequest(resp, "ID inválido. Use: /api/candidatos/{id}");
            return
        }

        try {
            Map dados = parseBody(req)
            Candidato cand = mapToCandidato(dados)
            cand.id = id

            boolean ok = candidatoDAO.atualizar(cand)
            ok ? ok(resp, candidatoDAO.buscarPorId(id))
               : notFound(resp, "Candidato não encontrado: id=$id")

        } catch (Exception e) {
            serverError(resp, "Erro ao atualizar candidato: ${e.message}")
        }
    }



    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        int id = extractId(req)
        if (id <= 0) { badRequest(resp, "ID inválido. Use: /api/candidatos/{id}"); return }

        try {
            candidatoDAO.deletar(id) ? noContent(resp) : notFound(resp, "Candidato não encontrado: id=$id")
        } catch (Exception e) {
            serverError(resp, "Erro ao remover candidato: ${e.message}")
        }
    }

    private static Candidato mapToCandidato(Map d) {
        Candidato c = new Candidato(
            nome: d.nome as String,
            sobrenome: d.sobrenome as String,
            email: d.email as String,
            cpf: d.cpf as String,
            descricao: d.descricao as String,
            senhaHash: d.senhaHash as String
        )

        if (d.dataNasc) {
            c.dataNasc = LocalDate.parse(d.dataNasc as String)
        }

        if (d.endereco instanceof Map) {
            Map end = d.endereco as Map
            c.endereco = new Endereco(
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

        if (d.competencias instanceof List) {
            c.competencias = (d.competencias as List).collect { Map comp ->
                new Competencia(nome: comp.nome as String, nivel: comp.nivel as String)
            }
        }

        return c
    }
}
