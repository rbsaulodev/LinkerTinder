package rb.aczg.service

import rb.aczg.interfaces.dao.ICompetenciaDAO
import rb.aczg.interfaces.service.ICompetenciaService
import rb.aczg.model.Competencia

class CompetenciaService implements ICompetenciaService {

    private static final String NOME_OBRIGATORIO = 'Nome da competencia e obrigatorio.'
    private static final String NOME_ATUALIZACAO_OBRIG  = 'Nome e obrigatorio.'
    private static final String COMPETENCIA_NAO_ENCONTRADA = 'Competencia #%d nao encontrada.'

    private final ICompetenciaDAO competenciaDAO

    CompetenciaService(ICompetenciaDAO competenciaDAO) {
        this.competenciaDAO = competenciaDAO
    }

    @Override
    Competencia cadastrar(String nome) {
        validarNome(nome, NOME_OBRIGATORIO)
        return competenciaDAO.inserir(new Competencia(nome: nome.trim()))
    }

    @Override
    List<Competencia> listarTodas() {
        return competenciaDAO.listarTodas()
    }

    @Override
    Competencia buscarPorId(int id) {
        Competencia competencia = competenciaDAO.buscarPorId(id)
        if (!competencia) throw new RuntimeException(String.format(COMPETENCIA_NAO_ENCONTRADA, id))
        return competencia
    }

    @Override
    boolean atualizar(int id, String novoNome) {
        validarNome(novoNome, NOME_ATUALIZACAO_OBRIG)
        return competenciaDAO.atualizar(new Competencia(id: id, nome: novoNome.trim()))
    }

    @Override
    boolean remover(int id) {
        return competenciaDAO.deletar(id)
    }

    private static void validarNome(String nome, String mensagemErro) {
        if (!nome?.trim()) throw new IllegalArgumentException(mensagemErro)
    }
}
