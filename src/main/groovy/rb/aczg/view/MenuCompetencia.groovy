package rb.aczg.view

import rb.aczg.model.Competencia
import rb.aczg.service.CompetenciaService

class MenuCompetencia {

    private final CompetenciaService service = new CompetenciaService()
    private final Scanner scanner

    MenuCompetencia(Scanner scanner) {
        this.scanner = scanner
    }

    void exibir() {
        boolean voltar = false
        while (!voltar) {
            println "--- MENU COMPETENCIAS ---"
            println "1. Cadastrar competencia"
            println "2. Listar todas"
            println "3. Buscar por ID"
            println "4. Atualizar competencia"
            println "5. Remover competencia"
            println "0. Voltar"
            print "Opcao: "

            String opcao = scanner.nextLine().trim()

            switch (opcao) {
                case '1': cadastrar();    break
                case '2': listarTodas();  break
                case '3': buscarPorId();  break
                case '4': atualizar();    break
                case '5': remover();      break
                case '0': voltar = true;  break
                default:  println "Opcao invalida."
            }
        }
    }

    // CREATE
    private void cadastrar() {
        String nome = ler("Nome da competencia: ")
        try {
            service.cadastrar(nome)
            println "Competencia cadastrada com sucesso."
        } catch (Exception e) {
            println "Erro: ${e.message}"
        }
    }

    // READ
    private void listarTodas() {
        List<Competencia> lista = service.listarTodas()
        if (lista.isEmpty()) {
            println "\nNenhuma competencia cadastrada."
        } else {
            println "\n--- Competencias ---"
            lista.each { println it }
        }
    }

    private void buscarPorId() {
        int id = lerInt("ID da competencia: ")
        try {
            println service.buscarPorId(id)
        } catch (Exception e) {
            println "Erro: ${e.message}"
        }
    }

    // UPDATE
    private void atualizar() {
        int id = lerInt("ID da competencia a atualizar: ")
        String nome = ler("Novo nome: ")
        try {
            service.atualizar(id, nome)
            println "Competencia atualizada."
        } catch (Exception e) {
            println "Erro: ${e.message}"
        }
    }

    // DELETE
    private void remover() {
        int id = lerInt("ID da competencia a remover: ")
        print "Confirmar remocao? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            service.remover(id)
            println "Competencia removida."
        } else {
            println "Operacao cancelada."
        }
    }

    // UTIL
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
                println "Digite um numero valido."
            }
        }
    }
}