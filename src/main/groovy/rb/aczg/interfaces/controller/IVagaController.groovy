package rb.aczg.interfaces.controller

import rb.aczg.model.Candidato
import rb.aczg.model.Match
import rb.aczg.model.Vaga

interface IVagaController {
    Vaga publicar(Vaga vaga, String competenciasRaw)
    List<Vaga> listarPorEmpresa(int empresaId)
    Vaga atualizar(Vaga vaga)
    void remover(int vagaId)
    void adicionarCompetencia(int vagaId, String nomeCompetencia)
    List<Candidato> candidatosCompativeis(int vagaId)
    void curtirCandidato(int vagaId, int candidatoId)
    List<Match> matchesDaEmpresa(int empresaId)
}
