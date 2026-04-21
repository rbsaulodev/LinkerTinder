package rb.aczg.interfaces.service

import rb.aczg.model.Candidato
import rb.aczg.model.Match
import rb.aczg.model.Vaga

interface ICandidatoService {
    Candidato cadastrar(Candidato candidato)
    List<Candidato> listarTodos()
    Candidato buscarPorId(int id)
    Candidato atualizar(Candidato candidato)
    void remover(int id)
    void adicionarCompetencia(int candidatoId, String nomeCompetencia, String nivel)
    void removerCompetencia(int candidatoId, int competenciaId)
    List<Vaga> verMatchesDeVagas(int candidatoId)
    List<Match> listarMatchesConfirmados(int candidatoId)
    void curtirVaga(int candidatoId, int vagaId)
}

