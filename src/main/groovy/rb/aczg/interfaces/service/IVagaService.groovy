package rb.aczg.interfaces.service

import rb.aczg.model.Candidato
import rb.aczg.model.Match
import rb.aczg.model.Vaga

interface IVagaService {
    Vaga publicar(Vaga vaga)
    List<Vaga> listarPorEmpresa(int empresaId)
    Vaga buscarPorId(int id)
    Vaga atualizar(Vaga vaga)
    void remover(int vagaId)
    void adicionarCompetencia(int vagaId, String nomeCompetencia)
    void curtirCandidato(int vagaId, int candidatoId)
    List<Candidato> candidatosCompativeis(int vagaId)
    List<Match> matchesDaEmpresa(int empresaId)
}
