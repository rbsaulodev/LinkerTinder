package rb.aczg.controller

import rb.aczg.interfaces.controller.IVagaController
import rb.aczg.interfaces.service.IVagaService
import rb.aczg.model.Candidato
import rb.aczg.model.Match
import rb.aczg.model.Vaga

class VagaController implements IVagaController {

    private final IVagaService vagaService

    VagaController(IVagaService vagaService) {
        this.vagaService = vagaService
    }

    @Override
    Vaga publicar(Vaga vaga, String competenciasRaw) {
        Vaga inserida = vagaService.publicar(vaga)
        if (competenciasRaw) {
            competenciasRaw.split(',').each { nome ->
                if (nome.trim()) vagaService.adicionarCompetencia(inserida.id, nome.trim())
            }
        }
        return inserida
    }

    @Override
    List<Vaga> listarPorEmpresa(int empresaId) {
        return vagaService.listarPorEmpresa(empresaId)
    }

    @Override
    Vaga atualizar(Vaga vaga) {
        return vagaService.atualizar(vaga)
    }

    @Override
    void remover(int vagaId) {
        vagaService.remover(vagaId)
    }

    @Override
    void adicionarCompetencia(int vagaId, String nomeCompetencia) {
        vagaService.adicionarCompetencia(vagaId, nomeCompetencia)
    }

    @Override
    List<Candidato> candidatosCompativeis(int vagaId) {
        return vagaService.candidatosCompativeis(vagaId)
    }

    @Override
    void curtirCandidato(int vagaId, int candidatoId) {
        vagaService.curtirCandidato(vagaId, candidatoId)
    }

    @Override
    List<Match> matchesDaEmpresa(int empresaId) {
        return vagaService.matchesDaEmpresa(empresaId)
    }
}
