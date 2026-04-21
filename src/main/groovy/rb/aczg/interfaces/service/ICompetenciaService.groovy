package rb.aczg.interfaces.service

import rb.aczg.model.Competencia

interface ICompetenciaService {
    Competencia cadastrar(String nome)
    List<Competencia> listarTodas()
    Competencia buscarPorId(int id)
    boolean atualizar(int id, String novoNome)
    boolean remover(int id)
}
