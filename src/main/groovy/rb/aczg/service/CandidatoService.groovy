package rb.aczg.service

import rb.aczg.data.CandidatoDAO
import rb.aczg.data.CompetenciaDAO
import rb.aczg.data.VagaDAO
import rb.aczg.model.Candidato
import rb.aczg.model.Competencia
import rb.aczg.model.Vaga

class CandidatoService {

    private final CandidatoDAO candidatoDAO  = new CandidatoDAO()
    private final CompetenciaDAO competenciaDAO = new CompetenciaDAO()

    Candidato cadastrar(Candidato candidato) {
        validar(candidato)
        return candidatoDAO.inserir(candidato)
    }

    List<Candidato> listarTodos() {
        return candidatoDAO.listarTodos()
    }

    Candidato buscarPorId(int id) {
        Candidato c = candidatoDAO.buscarPorId(id)
        if (!c) throw new RuntimeException("Candidato #${id} não encontrado.")
        return c
    }

    Candidato atualizar(Candidato candidato) {
        validar(candidato)
        candidatoDAO.atualizar(candidato)
        return candidato
    }

    void remover(int id) {
        candidatoDAO.deletar(id)
    }

    void adicionarCompetencia(int candidatoId, String nomeCompetencia) {
        Competencia comp = new Competencia(nome: nomeCompetencia)
        comp = competenciaDAO.inserir(comp)
        competenciaDAO.vincularCandidato(candidatoId, comp.id)
        println "Competência '${comp.nome}' adicionada ao candidato #${candidatoId}."
    }

    void removerCompetencia(int candidatoId, int competenciaId) {
        competenciaDAO.desvincularCandidato(candidatoId, competenciaId)
        println "Competência #${competenciaId} removida do candidato #${candidatoId}."
    }

    List<Vaga> verMatchesDeVagas(int candidatoId) {
        return new VagaDAO().matchPorCandidato(candidatoId)
    }

    private void validar(Candidato c) {
        if (!c.nome?.trim())      throw new IllegalArgumentException("Nome é obrigatório.")
        if (!c.sobrenome?.trim()) throw new IllegalArgumentException("Sobrenome é obrigatório.")
        if (!c.email?.trim())     throw new IllegalArgumentException("Email é obrigatório.")
        if (!c.cpf?.trim())       throw new IllegalArgumentException("CPF é obrigatório.")
        if (c.idade <= 0)         throw new IllegalArgumentException("Idade inválida.")
        if (!c.estado?.trim())    throw new IllegalArgumentException("Estado é obrigatório.")
        if (!c.cep?.trim())       throw new IllegalArgumentException("CEP é obrigatório.")
    }
}