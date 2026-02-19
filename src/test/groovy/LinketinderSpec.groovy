import org.example.main.Linketinder
import spock.lang.Specification

class LinketinderSpec extends Specification {

    def setup() {
        Linketinder.candidatos = []
        Linketinder.empresas = []
    }

    def "deve popular listas com o minimo de 5 elementos cada"() {
        when: "o sistema popula os dados"
        Linketinder.popularDados()

        then: "deve haver 5 candidatos e 5 empresas"
        Linketinder.candidatos.size() >= 5
        Linketinder.empresas.size() >= 5
    }

    def "candidato deve conter informacoes obrigatorias"() {
        given: "um candidato cadastrado"
        Linketinder.popularDados()
        def c = Linketinder.candidatos[0]

        expect: "os campos obrigatorios devem existir"
        c.nome != null
        c.cpf != null
        c.competencias instanceof List
    }
}