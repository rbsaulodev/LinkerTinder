import rb.aczg.interfaces.dao.ICandidatoDAO
import rb.aczg.interfaces.dao.ICompetenciaDAO
import rb.aczg.interfaces.dao.IEnderecoDAO
import rb.aczg.interfaces.dao.IVagaDAO
import rb.aczg.interfaces.observer.MatchObserver
import rb.aczg.model.*
import rb.aczg.service.VagaService
import spock.lang.Specification

import java.time.LocalDateTime

class VagaServiceSpec extends Specification {

    IVagaDAO vagaDAO = Mock()
    IEnderecoDAO enderecoDAO = Mock()
    ICompetenciaDAO competenciaDAO = Mock()
    ICandidatoDAO candidatoDAO = Mock()
    MatchObserver observer = Mock()

    VagaService service = new VagaService(vagaDAO, enderecoDAO, competenciaDAO, candidatoDAO, [observer])

    private Vaga vagaBase() {
        new Vaga(empresaId: 1, titulo: 'Dev Groovy', status: 'Aberta')
    }

    def "publicar deve ignorar endereco que ja tem id"() {
        given:
        def vaga = vagaBase()
        vaga.endereco = new Endereco(id: 99, cidade: 'RJ')
        vagaDAO.inserir(vaga) >> new Vaga(id: 12)

        when:
        service.publicar(vaga)

        then:
        0 * enderecoDAO.inserir(_)
    }

    def "publicar deve lancar excecao quando titulo e nulo"() {
        given:
        def vaga = vagaBase()
        vaga.titulo = null

        when:
        service.publicar(vaga)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == 'Titulo da vaga e obrigatorio.'
        0 * vagaDAO.inserir(_)
    }

    def "publicar deve lancar excecao quando titulo e vazio"() {
        given:
        def vaga = vagaBase()
        vaga.titulo = '   '

        when:
        service.publicar(vaga)

        then:
        thrown(IllegalArgumentException)
        0 * vagaDAO.inserir(_)
    }

    def "buscarPorId deve retornar vaga quando encontrada"() {
        given:
        def vaga = new Vaga(id: 5, titulo: 'QA Engineer')
        vagaDAO.buscarPorId(5) >> vaga

        when:
        def resultado = service.buscarPorId(5)

        then:
        resultado.id     == 5
        resultado.titulo == 'QA Engineer'
    }

    def "buscarPorId deve lancar excecao quando vaga nao existe"() {
        given:
        vagaDAO.buscarPorId(99) >> null

        when:
        service.buscarPorId(99)

        then:
        def ex = thrown(RuntimeException)
        ex.message == 'Vaga #99 nao encontrada.'
    }

    def "listarPorEmpresa deve delegar ao DAO"() {
        given:
        def vagas = [vagaBase(), vagaBase()]
        vagaDAO.listarPorEmpresa(1) >> vagas

        when:
        def resultado = service.listarPorEmpresa(1)

        then:
        resultado.size() == 2
    }

    def "atualizar deve chamar DAO e retornar a vaga"() {
        given:
        def vaga = vagaBase()
        vaga.id = 6

        when:
        def resultado = service.atualizar(vaga)

        then:
        1 * vagaDAO.atualizar(vaga)
        resultado == vaga
    }

    def "remover deve delegar ao DAO com id correto"() {
        when:
        service.remover(7)

        then:
        1 * vagaDAO.deletar(7)
    }

    def "removerCompetencia deve desvincular"() {
        when:
        service.removerCompetencia(5, 20)

        then:
        1 * competenciaDAO.desvincularVaga(5, 20)
    }

    def "curtirCandidato deve registrar curtida e tentar gerar match"() {
        given:
        vagaDAO.gerarMatchSeAmbosCurtiram(8, 3) >> null

        when:
        service.curtirCandidato(3, 8)

        then:
        1 * vagaDAO.curtirCandidato(3, 8)
        1 * vagaDAO.gerarMatchSeAmbosCurtiram(8, 3)
    }

    def "curtirCandidato deve notificar observers quando match e gerado"() {
        given:
        def match = new Match(id: 1, candidatoId: 8, vagaId: 3, matchedEm: LocalDateTime.now())
        vagaDAO.gerarMatchSeAmbosCurtiram(8, 3) >> match

        when:
        service.curtirCandidato(3, 8)

        then:
        1 * observer.onMatch(match)
    }

    def "curtirCandidato NAO deve notificar observers quando match nao ocorre"() {
        given:
        vagaDAO.gerarMatchSeAmbosCurtiram(8, 3) >> null

        when:
        service.curtirCandidato(3, 8)

        then:
        0 * observer.onMatch(_)
    }

    def "deve notificar todos os observers registrados dinamicamente"() {
        given:
        MatchObserver obs2 = Mock()
        service.registrarObserver(obs2)
        def match = new Match(id: 2, candidatoId: 1, vagaId: 2, matchedEm: LocalDateTime.now())
        vagaDAO.gerarMatchSeAmbosCurtiram(1, 4) >> match

        when:
        service.curtirCandidato(4, 1)

        then:
        1 * observer.onMatch(match)
        1 * obs2.onMatch(match)
    }

    // ─── candidatosCompativeis ────────────────────────────────────────────────

    def "candidatosCompativeis deve delegar ao candidatoDAO"() {
        given:
        def candidatos = [new Candidato(id: 1, nome: 'Bia')]
        candidatoDAO.matchPorVaga(3) >> candidatos

        when:
        def resultado = service.candidatosCompativeis(3)

        then:
        resultado == candidatos
    }

    def "matchesDaEmpresa deve agregar matches de todas as vagas"() {
        given:
        vagaDAO.listarPorEmpresa(1) >> [new Vaga(id: 10), new Vaga(id: 11)]
        def m1 = new Match(id: 1, vagaId: 10, matchedEm: LocalDateTime.now())
        def m2 = new Match(id: 2, vagaId: 11, matchedEm: LocalDateTime.now())
        vagaDAO.listarMatchesPorVaga(10) >> [m1]
        vagaDAO.listarMatchesPorVaga(11) >> [m2]

        when:
        def resultado = service.matchesDaEmpresa(1)

        then:
        resultado.size() == 2
        resultado.containsAll([m1, m2])
    }

    def "matchesDaEmpresa deve retornar lista vazia quando empresa nao tem vagas"() {
        given:
        vagaDAO.listarPorEmpresa(99) >> []

        when:
        def resultado = service.matchesDaEmpresa(99)

        then:
        resultado.isEmpty()
    }
}
