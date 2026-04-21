package rb.aczg.service

import rb.aczg.interfaces.dao.ICandidatoDAO
import rb.aczg.interfaces.dao.ICompetenciaDAO
import rb.aczg.interfaces.dao.IEmpresaDAO
import rb.aczg.interfaces.dao.IEnderecoDAO
import rb.aczg.interfaces.dao.IVagaDAO
import rb.aczg.interfaces.service.IEmpresaService
import rb.aczg.model.Candidato
import rb.aczg.model.Competencia
import rb.aczg.model.Empresa
import rb.aczg.model.Match
import rb.aczg.model.Vaga
import rb.aczg.service.validation.EmpresaValidator

class EmpresaService implements IEmpresaService {

    private final IEmpresaDAO empresaDAO
    private final IEnderecoDAO enderecoDAO
    private final IVagaDAO vagaDAO
    private final ICompetenciaDAO competenciaDAO
    private final ICandidatoDAO candidatoDAO
    private final EmpresaValidator validator

    EmpresaService(
            IEmpresaDAO empresaDAO,
            IEnderecoDAO enderecoDAO,
            IVagaDAO vagaDAO,
            ICompetenciaDAO competenciaDAO,
            ICandidatoDAO candidatoDAO,
            EmpresaValidator validator) {
        this.empresaDAO     = empresaDAO
        this.enderecoDAO    = enderecoDAO
        this.vagaDAO        = vagaDAO
        this.competenciaDAO = competenciaDAO
        this.candidatoDAO   = candidatoDAO
        this.validator      = validator
    }

    @Override
    Empresa cadastrar(Empresa empresa) {
        validator.validar(empresa)
        if (empresa.endereco) {
            empresa.endereco = enderecoDAO.inserir(empresa.endereco)
        }
        return empresaDAO.inserir(empresa)
    }

    @Override
    List<Empresa> listarTodas() {
        return empresaDAO.listarTodas()
    }

    @Override
    Empresa buscarPorId(int id) {
        Empresa e = empresaDAO.buscarPorId(id)
        if (!e) throw new RuntimeException("Empresa #${id} nao encontrada.")
        return e
    }

    @Override
    Empresa atualizar(Empresa empresa) {
        validator.validar(empresa)
        if (empresa.endereco?.id) {
            enderecoDAO.atualizar(empresa.endereco)
        }
        empresaDAO.atualizar(empresa)
        return empresa
    }

    @Override
    void remover(int id) {
        empresaDAO.deletar(id)
    }

    @Override
    Vaga publicarVaga(Vaga vaga) {
        if (!vaga.titulo?.trim()) throw new IllegalArgumentException("Titulo da vaga e obrigatorio.")
        if (vaga.endereco && !vaga.endereco.id) {
            vaga.endereco = enderecoDAO.inserir(vaga.endereco)
        }
        return vagaDAO.inserir(vaga)
    }

    @Override
    List<Vaga> listarVagas(int empresaId) {
        return vagaDAO.listarPorEmpresa(empresaId)
    }

    @Override
    Vaga atualizarVaga(Vaga vaga) {
        vagaDAO.atualizar(vaga)
        return vaga
    }

    @Override
    void removerVaga(int vagaId) {
        vagaDAO.deletar(vagaId)
    }

    @Override
    void adicionarCompetenciaVaga(int vagaId, String nomeCompetencia) {
        Competencia comp = competenciaDAO.inserir(new Competencia(nome: nomeCompetencia))
        competenciaDAO.vincularVaga(vagaId, comp.id)
        println "Competencia '${comp.nome}' adicionada a vaga #${vagaId}."
    }

    @Override
    void removerCompetenciaVaga(int vagaId, int competenciaId) {
        competenciaDAO.desvincularVaga(vagaId, competenciaId)
        println "Competencia #${competenciaId} removida da vaga #${vagaId}."
    }

    @Override
    void curtirCandidato(int vagaId, int candidatoId) {
        vagaDAO.curtirCandidato(vagaId, candidatoId)
        vagaDAO.gerarMatchSeAmbosCurtiram(candidatoId, vagaId)
    }

    @Override
    List<Candidato> verMatchesDeCandidatos(int vagaId) {
        return candidatoDAO.matchPorVaga(vagaId)
    }

    @Override
    List<Match> verMatchesDaEmpresa(int empresaId) {
        List<Vaga> vagas = vagaDAO.listarPorEmpresa(empresaId)
        List<Match> matches = []
        vagas.each { vaga -> matches.addAll(vagaDAO.listarMatchesPorVaga(vaga.id)) }
        return matches
    }
}
