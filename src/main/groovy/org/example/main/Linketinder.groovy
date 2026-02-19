package org.example.main

import org.example.models.Candidato
import org.example.models.Empresa

class Linketinder {
    // Desenvolvedor: Saulo Brilhante
    static List<Candidato> candidatos = []
    static List<Empresa> empresas = []

    static void main(args) {
        popularDados()
        menu()
    }

    static void cadastrarCandidato(Candidato candidato) {
        if (candidato) candidatos << candidato
    }

    static void cadastrarEmpresa(Empresa empresa) {
        if (empresa) empresas << empresa
    }

    static void popularDados() {
        cadastrarCandidato(new Candidato(nome: "Saulo Brilhante", email: "saulo@email.com", cpf: "123", idade: 25, estado: "GO", cep: "74000", descricao: "Dev Groovy", competencias: ["Java", "Groovy"]))
        cadastrarCandidato(new Candidato(nome: "Ana Souza", email: "ana@email.com", cpf: "222", idade: 28, estado: "SP", cep: "01000", descricao: "Frontend", competencias: ["Angular", "CSS"]))
        cadastrarCandidato(new Candidato(nome: "Bruno Lima", email: "bruno@email.com", cpf: "333", idade: 22, estado: "RJ", cep: "20000", descricao: "Backend", competencias: ["Python", "Docker"]))
        cadastrarCandidato(new Candidato(nome: "Carla Dias", email: "carla@email.com", cpf: "444", idade: 30, estado: "MG", cep: "30000", descricao: "Mobile", competencias: ["Flutter"]))
        cadastrarCandidato(new Candidato(nome: "Daniel Silva", email: "daniel@email.com", cpf: "555", idade: 26, estado: "SC", cep: "88000", descricao: "Data Science", competencias: ["Python", "Pandas"]))

        cadastrarEmpresa(new Empresa(nome: "Arroz-Gostoso", email: "rh@arroz.com", cnpj: "12.345", pais: "Brasil", estado: "GO", cep: "74000", descricao: "Líder alimentício", competencias: ["Java", "SQL"]))
        cadastrarEmpresa(new Empresa(nome: "Império do Boliche", email: "vagas@boliche.com", cnpj: "23.456", pais: "Brasil", estado: "GO", cep: "74000", descricao: "Centro de lazer", competencias: ["Angular", "Node.js"]))
        cadastrarEmpresa(new Empresa(nome: "Zup Innovation", email: "talents@zup.com", cnpj: "34.567", pais: "Brasil", estado: "SP", cep: "04538", descricao: "Consultoria tech", competencias: ["Groovy", "AWS"]))
        cadastrarEmpresa(new Empresa(nome: "Brilhante Tech", email: "contato@brilhante.io", cnpj: "45.678", pais: "Brasil", estado: "GO", cep: "74123", descricao: "Soluções CNH", competencias: ["Groovy", "PostgreSQL"]))
        cadastrarEmpresa(new Empresa(nome: "Global Tech", email: "hr@globaltech.com", cnpj: "56.789", pais: "EUA", estado: "NY", cep: "10001", descricao: "Escalabilidade global", competencias: ["Python", "React"]))
    }

    static void menu() {
        def scanner = new Scanner(System.in)
        def opcao = -1

        while (opcao != 0) {
            println "\n=== LINKETINDER ==="
            println "1. Listar Candidatos"
            println "2. Listar Empresas"
            println "3. Cadastrar Novo Candidato"
            println "4. Cadastrar Nova Empresa"
            println "0. Sair"
            print "Escolha uma opção: "

            def entrada = scanner.nextLine()
            if (entrada.isInteger()) {
                opcao = entrada.toInteger()
                switch (opcao) {
                    case 1: candidatos.each { it.exibirDados() }; break
                    case 2: empresas.each { it.exibirDados() }; break
                    case 3: criarCandidatoManualmente(scanner); break
                    case 4: criarEmpresaManualmente(scanner); break
                    case 0: println "Encerrando sistema..."; break
                    default: println "Opção inválida!"
                }
            }
        }
    }

    static void criarCandidatoManualmente(Scanner scanner) {
        println "\n--- Cadastro de Novo Candidato ---"
        print "Nome: "; String nome = scanner.nextLine()
        print "Email: "; String email = scanner.nextLine()
        print "CPF: "; String cpf = scanner.nextLine()
        print "Idade: "; int idade = scanner.nextLine().toInteger()
        print "Estado: "; String estado = scanner.nextLine()
        print "CEP: "; String cep = scanner.nextLine()
        print "Descrição: "; String desc = scanner.nextLine()
        print "Competências (separadas por vírgula): "
        List comps = scanner.nextLine().split(",").collect { it.trim() }

        def novo = new Candidato(nome: nome, email: email, cpf: cpf, idade: idade,
                estado: estado, cep: cep, descricao: desc, competencias: comps)
        cadastrarCandidato(novo)
        println "Candidato cadastrado com sucesso!"
    }

    static void criarEmpresaManualmente(Scanner scanner) {
        println "\n--- Cadastro de Nova Empresa ---"
        print "Nome: "; String nome = scanner.nextLine()
        print "Email Corporativo: "; String email = scanner.nextLine()
        print "CNPJ: "; String cnpj = scanner.nextLine()
        print "País: "; String pais = scanner.nextLine()
        print "Estado: "; String estado = scanner.nextLine()
        print "CEP: "; String cep = scanner.nextLine()
        print "Descrição: "; String desc = scanner.nextLine()
        print "Competências desejadas (separadas por vírgula): "
        List comps = scanner.nextLine().split(",").collect { it.trim() }

        def nova = new Empresa(nome: nome, email: email, cnpj: cnpj, pais: pais,
                estado: estado, cep: cep, descricao: desc, competencias: comps)
        cadastrarEmpresa(nova)
        println "Empresa cadastrada com sucesso!"
    }
}