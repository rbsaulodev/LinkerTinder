package rb.aczg.service

import rb.aczg.data.CandidatoDAO
import rb.aczg.data.CompetenciaDAO
import rb.aczg.data.EmpresaDAO
import rb.aczg.data.VagaDAO
import rb.aczg.model.Candidato
import rb.aczg.model.Competencia
import rb.aczg.model.Empresa
import rb.aczg.model.Vaga


class EmpresaService {

    private final EmpresaDAO empresaDAO = new EmpresaDAO()
    private final VagaDAO vagaDAO = new VagaDAO()
    private final CompetenciaDAO competenciaDAO = new CompetenciaDAO()

    Empresa cadastrar(Empresa empresa) {
        validar(empresa)
        return empresaDAO.inserir(empresa)
    }

    List<Empresa> listarTodas() {
        return empresaDAO.listarTodas()
    }

    Empresa buscarPorId(int id) {
        Empresa e = empresaDAO.buscarPorId(id)
        if (!e) throw new RuntimeException("Empresa #${id} não encontrada.")
        return e
    }

    Empresa atualizar(Empresa empresa) {
        validar(empresa)
        empresaDAO.atualizar(empresa)
        return empresa
    }

    void remover(int id) {
        empresaDAO.deletar(id)
    }

    //CRUD de Vagas
    Vaga publicarVaga(Vaga vaga) {
        if (!vaga.titulo?.trim()) throw new IllegalArgumentException("Título da vaga é obrigatório.")
        return vagaDAO.inserir(vaga)
    }

    List<Vaga> listarVagas(int empresaId) {
        return vagaDAO.listarPorEmpresa(empresaId)
    }

    Vaga atualizarVaga(Vaga vaga) {
        vagaDAO.atualizar(vaga)
        return vaga
    }

    void removerVaga(int vagaId) {
        vagaDAO.deletar(vagaId)
    }

    void adicionarCompetenciaVaga(int vagaId, String nomeCompetencia) {
        Competencia comp = new Competencia(nome: nomeCompetencia)
        comp = competenciaDAO.inserir(comp)
        competenciaDAO.vincularVaga(vagaId, comp.id)
        println "Competência '${comp.nome}' adicionada à vaga #${vagaId}."
    }

    List<Candidato> verMatchesDeCandidatos(int vagaId) {
        return new CandidatoDAO().matchPorVaga(vagaId)
    }

    private void validar(Empresa e) {
        if (!e.nome?.trim())   throw new IllegalArgumentException("Nome é obrigatório.")
        if (!e.email?.trim())  throw new IllegalArgumentException("Email é obrigatório.")
        if (!e.cnpj?.trim())   throw new IllegalArgumentException("CNPJ é obrigatório.")
        if (!e.pais?.trim())   throw new IllegalArgumentException("País é obrigatório.")
        if (!e.estado?.trim()) throw new IllegalArgumentException("Estado é obrigatório.")
        if (!e.cep?.trim())    throw new IllegalArgumentException("CEP é obrigatório.")
    }
}