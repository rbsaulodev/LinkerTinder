import spock.lang.Specification
import org.example.models.Candidato
import org.example.models.Empresa
import org.example.main.Linketinder

class CadastroSpec extends Specification {

    def setup() {
        Linketinder.candidatos = []
        Linketinder.empresas = []
    }

    def "deve inserir um novo candidato na lista de candidatos"() {
        given: "um candidato pronto para cadastro"
        def novoCandidato = new Candidato(nome: "Teste TDD", email: "tdd@linketinder.com")

        when: "o método de cadastro de candidato é executado"
        Linketinder.cadastrarCandidato(novoCandidato)

        then: "a lista de candidatos deve ter exatamente 1 elemento"
        Linketinder.candidatos.size() == 1
        Linketinder.candidatos[0].nome == "Teste TDD"
    }

    def "deve inserir uma nova empresa na lista de empresas"() {
        given: "uma empresa pronta para cadastro"
        def novaEmpresa = new Empresa(nome: "Empresa TDD", cnpj: "99.999/0001")

        when: "o método de cadastro de empresa é executado"
        Linketinder.cadastrarEmpresa(novaEmpresa)

        then: "a lista de empresas deve ter exatamente 1 elemento"
        Linketinder.empresas.size() == 1
        Linketinder.empresas[0].nome == "Empresa TDD"
    }
}