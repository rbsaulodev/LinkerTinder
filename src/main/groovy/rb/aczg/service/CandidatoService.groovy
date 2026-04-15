package rb.aczg.service

import rb.aczg.interfaces.ICandidatoDAO
import rb.aczg.interfaces.ICompetenciaDAO
import rb.aczg.interfaces.IEnderecoDAO
import rb.aczg.interfaces.IVagaDAO
import rb.aczg.model.Candidato
import rb.aczg.model.Competencia
import rb.aczg.model.Vaga
import rb.aczg.service.validation.CandidatoValidator


class CandidatoService{

    private final ICandidatoDAO    candidatoDAO
    private final IEnderecoDAO     enderecoDAO
    private final ICompetenciaDAO  competenciaDAO
    private final IVagaDAO         vagaDAO
    private final CandidatoValidator validator

    CandidatoService(
            ICandidatoDAO candidatoDAO,
            IEnderecoDAO enderecoDAO,
            ICompetenciaDAO competenciaDAO,
            IVagaDAO vagaDAO,
            CandidatoValidator validator) {
        this.candidatoDAO = candidatoDAO
        this.enderecoDAO = enderecoDAO
        this.competenciaDAO = competenciaDAO
        this.vagaDAO = vagaDAO
        this.validator = validator
    }

    Candidato cadastrar(Candidato candidato) {
        validator.validar(candidato)
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
        validator.validar(candidato)
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
}
