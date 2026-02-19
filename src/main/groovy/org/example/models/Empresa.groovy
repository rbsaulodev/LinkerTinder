package org.example.models

class Empresa extends Pessoa {
    String cnpj
    String pais

    @Override
    void exibirDados() {
        super.exibirDados()
        println "EMPRESA: $nome | CNPJ: $cnpj"
        println "Email: $email | País: $pais | Local: $estado"
        println "O que buscamos: $descricao"
        println "Skills desejadas: ${competencias.join(', ')}"
    }
}