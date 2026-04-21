import rb.aczg.model.Empresa
import rb.aczg.service.validation.EmpresaValidator
import spock.lang.Specification
import spock.lang.Unroll

class EmpresaValidatorSpec extends Specification {

    EmpresaValidator validator = new EmpresaValidator()

    def "deve aceitar empresa com dados validos"() {
        given:
        Empresa e = empresaValida()

        when:
        validator.validar(e)

        then:
        noExceptionThrown()
    }

    @Unroll
    def "deve rejeitar quando nome='#nome'"() {
        given:
        Empresa e = empresaValida()
        e.nome = nome

        when:
        validator.validar(e)

        then:
        thrown(IllegalArgumentException)

        where:
        nome << [null, '', '   ']
    }

    def "deve rejeitar email invalido"() {
        given:
        Empresa e = empresaValida()
        e.email = "invalido"

        when:
        validator.validar(e)

        then:
        thrown(IllegalArgumentException)
    }

    def "deve rejeitar cnpj com menos de 14 digitos"() {
        given:
        Empresa e = empresaValida()
        e.cnpj = "1234"

        when:
        validator.validar(e)

        then:
        thrown(IllegalArgumentException)
    }

    def "deve aceitar cnpj com 14 digitos numericos"() {
        given:
        Empresa e = empresaValida()
        e.cnpj = "11222333000144"

        when:
        validator.validar(e)

        then:
        noExceptionThrown()
    }

    private Empresa empresaValida() {
        new Empresa(
            nome:  'Pastelsoft',
            email: 'contato@pastelsoft.com',
            cnpj:  '11222333000144'
        )
    }
}
