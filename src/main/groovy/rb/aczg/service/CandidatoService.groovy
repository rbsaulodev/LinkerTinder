package rb.aczg.service

import rb.aczg.interfaces.dao.ICandidatoDAO
import rb.aczg.interfaces.dao.ICompetenciaDAO
import rb.aczg.interfaces.dao.IEnderecoDAO
import rb.aczg.interfaces.dao.IVagaDAO
import rb.aczg.interfaces.observer.MatchObserver
import rb.aczg.interfaces.service.ICandidatoService
import rb.aczg.model.Candidato
import rb.aczg.model.Competencia
import rb.aczg.model.Match
import rb.aczg.model.Vaga
import rb.aczg.service.validation.CandidatoValidator

class CandidatoService implements ICandidatoService {

    private static final String CANDIDATO_NAO_ENCONTRADO = 'Candidato #%d nao encontrado.'

    private final ICandidatoDAO candidatoDAO
    private final IEnderecoDAO enderecoDAO
    private final ICompetenciaDAO competenciaDAO
    private final IVagaDAO vagaDAO
    private final CandidatoValidator validator
    private final List<MatchObserver> observers

    CandidatoService(
            ICandidatoDAO candidatoDAO,
            IEnderecoDAO enderecoDAO,
            ICompetenciaDAO competenciaDAO,
            IVagaDAO vagaDAO,
            CandidatoValidator validator,
            List<MatchObserver> observers = []) {
        this.candidatoDAO = candidatoDAO
        this.enderecoDAO = enderecoDAO
        this.competenciaDAO = competenciaDAO
        this.vagaDAO = vagaDAO
        this.validator = validator
        this.observers = new ArrayList<>(observers)
    }

    void registrarObserver(MatchObserver observer) {
        observers << observer
    }

    @Override
    Candidato cadastrar(Candidato candidato) {
        validator.validar(candidato)
        salvarEnderecoSePresente(candidato)
        return candidatoDAO.inserir(candidato)
    }

    @Override
    List<Candidato> listarTodos() {
        return candidatoDAO.listarTodos()
    }

    @Override
    Candidato buscarPorId(int id) {
        Candidato candidato = candidatoDAO.buscarPorId(id)
        if (!candidato) throw new RuntimeException(String.format(CANDIDATO_NAO_ENCONTRADO, id))
        return candidato
    }

    @Override
    Candidato atualizar(Candidato candidato) {
        validator.validar(candidato)
        atualizarEnderecoSeExistente(candidato)
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
        Match match = vagaDAO.gerarMatchSeAmbosCurtiram(candidatoId, vagaId)
        if (match) notificarObservers(match)
    }

    private void salvarEnderecoSePresente(Candidato candidato) {
        if (candidato.endereco) {
            candidato.endereco = enderecoDAO.inserir(candidato.endereco)
        }
    }

    private void atualizarEnderecoSeExistente(Candidato candidato) {
        if (candidato.endereco?.id) {
            enderecoDAO.atualizar(candidato.endereco)
        }
    }

    private void notificarObservers(Match match) {
        observers.each { it.onMatch(match) }
    }
}
