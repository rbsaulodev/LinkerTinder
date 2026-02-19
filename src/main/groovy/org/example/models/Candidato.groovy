package org.example.models

class Candidato extends Pessoa {
    String cpf
    int idade

    @Override
    void exibirDados() {
        super.exibirDados()
        println "CANDIDATO: $nome ($idade anos) | CPF: $cpf"
        println "Email: $email | Local: $estado - CEP: $cep"
        println "Descrição: $descricao"
        println "Competências: ${competencias.join(', ')}"
    }
}