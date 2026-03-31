package rb.aczg.service

import rb.aczg.data.CompetenciaDAO
import rb.aczg.model.Competencia

class CompetenciaService {

    private final CompetenciaDAO competenciaDAO = new CompetenciaDAO()

    Competencia cadastrar(String nome) {
        if (!nome?.trim()) throw new IllegalArgumentException("Nome da competência é obrigatório.")
        return competenciaDAO.inserir(new Competencia(nome: nome.trim()))
    }

    List<Competencia> listarTodas() {
        return competenciaDAO.listarTodas()
    }

    Competencia buscarPorId(int id) {
        Competencia c = competenciaDAO.buscarPorId(id)
        if (!c) throw new RuntimeException("Competência #${id} não encontrada.")
        return c
    }

    boolean atualizar(int id, String novoNome) {
        if (!novoNome?.trim()) throw new IllegalArgumentException("Nome é obrigatório.")
        return competenciaDAO.atualizar(new Competencia(id: id, nome: novoNome.trim()))
    }

    boolean remover(int id) {
        return competenciaDAO.deletar(id)
    }
}