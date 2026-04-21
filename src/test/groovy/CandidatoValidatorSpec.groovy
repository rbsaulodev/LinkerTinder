import rb.aczg.model.Candidato
import rb.aczg.service.validation.CandidatoValidator
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

class CandidatoValidatorSpec extends Specification {

    CandidatoValidator validator = new CandidatoValidator()

    def "deve aceitar candidato com dados validos"() {
        given:
        Candidato c = candidatoValido()

        when:
        validator.validar(c)

        then:
        noExceptionThrown()
    }

    @Unroll
    def "deve rejeitar quando nome='#nome'"() {
        given:
        Candidato c = candidatoValido()
        c.nome = nome

        when:
        validator.validar(c)

        then:
        thrown(IllegalArgumentException)

        where:
        nome << [null, '', '   ']
    }

    @Unroll
    def "deve rejeitar quando sobrenome='#sob'"() {
        given:
        Candidato c = candidatoValido()
        c.sobrenome = sob

        when:
        validator.validar(c)

        then:
        thrown(IllegalArgumentException)

        where:
        sob << [null, '', '   ']
    }

    def "deve rejeitar email invalido"() {
        given:
        Candidato c = candidatoValido()
        c.email = "nao-e-um-email"

        when:
        validator.validar(c)

        then:
        thrown(IllegalArgumentException)
    }

    def "deve rejeitar cpf com menos de 11 digitos"() {
        given:
        Candidato c = candidatoValido()
        c.cpf = "1234"

        when:
        validator.validar(c)

        then:
        thrown(IllegalArgumentException)
    }

    def "deve aceitar cpf com mascara formatada"() {
        given:
        Candidato c = candidatoValido()
        c.cpf = "123.456.789-00"

        when:
        validator.validar(c)

        then:
        noExceptionThrown()
    }

    def "deve rejeitar quando dataNasc e nula"() {
        given:
        Candidato c = candidatoValido()
        c.dataNasc = null

        when:
        validator.validar(c)

        then:
        thrown(IllegalArgumentException)
    }

    private Candidato candidatoValido() {
        new Candidato(
            nome:      'Sandubinha',
            sobrenome: 'Silva',
            email:     'sand@email.com',
            cpf:       '12345678900',
            dataNasc:  LocalDate.of(2000, 1, 1)
        )
    }
}
