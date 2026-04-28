package rb.aczg.view

import rb.aczg.interfaces.controller.ICandidatoController
import rb.aczg.model.Candidato
import rb.aczg.model.Endereco
import rb.aczg.model.Vaga

import java.time.LocalDate

class ViewCandidato {

    private final ICandidatoController controller
    private final Scanner scanner

    ViewCandidato(Scanner scanner, ICandidatoController controller) {
        this.scanner    = scanner
        this.controller = controller
    }

    void exibir() {
        boolean voltar = false
        while (!voltar) {
            println ""
            println "--- MENU CANDIDATOS ---"
            println "1.  Cadastrar candidato"
            println "2.  Listar todos"
            println "3.  Buscar por ID"
            println "4.  Atualizar candidato"
            println "5.  Remover candidato"
            println "6.  Adicionar competencia"
            println "7.  Remover competencia"
            println "8.  Ver vagas compativeis (match)"
            println "9.  Curtir vaga"
            println "0.  Voltar"
            print "Opcao: "

            switch (scanner.nextLine().trim()) {
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
        Candidato candidato = new Candidato()
        candidato.nome = ler("Nome: ")
        candidato.sobrenome = ler("Sobrenome: ")
        candidato.email = ler("Email: ")
        candidato.cpf = ler("CPF (somente numeros): ")
        candidato.dataNasc  = lerData("Data de nascimento (AAAA-MM-DD): ")
        candidato.descricao = ler("Descricao (opcional): ")

        c.endereco = lerEndereco()

        print "Competencias (separadas por virgula, ou ENTER para pular): "
        String comps = scanner.nextLine().trim()

        try {
            Candidato inserido = controller.cadastrar(candidato, comps)
            println "Candidato cadastrado! ID: ${inserido.id}"
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void listarTodos() {
        List<Candidato> lista = controller.listarTodos()
        if (lista.isEmpty()) { println "\nNenhum candidato cadastrado."; return }
        println "\n--- Candidatos ---"
        lista.each { println it }
    }

    private void buscarPorId() {
        try { println controller.buscarPorId(lerInt("ID: ")) }
        catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void atualizar() {
        int id = lerInt("ID do candidato: ")
        try {
            Candidato c = controller.buscarPorId(id)
            println "Deixe em branco para manter."

            String nome = ler ("Nome [${c.nome}]: ");
            if (nome){
                c.nome = nome
            }

            String sob = ler ("Sobrenome [${c.sobrenome}]: ");
            if (sob){
                c.sobrenome = sob
            }

            String email = ler ("Email [${c.email}]: ");
            if (email){
                c.email = email
            }

            String desc = ler ("Descricao [${c.descricao ?: ''}]: ");
            if (desc){
                c.descricao = desc
            }

            String cidade = ler ("Cidade [${c.endereco.cidade ?: ''}]: ");
            if (cidade){
                c.endereco.cidade = cidade
            }

            String est = ler ("Estado [${c.endereco.estado ?: ''}]: ");
            if (est){
                c.endereco.estado = est
            }
            controller.atualizar(c)
            println "Candidato atualizado."
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void remover() {
        int id = lerInt("ID: ")
        print "Confirmar? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            controller.remover(id); println "Removido."
        } else { println "Cancelado." }
    }

    private void adicionarComp() {
        int    id    = lerInt("ID do candidato: ")
        String nome  = ler("Competencia: ")
        String nivel = ler("Nivel (Basico/Intermediario/Avancado ou ENTER): ")
        try { controller.adicionarCompetencia(id, nome, nivel ?: null); println "Adicionada." }
        catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void removerComp() {
        try {
            controller.removerCompetencia(lerInt("ID do candidato: "), lerInt("ID da competencia: "))
            println "Removida."
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void verMatches() {
        try {
            List<Vaga> vagas = controller.verMatchesDeVagas(lerInt("ID do candidato: "))
            if (vagas.isEmpty()) println "\nNenhuma vaga compativel."
            else { println "\n--- Vagas Compativeis ---"; vagas.each { println it } }
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void curtirVaga() {
        try {
            controller.curtirVaga(lerInt("ID do candidato: "), lerInt("ID da vaga: "))
            println "Curtida registrada!"
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private Endereco lerEndereco() {
        Endereco end = new Endereco()
        end.cep = ler("CEP: ")
        end.logradouro  = ler("Logradouro: ")
        end.numero = ler("Numero: ")
        end.complemento = ler("Complemento (opcional): ")
        end.bairro = ler("Bairro: ")
        end.cidade = ler("Cidade: ")
        end.estado = ler("Estado (UF): ")
        end.pais = ler("Pais: ")
        return end
    }

    private String ler(String label) { print label; scanner.nextLine().trim() }

    private int lerInt(String label) {
        while (true) {
            print label
            try { return scanner.nextLine().trim().toInteger() }
            catch (NumberFormatException ignored) { println "Digite um numero valido." }
        }
    }

    private LocalDate lerData(String label) {
        print label
        try { return LocalDate.parse(scanner.nextLine().trim()) }
        catch (Exception ignored) {
            println "Data invalida. Usando 2000-01-01."
            return LocalDate.of(2000, 1, 1)
        }
    }
}
