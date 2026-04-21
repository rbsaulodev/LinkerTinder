import rb.aczg.interfaces.dao.*
import rb.aczg.model.Empresa
import rb.aczg.model.Endereco
import rb.aczg.model.Match
import rb.aczg.model.Vaga
import rb.aczg.service.EmpresaService
import rb.aczg.service.validation.EmpresaValidator
import spock.lang.Specification

import java.time.LocalDateTime

class EmpresaServiceSpec extends Specification {

    IEmpresaDAO empresaDAO = Mock()
    IEnderecoDAO enderecoDAO = Mock()
    IVagaDAO vagaDAO = Mock()
    ICompetenciaDAO  competenciaDAO = Mock()
    ICandidatoDAO candidatoDAO = Mock()
    EmpresaValidator validator = new EmpresaValidator()

    EmpresaService service

    def setup() {
        service = new EmpresaService(
            empresaDAO, enderecoDAO, vagaDAO, competenciaDAO, candidatoDAO, validator)
    }

    def "cadastrar deve rejeitar cnpj invalido"() {
        given:
        Empresa e = empresaValida()
        e.cnpj = "123"

        when:
        service.cadastrar(e)

        then:
        thrown(IllegalArgumentException)
        0 * empresaDAO.inserir(_)
    }

    def "buscarPorId deve lancar excecao quando empresa nao existe"() {
        given:
        empresaDAO.buscarPorId(99) >> null

        when:
        service.buscarPorId(99)

        then:
        thrown(RuntimeException)
    }

    def "publicarVaga deve inserir endereco da vaga se necessario"() {
        given:
        Vaga v = new Vaga(empresaId: 1, titulo: 'Dev Java',
                          status: 'Aberta', endereco: new Endereco(cidade: 'SP'))
        enderecoDAO.inserir(_) >> new Endereco(id: 2)
        vagaDAO.inserir(_) >> { Vaga arg -> arg.id = 10; arg }

        when:
        Vaga resultado = service.publicarVaga(v)

        then:
        1 * enderecoDAO.inserir(_)
        resultado.id == 10
    }

    def "publicarVaga deve rejeitar vaga sem titulo"() {
        given:
        Vaga v = new Vaga(empresaId: 1, titulo: '')

        when:
        service.publicarVaga(v)

        then:
        thrown(IllegalArgumentException)
        0 * vagaDAO.inserir(_)
    }

    def "curtirCandidato deve curtir e tentar gerar match"() {
        when:
        service.curtirCandidato(2, 1)

        then:
        1 * vagaDAO.curtirCandidato(2, 1)
        1 * vagaDAO.gerarMatchSeAmbosCurtiram(1, 2)
    }

    def "verMatchesDaEmpresa deve agregar matches de todas as vagas"() {
        given:
        vagaDAO.listarPorEmpresa(1) >> [
            new Vaga(id: 10, titulo: 'Dev'), new Vaga(id: 11, titulo: 'DBA')
        ]
        vagaDAO.listarMatchesPorVaga(10) >> [
            new Match(id: 1, matchedEm: LocalDateTime.now())
        ]
        vagaDAO.listarMatchesPorVaga(11) >> []

        when:
        List<Match> matches = service.verMatchesDaEmpresa(1)

        then:
        matches.size() == 1
    }

    def "removerVaga deve delegar ao vagaDAO"() {
        when:
        service.removerVaga(7)

        then:
        1 * vagaDAO.deletar(7)
    }

    private Empresa empresaValida() {
        new Empresa(
            nome:     'Pastelsoft',
            email:    'contato@pastelsoft.com',
            cnpj:     '11222333000144',
            endereco: new Endereco(cep: '01310100', cidade: 'SP', estado: 'SP', pais: 'Brasil')
        )
    }
}
