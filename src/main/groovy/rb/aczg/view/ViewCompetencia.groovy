package rb.aczg.view

import rb.aczg.interfaces.controller.ICompetenciaController
import rb.aczg.model.Competencia

class ViewCompetencia {

    private final ICompetenciaController controller
    private final Scanner scanner

    ViewCompetencia(Scanner scanner, ICompetenciaController controller) {
        this.scanner    = scanner
        this.controller = controller
    }

    void exibir() {
        boolean voltar = false
        while (!voltar) {
            println ""
            println "--- MENU COMPETENCIAS ---"
            println "1. Cadastrar"
            println "2. Listar todas"
            println "3. Buscar por ID"
            println "4. Atualizar"
            println "5. Remover"
            println "0. Voltar"
            print "Opcao: "

            switch (scanner.nextLine().trim()) {
                case '1': cadastrar();   break
                case '2': listarTodas(); break
                case '3': buscarPorId(); break
                case '4': atualizar();   break
                case '5': remover();     break
                case '0': voltar = true; break
                default:  println "Opcao invalida."
            }
        }
    }

    private void cadastrar() {
        print "Nome da competencia: "
        String nome = scanner.nextLine().trim()
        try { controller.cadastrar(nome); println "Competencia cadastrada." }
        catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void listarTodas() {
        List<Competencia> lista = controller.listarTodas()
        if (lista.isEmpty()) println "\nNenhuma competencia."
        else { println "\n--- Competencias ---"; lista.each { println it } }
    }

    private void buscarPorId() {
        print "ID: "
        try { println controller.buscarPorId(scanner.nextLine().trim().toInteger()) }
        catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void atualizar() {
        print "ID: "
        int id = scanner.nextLine().trim().toInteger()
        print "Novo nome: "
        String nome = scanner.nextLine().trim()
        try { controller.atualizar(id, nome); println "Atualizada." }
        catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void remover() {
        print "ID: "
        int id = scanner.nextLine().trim().toInteger()
        print "Confirmar? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            controller.remover(id); println "Removida."
        } else { println "Cancelado." }
    }
}
