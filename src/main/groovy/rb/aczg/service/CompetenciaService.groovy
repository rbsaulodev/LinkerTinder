package rb.aczg.service

import rb.aczg.interfaces.dao.ICompetenciaDAO
import rb.aczg.interfaces.service.ICompetenciaService
import rb.aczg.model.Competencia

class CompetenciaService implements ICompetenciaService {

    private final ICompetenciaDAO competenciaDAO

    CompetenciaService(ICompetenciaDAO competenciaDAO) {
        this.competenciaDAO = competenciaDAO
    }

    @Override
    Competencia cadastrar(String nome) {
        if (!nome?.trim()) throw new IllegalArgumentException("Nome da competencia e obrigatorio.")
        return competenciaDAO.inserir(new Competencia(nome: nome.trim()))
    }

    @Override
    List<Competencia> listarTodas() {
        return competenciaDAO.listarTodas()
    }

    @Override
    Competencia buscarPorId(int id) {
        Competencia c = competenciaDAO.buscarPorId(id)
        if (!c) throw new RuntimeException("Competencia #${id} nao encontrada.")
        return c
    }

    @Override
    boolean atualizar(int id, String novoNome) {
        if (!novoNome?.trim()) throw new IllegalArgumentException("Nome e obrigatorio.")
        return competenciaDAO.atualizar(new Competencia(id: id, nome: novoNome.trim()))
    }

    @Override
    boolean remover(int id) {
        return competenciaDAO.deletar(id)
    }
}
