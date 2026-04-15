package rb.aczg.interfaces

import rb.aczg.model.Competencia

interface ICompetenciaDAO {
    Competencia inserir(Competencia competencia)
    List<Competencia> listarTodas()
    Competencia buscarPorId(int id)
    Competencia buscarPorNome(String nome)
    List<Competencia> buscarPorCandidato(int candidatoId)
    List<Competencia> buscarPorVaga(int vagaId)
    boolean atualizar(Competencia competencia)
    boolean deletar(int id)
    void vincularCandidato(int candidatoId, int competenciaId, String nivel)
    void vincularVaga(int vagaId, int competenciaId, boolean obrigatorio)
    void desvincularCandidato(int candidatoId, int competenciaId)
}
