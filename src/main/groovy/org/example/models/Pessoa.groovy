package org.example.models

abstract class Pessoa implements IPessoa {
    String nome
    String email
    String estado
    String cep
    String descricao
    List<String> competencias = []

    @Override
    void exibirDados() {
        println "--------------------------------------"
    }
}