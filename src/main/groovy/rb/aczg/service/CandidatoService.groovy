package rb.aczg.service

import rb.aczg.interfaces.dao.ICandidatoDAO
import rb.aczg.interfaces.dao.ICompetenciaDAO
import rb.aczg.interfaces.dao.IEnderecoDAO
import rb.aczg.interfaces.dao.IVagaDAO
import rb.aczg.interfaces.service.ICandidatoService
import rb.aczg.model.Candidato
import rb.aczg.model.Competencia
import rb.aczg.model.Match
import rb.aczg.model.Vaga
import rb.aczg.service.validation.CandidatoValidator

class CandidatoService implements ICandidatoService {

    private final ICandidatoDAO candidatoDAO
    private final IEnderecoDAO enderecoDAO
    private final ICompetenciaDAO competenciaDAO
    private final IVagaDAO vagaDAO
    private final CandidatoValidator validator

    CandidatoService(
            ICandidatoDAO candidatoDAO,
            IEnderecoDAO enderecoDAO,
            ICompetenciaDAO competenciaDAO,
            IVagaDAO vagaDAO,
            CandidatoValidator validator) {
        this.candidatoDAO  = candidatoDAO
        this.enderecoDAO   = enderecoDAO
        this.competenciaDAO = competenciaDAO
        this.vagaDAO        = vagaDAO
        this.validator      = validator
    }

    @Override
    Candidato cadastrar(Candidato candidato) {
        validator.validar(candidato)
        if (candidato.endereco) {
            candidato.endereco = enderecoDAO.inserir(candidato.endereco)
        }
        return candidatoDAO.inserir(candidato)
    }

    @Override
    List<Candidato> listarTodos() {
        return candidatoDAO.listarTodos()
    }

    @Override
    Candidato buscarPorId(int id) {
        Candidato c = candidatoDAO.buscarPorId(id)
        if (!c) throw new RuntimeException("Candidato #${id} nao encontrado.")
        return c
    }

    @Override
    Candidato atualizar(Candidato candidato) {
        validator.validar(candidato)
        if (candidato.endereco?.id) {
            enderecoDAO.atualizar(candidato.endereco)
        }
        candidatoDAO.atualizar(candidato)
        return candidato
    }

    @Override
    void remover(int id) {
        candidatoDAO.deletar(id)
    }

    @Override
    void adicionarCompetencia(int candidatoId, String nomeCompetencia, String nivel = null) {
        Competencia comp = competenciaDAO.inserir(new Competencia(nome: nomeCompetencia))
        competenciaDAO.vincularCandidato(candidatoId, comp.id, nivel)
        println "Competencia '${comp.nome}' adicionada ao candidato #${candidatoId}."
    }

    @Override
    void removerCompetencia(int candidatoId, int competenciaId) {
        competenciaDAO.desvincularCandidato(candidatoId, competenciaId)
        println "Competencia #${competenciaId} removida do candidato #${candidatoId}."
    }

    @Override
    List<Vaga> verMatchesDeVagas(int candidatoId) {
        return vagaDAO.matchPorCandidato(candidatoId)
    }

    @Override
    List<Match> listarMatchesConfirmados(int candidatoId) {
        return vagaDAO.listarMatchesPorCandidato(candidatoId)
    }

    @Override
    void curtirVaga(int candidatoId, int vagaId) {
        candidatoDAO.curtirVaga(candidatoId, vagaId)
        vagaDAO.gerarMatchSeAmbosCurtiram(candidatoId, vagaId)
    }
}
