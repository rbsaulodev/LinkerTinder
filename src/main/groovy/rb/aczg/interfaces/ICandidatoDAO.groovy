package rb.aczg.interfaces

import rb.aczg.model.Candidato

interface ICandidatoDAO {
    Candidato inserir(Candidato candidato)
    List<Candidato> listarTodos()
    Candidato buscarPorId(int id)
    boolean atualizar(Candidato candidato)
    boolean deletar(int id)
    void curtirVaga(int candidatoId, int vagaId)
    List<Candidato> matchPorVaga(int vagaId)
}
