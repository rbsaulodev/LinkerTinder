package rb.aczg.interfaces.controller

import rb.aczg.model.Candidato
import rb.aczg.model.Vaga

interface ICandidatoController {
    Candidato cadastrar(Candidato candidato, String competenciasRaw)
    List<Candidato> listarTodos()
    Candidato buscarPorId(int id)
    Candidato atualizar(Candidato candidato)
    void remover(int id)
    void adicionarCompetencia(int candidatoId, String nomeCompetencia, String nivel)
    void removerCompetencia(int candidatoId, int competenciaId)
    List<Vaga> verMatchesDeVagas(int candidatoId)
    void curtirVaga(int candidatoId, int vagaId)
}
