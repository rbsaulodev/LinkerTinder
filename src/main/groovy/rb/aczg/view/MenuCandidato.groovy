package rb.aczg.view

import rb.aczg.model.Candidato
import rb.aczg.model.Endereco
import rb.aczg.model.Vaga
import rb.aczg.service.CandidatoService

import java.time.LocalDate

class MenuCandidato {

    private final CandidatoService service = new CandidatoService()
    private final Scanner scanner

    MenuCandidato(Scanner scanner) {
        this.scanner = scanner
    }

    void exibir() {
        boolean voltar = false
        while (!voltar) {
            println ""
            println "--- MENU CANDIDATOS ---"
            println "1.  Cadastrar candidato"
            println "2.  Listar todos os candidatos"
            println "3.  Buscar candidato por ID"
            println "4.  Atualizar candidato"
            println "5.  Remover candidato"
            println "--- COMPETENCIAS ---"
            println "6.  Adicionar competencia"
            println "7.  Remover competencia"
            println "--- INTERACOES ---"
            println "8.  Ver vagas compativeis (match por competencias)"
            println "9.  Curtir uma vaga"
            println "0.  Voltar"
            print "Opcao: "

            String opcao = scanner.nextLine().trim()
            switch (opcao) {
                case '1': cadastrar();     break
                case '2': listarTodos();   break
                case '3': buscarPorId();   break
                case '4': atualizar();     break
                case '5': remover();       break
                case '6': adicionarComp(); break
                case '7': removerComp();   break
                case '8': verMatches();    break
                case '9': curtirVaga();    break
                case '0': voltar = true;   break
                default:  println "Opcao invalida."
            }
        }
    }

    private void cadastrar() {
        println "\n--- Novo Candidato ---"
        Candidato c = new Candidato()
        c.nome      = ler("Nome: ")
        c.sobrenome = ler("Sobrenome: ")
        c.email     = ler("Email: ")
        c.cpf       = ler("CPF (somente numeros): ")

        // BUG CORRIGIDO: data_nasc no lugar de idade
        String nasc = ler("Data de nascimento (AAAA-MM-DD): ")
        try {
            c.dataNasc = LocalDate.parse(nasc)
        } catch (Exception e) {
            println "Data invalida. Usando data padrao 2000-01-01."
            c.dataNasc = LocalDate.of(2000, 1, 1)
        }

        c.descricao = ler("Descricao (opcional): ")

        // Endereco
        c.endereco = new Endereco()
        c.endereco.cep        = ler("CEP (somente numeros): ")
        c.endereco.logradouro = ler("Logradouro/Rua: ")
        c.endereco.numero     = ler("Numero: ")
        c.endereco.complemento = ler("Complemento (opcional): ")
        c.endereco.bairro     = ler("Bairro: ")
        c.endereco.cidade     = ler("Cidade: ")
        c.endereco.estado     = ler("Estado (UF): ")
        c.endereco.pais       = ler("Pais: ")

        print "Competencias (separadas por virgula, ou ENTER para pular): "
        String comps = scanner.nextLine().trim()

        try {
            Candidato inserido = service.cadastrar(c)
            if (comps) {
                comps.split(',').each { nome ->
                    if (nome.trim()) service.adicionarCompetencia(inserido.id, nome.trim())
                }
            }
            println "Candidato cadastrado com sucesso! ID: ${inserido.id}"
        } catch (Exception e) {
            println "Erro: ${e.message}"
        }
    }

    private void listarTodos() {
        List<Candidato> lista = service.listarTodos()
        if (lista.isEmpty()) { println "\nNenhum candidato cadastrado."; return }
        println "\n--- Lista de Candidatos ---"
        lista.each { println it }
    }

    private void buscarPorId() {
        int id = lerInt("ID do candidato: ")
        try { println service.buscarPorId(id) }
        catch (Exception e) { println "Erro: ${e.message}" }
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

            String desc = ler("Descricao [${c.descricao ?: ''}]: ")
            if (desc) c.descricao = desc

            println "--- Endereco (deixe em branco para manter) ---"
            String logr = ler("Logradouro [${c.endereco.logradouro ?: ''}]: ")
            if (logr) c.endereco.logradouro = logr

            String cidade = ler("Cidade [${c.endereco.cidade ?: ''}]: ")
            if (cidade) c.endereco.cidade = cidade

            String est = ler("Estado [${c.endereco.estado ?: ''}]: ")
            if (est) c.endereco.estado = est

            service.atualizar(c)
            println "Candidato atualizado."
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void remover() {
        int id = lerInt("ID do candidato a remover: ")
        print "Confirmar remocao? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            service.remover(id)
            println "Candidato removido."
        } else { println "Operacao cancelada." }
    }

    private void adicionarComp() {
        int id      = lerInt("ID do candidato: ")
        String nome = ler("Nome da competencia: ")
        String nivel = ler("Nivel (Basico/Intermediario/Avancado, ou ENTER para pular): ")
        try {
            service.adicionarCompetencia(id, nome, nivel ?: null)
            println "Competencia adicionada."
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void removerComp() {
        int candId = lerInt("ID do candidato: ")
        int compId = lerInt("ID da competencia: ")
        try {
            service.removerCompetencia(candId, compId)
            println "Competencia removida."
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void verMatches() {
        int id = lerInt("ID do candidato: ")
        try {
            List<Vaga> vagas = service.verMatchesDeVagas(id)
            if (vagas.isEmpty()) { println "\nNenhuma vaga compativel encontrada." }
            else {
                println "\n--- Vagas Compativeis por Competencia ---"
                vagas.each { println it }
            }
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void curtirVaga() {
        int candidatoId = lerInt("ID do candidato: ")
        int vagaId      = lerInt("ID da vaga: ")
        try {
            service.curtirVaga(candidatoId, vagaId)
            println "Curtida registrada!"
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private String ler(String label) {
        print label
        return scanner.nextLine().trim()
    }

    private int lerInt(String label) {
        while (true) {
            print label
            try { return scanner.nextLine().trim().toInteger() }
            catch (NumberFormatException ignored) { println "Digite um numero valido." }
        }
    }
}
