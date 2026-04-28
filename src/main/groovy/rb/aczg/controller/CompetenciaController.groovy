package rb.aczg.controller

import rb.aczg.interfaces.controller.ICompetenciaController
import rb.aczg.interfaces.service.ICompetenciaService
import rb.aczg.model.Competencia

class CompetenciaController implements ICompetenciaController {

    private final ICompetenciaService competenciaService

    CompetenciaController(ICompetenciaService competenciaService) {
        this.competenciaService = competenciaService
    }

    @Override
    Competencia cadastrar(String nome) {
        return competenciaService.cadastrar(nome)
    }

    @Override
    List<Competencia> listarTodas() {
        return competenciaService.listarTodas()
    }

    @Override
    Competencia buscarPorId(int id) {
        return competenciaService.buscarPorId(id)
    }

    @Override
    boolean atualizar(int id, String novoNome) {
        return competenciaService.atualizar(id, novoNome)
    }

    @Override
    boolean remover(int id) {
        return competenciaService.remover(id)
    }
}
