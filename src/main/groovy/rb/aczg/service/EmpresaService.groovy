package rb.aczg.service

import rb.aczg.dao.*
import rb.aczg.model.*

class EmpresaService {

    private final EmpresaDAO empresaDAO = new EmpresaDAO()
    private final EnderecoDAO enderecoDAO = new EnderecoDAO()
    private final VagaDAO vagaDAO = new VagaDAO()
    private final CompetenciaDAO competenciaDAO = new CompetenciaDAO()

    Empresa cadastrar(Empresa empresa) {
        validar(empresa)
        if (empresa.endereco) {
            empresa.endereco = enderecoDAO.inserir(empresa.endereco)
        }
        return empresaDAO.inserir(empresa)
    }

    List<Empresa> listarTodas() {
        return empresaDAO.listarTodas()
    }

    Empresa buscarPorId(int id) {
        Empresa e = empresaDAO.buscarPorId(id)
        if (!e) throw new RuntimeException("Empresa #${id} nao encontrada.")
        return e
    }

    Empresa atualizar(Empresa empresa) {
        validar(empresa)
        if (empresa.endereco?.id) {
            enderecoDAO.atualizar(empresa.endereco)
        }
        empresaDAO.atualizar(empresa)
        return empresa
    }

    void remover(int id) {
        empresaDAO.deletar(id)
    }

    Vaga publicarVaga(Vaga vaga) {
        if (!vaga.titulo?.trim()) throw new IllegalArgumentException("Titulo da vaga e obrigatorio.")
        if (vaga.endereco && !vaga.endereco.id) {
            vaga.endereco = enderecoDAO.inserir(vaga.endereco)
        }
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
        Competencia comp = competenciaDAO.inserir(new Competencia(nome: nomeCompetencia))
        competenciaDAO.vincularVaga(vagaId, comp.id)
        println "Competencia '${comp.nome}' adicionada a vaga #${vagaId}."
    }

    void curtirCandidato(int vagaId, int candidatoId) {
        vagaDAO.curtirCandidato(vagaId, candidatoId)
        vagaDAO.gerarMatchSeAmbosCurtiram(candidatoId, vagaId)
    }

    List<Candidato> verMatchesDeCandidatos(int vagaId) {
        return new CandidatoDAO().matchPorVaga(vagaId)
    }

    List<Match> verMatchesDaEmpresa(int empresaId) {
        List<Vaga> vagas = vagaDAO.listarPorEmpresa(empresaId)
        List<Match> matches = []
        vagas.each { vaga ->
            matches.addAll(vagaDAO.listarMatchesPorVaga(vaga.id))
        }
        return matches
    }

    private void validar(Empresa e) {
        if (!e.nome?.trim())  throw new IllegalArgumentException("Nome e obrigatorio.")
        if (!e.email?.trim()) throw new IllegalArgumentException("Email e obrigatorio.")
        if (!e.cnpj?.trim())  throw new IllegalArgumentException("CNPJ e obrigatorio.")
    }
}
