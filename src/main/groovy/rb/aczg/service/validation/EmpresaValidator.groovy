package rb.aczg.service.validation

import rb.aczg.model.Empresa

class EmpresaValidator {

    void validar(Empresa e) {
        if (!e.nome?.trim())
            throw new IllegalArgumentException("Nome e obrigatorio.")
        if (!e.email?.trim())
            throw new IllegalArgumentException("Email e obrigatorio.")
        if (!validarEmail(e.email))
            throw new IllegalArgumentException("Email invalido.")
        if (!e.cnpj?.trim())
            throw new IllegalArgumentException("CNPJ e obrigatorio.")
        if (!validarCnpj(e.cnpj))
            throw new IllegalArgumentException("CNPJ deve ter 14 digitos numericos.")
    }

    private boolean validarEmail(String email) {
        return email ==~ /^[^\s@]+@[^\s@]+\.[^\s@]+$/
    }

    private boolean validarCnpj(String cnpj) {
        return cnpj.replaceAll('[^0-9]', '').length() == 14
    }
}
