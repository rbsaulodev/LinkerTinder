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
            println """
MENU — COMPETÊNCIAS

1. Cadastrar competência            
2. Listar todas                     
3. Buscar por ID                    
4. Atualizar competência            
5. Remover competência              
0. Voltar  
"""
            print "Opção: "
            String opcao = scanner.nextLine().trim()

            switch (opcao) {
                case '1': cadastrar();  break
                case '2': listarTodas();    break
                case '3': buscarPorId();    break
                case '4': atualizar();  break
                case '5': remover();    break
                case '0': voltar = true;    break
                default:  println "Opção inválida."
            }
        }
    }

    private void cadastrar() {
        print "Nome da competência: "
        String nome = scanner.nextLine().trim()
        try {
            service.cadastrar(nome)
        } catch (Exception e) {
            println "${e.message}"
        }
    }

    private void listarTodas() {
        List<Competencia> lista = service.listarTodas()
        if (lista.isEmpty()) {
            println "\nNenhuma competência cadastrada."
        } else {
            println "\n─── Competências ───"
            lista.each { println it }
        }
    }

    private void buscarPorId() {
        print "ID: "
        int id = scanner.nextLine().trim().toInteger()
        try {
            println service.buscarPorId(id)
        } catch (Exception e) {
            println "${e.message}"
        }
    }

    private void atualizar() {
        print "ID da competência: "
        int id = scanner.nextLine().trim().toInteger()
        print "Novo nome: "
        String nome = scanner.nextLine().trim()
        try {
            service.atualizar(id, nome)
        } catch (Exception e) {
            println "${e.message}"
        }
    }

    private void remover() {
        print "ID da competência: "
        int id = scanner.nextLine().trim().toInteger()
        print "Confirmar remoção? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            service.remover(id)
        } else {
            println "Operação cancelada."
        }
    }
}