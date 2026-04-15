package rb.aczg.service.validation

import rb.aczg.model.Candidato

class CandidatoValidator {

    void validar(Candidato c) {
        if (!c.nome?.trim())
            throw new IllegalArgumentException("Nome e obrigatorio.")
        if (!c.sobrenome?.trim())
            throw new IllegalArgumentException("Sobrenome e obrigatorio.")
        if (!c.email?.trim())
            throw new IllegalArgumentException("Email e obrigatorio.")
        if (!validarEmail(c.email))
            throw new IllegalArgumentException("Email invalido.")
        if (!c.cpf?.trim())
            throw new IllegalArgumentException("CPF e obrigatorio.")
        if (!validarCpf(c.cpf))
            throw new IllegalArgumentException("CPF deve ter 11 digitos numericos.")
        if (!c.dataNasc)
            throw new IllegalArgumentException("Data de nascimento e obrigatoria.")
    }

    private boolean validarEmail(String email) {
        return email ==~ /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    }

    private boolean validarCpf(String cpf) {
        return cpf.replaceAll('[^0-9]', '').length() == 11
    }
}
