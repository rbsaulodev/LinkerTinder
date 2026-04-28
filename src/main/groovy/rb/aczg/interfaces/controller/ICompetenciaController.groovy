package rb.aczg.interfaces.controller

import rb.aczg.model.Competencia

interface ICompetenciaController {
    Competencia cadastrar(String nome)
    List<Competencia> listarTodas()
    Competencia buscarPorId(int id)
    boolean atualizar(int id, String novoNome)
    boolean remover(int id)
}
