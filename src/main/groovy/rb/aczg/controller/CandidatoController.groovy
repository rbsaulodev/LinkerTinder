package rb.aczg.controller

import rb.aczg.interfaces.controller.ICandidatoController
import rb.aczg.interfaces.service.ICandidatoService
import rb.aczg.model.Candidato
import rb.aczg.model.Vaga

class CandidatoController implements ICandidatoController {

    private final ICandidatoService candidatoService

    CandidatoController(ICandidatoService candidatoService) {
        this.candidatoService = candidatoService
    }

    @Override
    Candidato cadastrar(Candidato candidato, String competenciasRaw) {
        Candidato inserido = candidatoService.cadastrar(candidato)
        if (competenciasRaw) {
            competenciasRaw.split(',').each { nome ->
                if (nome.trim()) candidatoService.adicionarCompetencia(inserido.id, nome.trim(), null)
            }
        }
        return inserido
    }

    @Override
    List<Candidato> listarTodos() {
        return candidatoService.listarTodos()
    }

    @Override
    Candidato buscarPorId(int id) {
        return candidatoService.buscarPorId(id)
    }

    @Override
    Candidato atualizar(Candidato candidato) {
        return candidatoService.atualizar(candidato)
    }

    @Override
    void remover(int id) {
        candidatoService.remover(id)
    }

    @Override
    void adicionarCompetencia(int candidatoId, String nomeCompetencia, String nivel) {
        candidatoService.adicionarCompetencia(candidatoId, nomeCompetencia, nivel ?: null)
    }

    @Override
    void removerCompetencia(int candidatoId, int competenciaId) {
        candidatoService.removerCompetencia(candidatoId, competenciaId)
    }

    @Override
    List<Vaga> verMatchesDeVagas(int candidatoId) {
        return candidatoService.verMatchesDeVagas(candidatoId)
    }

    @Override
    void curtirVaga(int candidatoId, int vagaId) {
        candidatoService.curtirVaga(candidatoId, vagaId)
    }
}
