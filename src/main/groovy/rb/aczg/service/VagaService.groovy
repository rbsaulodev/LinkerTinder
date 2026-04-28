package rb.aczg.service

import rb.aczg.interfaces.dao.ICandidatoDAO
import rb.aczg.interfaces.dao.ICompetenciaDAO
import rb.aczg.interfaces.dao.IEnderecoDAO
import rb.aczg.interfaces.dao.IVagaDAO
import rb.aczg.interfaces.observer.MatchObserver
import rb.aczg.interfaces.service.IVagaService
import rb.aczg.model.Candidato
import rb.aczg.model.Competencia
import rb.aczg.model.Match
import rb.aczg.model.Vaga

class VagaService implements IVagaService {

    private static final String TITULO_OBRIGATORIO = 'Titulo da vaga e obrigatorio.'
    private static final String VAGA_NAO_ENCONTRADA = 'Vaga #%d nao encontrada.'

    private final IVagaDAO vagaDAO
    private final IEnderecoDAO enderecoDAO
    private final ICompetenciaDAO competenciaDAO
    private final ICandidatoDAO candidatoDAO
    private final List<MatchObserver> observers

    VagaService(
            IVagaDAO vagaDAO,
            IEnderecoDAO enderecoDAO,
            ICompetenciaDAO competenciaDAO,
            ICandidatoDAO candidatoDAO,
            List<MatchObserver> observers = []) {
        this.vagaDAO = vagaDAO
        this.enderecoDAO = enderecoDAO
        this.competenciaDAO = competenciaDAO
        this.candidatoDAO  = candidatoDAO
        this.observers = new ArrayList<>(observers)
    }

    void registrarObserver(MatchObserver observer) {
        observers << observer
    }

    @Override
    Vaga publicar(Vaga vaga) {
        validarTitulo(vaga.titulo)
        salvarEnderecoSeNovo(vaga)
        return vagaDAO.inserir(vaga)
    }

    @Override
    List<Vaga> listarPorEmpresa(int empresaId) {
        return vagaDAO.listarPorEmpresa(empresaId)
    }

    @Override
    Vaga buscarPorId(int id) {
        Vaga vaga = vagaDAO.buscarPorId(id)
        if (!vaga) throw new RuntimeException(String.format(VAGA_NAO_ENCONTRADA, id))
        return vaga
    }

    @Override
    Vaga atualizar(Vaga vaga) {
        vagaDAO.atualizar(vaga)
        return vaga
    }

    @Override
    void remover(int vagaId) {
        vagaDAO.deletar(vagaId)
    }

    @Override
    void adicionarCompetencia(int vagaId, String nomeCompetencia) {
        Competencia comp = competenciaDAO.inserir(new Competencia(nome: nomeCompetencia))
        competenciaDAO.vincularVaga(vagaId, comp.id)
        println "Competencia '${comp.nome}' adicionada a vaga #${vagaId}."
    }

    void removerCompetencia(int vagaId, int competenciaId) {
        competenciaDAO.desvincularVaga(vagaId, competenciaId)
        println "Competencia #${competenciaId} removida da vaga #${vagaId}."
    }

    @Override
    void curtirCandidato(int vagaId, int candidatoId) {
        vagaDAO.curtirCandidato(vagaId, candidatoId)
        Match match = vagaDAO.gerarMatchSeAmbosCurtiram(candidatoId, vagaId)
        if (match) notificarObservers(match)
    }

    @Override
    List<Candidato> candidatosCompativeis(int vagaId) {
        return candidatoDAO.matchPorVaga(vagaId)
    }

    @Override
    List<Match> matchesDaEmpresa(int empresaId) {
        return vagaDAO.listarPorEmpresa(empresaId)
                      .collectMany { vaga -> vagaDAO.listarMatchesPorVaga(vaga.id) }
    }

    private void validarTitulo(String titulo) {
        if (!titulo?.trim()) throw new IllegalArgumentException(TITULO_OBRIGATORIO)
    }

    private void salvarEnderecoSeNovo(Vaga vaga) {
        if (vaga.endereco && !vaga.endereco.id) {
            vaga.endereco = enderecoDAO.inserir(vaga.endereco)
        }
    }

    private void notificarObservers(Match match) {
        observers.each { it.onMatch(match) }
    }
}
