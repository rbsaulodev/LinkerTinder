package rb.aczg.interfaces

import rb.aczg.model.Match
import rb.aczg.model.Vaga

interface IVagaDAO {
    Vaga inserir(Vaga vaga)
    List<Vaga> listarTodas()
    List<Vaga> listarPorEmpresa(int empresaId)
    Vaga buscarPorId(int id)
    boolean atualizar(Vaga vaga)
    boolean deletar(int id)
    void curtirCandidato(int vagaId, int candidatoId)
    List<Vaga> matchPorCandidato(int candidatoId)
    Match gerarMatchSeAmbosCurtiram(int candidatoId, int vagaId)
    List<Match> listarMatchesPorCandidato(int candidatoId)
    List<Match> listarMatchesPorVaga(int vagaId)
}
