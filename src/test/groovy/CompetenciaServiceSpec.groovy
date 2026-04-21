import rb.aczg.interfaces.dao.ICompetenciaDAO
import rb.aczg.model.Competencia
import rb.aczg.service.CompetenciaService
import spock.lang.Specification

class CompetenciaServiceSpec extends Specification {

    ICompetenciaDAO competenciaDAO = Mock()
    CompetenciaService service

    def setup() {
        service = new CompetenciaService(competenciaDAO)
    }

    def "cadastrar deve rejeitar nome em branco"() {
        when:
        service.cadastrar('   ')

        then:
        thrown(IllegalArgumentException)
        0 * competenciaDAO.inserir(_)
    }

    def "buscarPorId deve lancar excecao quando nao encontrado"() {
        given:
        competenciaDAO.buscarPorId(99) >> null

        when:
        service.buscarPorId(99)

        then:
        thrown(RuntimeException)
    }

    def "atualizar deve rejeitar nome vazio"() {
        when:
        service.atualizar(1, '')

        then:
        thrown(IllegalArgumentException)
    }

    def "remover deve delegar ao DAO e retornar resultado"() {
        given:
        competenciaDAO.deletar(3) >> true

        when:
        boolean resultado = service.remover(3)

        then:
        resultado == true
    }

    def "listarTodas deve retornar lista do DAO"() {
        given:
        competenciaDAO.listarTodas() >> [
            new Competencia(id: 1, nome: 'Java'),
            new Competencia(id: 2, nome: 'Python')
        ]

        when:
        List<Competencia> lista = service.listarTodas()

        then:
        lista.size() == 2
        lista*.nome == ['Java', 'Python']
    }
}
