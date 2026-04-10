package rb.aczg.service

import rb.aczg.dao.CandidatoDAO
import rb.aczg.dao.CompetenciaDAO
import rb.aczg.dao.EnderecoDAO
import rb.aczg.dao.VagaDAO
import rb.aczg.model.Candidato
import rb.aczg.model.Competencia
import rb.aczg.model.Vaga

class CandidatoService {

    private final CandidatoDAO candidatoDAO = new CandidatoDAO()
    private final EnderecoDAO enderecoDAO = new EnderecoDAO()
    private final CompetenciaDAO competenciaDAO = new CompetenciaDAO()
    private final VagaDAO vagaDAO = new VagaDAO()

    Candidato cadastrar(Candidato candidato) {
        validar(candidato)
        if (candidato.endereco) {
            candidato.endereco = enderecoDAO.inserir(candidato.endereco)
        }
        return candidatoDAO.inserir(candidato)
    }

    List<Candidato> listarTodos() {
        return candidatoDAO.listarTodos()
    }

    Candidato buscarPorId(int id) {
        Candidato c = candidatoDAO.buscarPorId(id)
        if (!c) throw new RuntimeException("Candidato #${id} nao encontrado.")
        return c
    }

    Candidato atualizar(Candidato candidato) {
        validar(candidato)
        if (candidato.endereco?.id) {
            enderecoDAO.atualizar(candidato.endereco)
        }
        candidatoDAO.atualizar(candidato)
        return candidato
    }

    void remover(int id) {
        candidatoDAO.deletar(id)
    }

    void adicionarCompetencia(int candidatoId, String nomeCompetencia, String nivel = null) {
        Competencia comp = competenciaDAO.inserir(new Competencia(nome: nomeCompetencia))
        competenciaDAO.vincularCandidato(candidatoId, comp.id, nivel)
        println "Competencia '${comp.nome}' adicionada ao candidato #${candidatoId}."
    }

    void removerCompetencia(int candidatoId, int competenciaId) {
        competenciaDAO.desvincularCandidato(candidatoId, competenciaId)
        println "Competencia #${competenciaId} removida do candidato #${candidatoId}."
    }

    List<Vaga> verMatchesDeVagas(int candidatoId) {
        return vagaDAO.matchPorCandidato(candidatoId)
    }

    void curtirVaga(int candidatoId, int vagaId) {
        candidatoDAO.curtirVaga(candidatoId, vagaId)
        vagaDAO.gerarMatchSeAmbosCurtiram(candidatoId, vagaId)
    }

    private void validar(Candidato c) {
        if (!c.nome?.trim()) throw new IllegalArgumentException("Nome e obrigatorio.")
        if (!c.sobrenome?.trim()) throw new IllegalArgumentException("Sobrenome e obrigatorio.")
        if (!c.email?.trim()) throw new IllegalArgumentException("Email e obrigatorio.")
        if (!c.cpf?.trim()) throw new IllegalArgumentException("CPF e obrigatorio.")
    }
}
