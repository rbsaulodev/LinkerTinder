import rb.aczg.interfaces.dao.ICandidatoDAO
import rb.aczg.interfaces.dao.ICompetenciaDAO
import rb.aczg.interfaces.dao.IEnderecoDAO
import rb.aczg.interfaces.dao.IVagaDAO
import rb.aczg.model.Candidato
import rb.aczg.model.Competencia
import rb.aczg.model.Endereco
import rb.aczg.model.Vaga
import rb.aczg.service.CandidatoService
import rb.aczg.service.validation.CandidatoValidator
import spock.lang.Specification

import java.time.LocalDate


class CandidatoServiceSpec extends Specification {

    ICandidatoDAO candidatoDAO = Mock()
    IEnderecoDAO enderecoDAO = Mock()
    ICompetenciaDAO competenciaDAO = Mock()
    IVagaDAO vagaDAO = Mock()
    CandidatoValidator validator = new CandidatoValidator()

    CandidatoService service

    def setup() {
        service = new CandidatoService(
            candidatoDAO, enderecoDAO, competenciaDAO, vagaDAO, validator)
    }

    def "cadastrar deve lancar excecao para email invalido"() {
        given:
        Candidato c = candidatoValido()
        c.email = "nao-e-email"

        when:
        service.cadastrar(c)

        then:
        thrown(IllegalArgumentException)
        0 * candidatoDAO.inserir(_)
    }

    def "cadastrar deve lancar excecao para nome em branco"() {
        given:
        Candidato c = candidatoValido()
        c.nome = ""

        when:
        service.cadastrar(c)

        then:
        thrown(IllegalArgumentException)
        0 * candidatoDAO.inserir(_)
    }

    def "buscarPorId deve lancar excecao quando candidato nao existe"() {
        given:
        candidatoDAO.buscarPorId(99) >> null

        when:
        service.buscarPorId(99)

        then:
        thrown(RuntimeException)
    }

    def "buscarPorId deve retornar candidato quando existe"() {
        given:
        Candidato esperado = new Candidato(id: 1, nome: 'Sandubinha')
        candidatoDAO.buscarPorId(1) >> esperado

        when:
        Candidato resultado = service.buscarPorId(1)

        then:
        resultado.nome == 'Sandubinha'
    }

    def "remover deve delegar ao DAO"() {
        when:
        service.remover(5)

        then:
        1 * candidatoDAO.deletar(5)
    }

    def "curtirVaga deve curtir e tentar gerar match"() {
        when:
        service.curtirVaga(1, 2)

        then:
        1 * candidatoDAO.curtirVaga(1, 2)
        1 * vagaDAO.gerarMatchSeAmbosCurtiram(1, 2)
    }

    def "verMatchesDeVagas deve delegar ao vagaDAO"() {
        given:
        vagaDAO.matchPorCandidato(1) >> [new Vaga(titulo: 'Dev Java')]

        when:
        List<Vaga> vagas = service.verMatchesDeVagas(1)

        then:
        vagas.size() == 1
        vagas[0].titulo == 'Dev Java'
    }

    def "listarTodos deve delegar ao DAO"() {
        given:
        candidatoDAO.listarTodos() >> [new Candidato(nome: 'A'), new Candidato(nome: 'B')]

        when:
        List<Candidato> lista = service.listarTodos()

        then:
        lista.size() == 2
    }

    private Candidato candidatoValido() {
        new Candidato(
            nome:      'Sandubinha',
            sobrenome: 'Silva',
            email:     'sand@email.com',
            cpf:       '12345678900',
            dataNasc:  LocalDate.of(2000, 1, 1),
            endereco:  new Endereco(cep: '01310100', cidade: 'SP', estado: 'SP', pais: 'Brasil')
        )
    }
}
