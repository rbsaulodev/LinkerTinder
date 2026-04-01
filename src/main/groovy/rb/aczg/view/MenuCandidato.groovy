package rb.aczg.view

import rb.aczg.model.Candidato
import rb.aczg.model.Competencia
import rb.aczg.model.Vaga
import rb.aczg.service.CandidatoService

class MenuCandidato {

    private final CandidatoService service = new CandidatoService()
    private final Scanner scanner

    MenuCandidato(Scanner scanner) {
        this.scanner = scanner
    }

    void exibir() {
        boolean voltar = false
        while (!voltar) {
            println """
MENU — CANDIDATOS
            
1. Cadastrar candidato             
2. Listar todos os candidatos       
3. Buscar candidato por ID          
4. Atualizar candidato              
5. Remover candidato                
6. Adicionar competência            
7. Remover competência              
8. Ver vagas compatíveis (match)    
0. Voltar                
            """
            print "Opção: "
            String opcao = scanner.nextLine().trim()

            switch (opcao) {
                case '1': cadastrar();  break
                case '2': listarTodos();    break
                case '3': buscarPorId();    break
                case '4': atualizar();  break
                case '5': remover();    break
                case '6': adicionarComp();  break
                case '7': removerComp();    break
                case '8': verMatches(); break
                case '0': voltar = true;    break
                default:  println "Opção inválida."
            }
        }
    }

    private void cadastrar() {
        println "\n─── Novo Candidato ───"
        Candidato c = new Candidato()
        c.nome = ler("Nome: ")
        c.sobrenome = ler("Sobrenome: ")
        c.email = ler("Email: ")
        c.cpf = ler("CPF: ")
        c.idade = lerInt("Idade: ")
        c.estado = ler("Estado (UF): ")
        c.cep = ler("CEP: ")
        c.descricao = ler("Descrição (opcional): ")

        print "Competências (separadas por vírgula, ou deixe em branco): "
        String comps = scanner.nextLine().trim()
        if (comps) {
            c.competencias = comps.split(',').collect { new Competencia(nome: it.trim()) }
        }

        try {
            service.cadastrar(c)
        } catch (Exception e) {
            println "❌ Erro: ${e.message}"
        }
    }

    private void listarTodos() {
        List<Candidato> lista = service.listarTodos()
        if (lista.isEmpty()) {
            println "\nNenhum candidato cadastrado."
            return
        }
        println "\n─── Candidatos ───"
        lista.each { println it }
    }

    private void buscarPorId() {
        int id = lerInt("ID do candidato: ")
        try {
            println service.buscarPorId(id)
        } catch (Exception e) {
            println "${e.message}"
        }
    }

    private void atualizar() {
        int id = lerInt("ID do candidato a atualizar: ")
        try {
            Candidato c = service.buscarPorId(id)
            println "Deixe em branco para manter o valor atual."
            String nome = ler("Nome [${c.nome}]: ")
            if (nome) c.nome = nome
            String sob = ler("Sobrenome [${c.sobrenome}]: ")
            if (sob) c.sobrenome = sob
            String email = ler("Email [${c.email}]: ")
            if (email) c.email = email
            String cpf = ler("CPF [${c.cpf}]: ")
            if (cpf) c.cpf = cpf
            String idade = ler("Idade [${c.idade}]: ")
            if (idade) c.idade = idade.toInteger()
            String estado = ler("Estado [${c.estado}]: ")
            if (estado) c.estado = estado
            String cep = ler("CEP [${c.cep}]: ")
            if (cep) c.cep = cep
            String desc = ler("Descrição [${c.descricao ?: ''}]: ")
            if (desc) c.descricao = desc
            service.atualizar(c)
        } catch (Exception e) {
            println "${e.message}"
        }
    }

    private void remover() {
        int id = lerInt("ID do candidato a remover: ")
        print "Confirmar remoção? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            service.remover(id)
        } else {
            println "Operação cancelada."
        }
    }

    private void adicionarComp() {
        int id = lerInt("ID do candidato: ")
        String comp = ler("Nome da competência: ")
        try {
            service.adicionarCompetencia(id, comp)
        } catch (Exception e) {
            println "${e.message}"
        }
    }

    private void removerComp() {
        int candidatoId   = lerInt("ID do candidato: ")
        int competenciaId = lerInt("ID da competência: ")
        try {
            service.removerCompetencia(candidatoId, competenciaId)
        } catch (Exception e) {
            println "${e.message}"
        }
    }

    private void verMatches() {
        int id = lerInt("ID do candidato: ")
        try {
            List<Vaga> vagas = service.verMatchesDeVagas(id)
            if (vagas.isEmpty()) {
                println "\nNenhuma vaga compatível encontrada."
            } else {
                println "\n─── Vagas compatíveis ───"
                vagas.each { println it }
            }
        } catch (Exception e) {
            println "${e.message}"
        }
    }

    private String ler(String label) {
        print label
        return scanner.nextLine().trim()
    }

    private int lerInt(String label) {
        while (true) {
            print label
            String entrada = scanner.nextLine().trim()
            try {
                return entrada.toInteger()
            } catch (NumberFormatException ignored) {
                println "Digite um número válido."
            }
        }
    }
}