package rb.aczg.interfaces.service

import rb.aczg.model.Candidato
import rb.aczg.model.Empresa
import rb.aczg.model.Match
import rb.aczg.model.Vaga

interface IEmpresaService {
    Empresa cadastrar(Empresa empresa)
    List<Empresa> listarTodas()
    Empresa buscarPorId(int id)
    Empresa atualizar(Empresa empresa)
    void remover(int id)
    Vaga publicarVaga(Vaga vaga)
    List<Vaga> listarVagas(int empresaId)
    Vaga atualizarVaga(Vaga vaga)
    void removerVaga(int vagaId)
    void adicionarCompetenciaVaga(int vagaId, String nomeCompetencia)
    void removerCompetenciaVaga(int vagaId, int competenciaId)
    void curtirCandidato(int vagaId, int candidatoId)
    List<Candidato> verMatchesDeCandidatos(int vagaId)
    List<Match> verMatchesDaEmpresa(int empresaId)
}