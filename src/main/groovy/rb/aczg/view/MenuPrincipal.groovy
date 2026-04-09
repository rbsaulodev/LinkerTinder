package rb.aczg.view

class MenuPrincipal {

    static void main(String[] args) {
        Scanner scanner = new Scanner(System.in)

        MenuCandidato menuCandidato = new MenuCandidato(scanner)
        MenuEmpresa menuEmpresa = new MenuEmpresa(scanner)
        MenuCompetencia menuCompetencia = new MenuCompetencia(scanner)

        boolean sair = false
        while (!sair) {
            println "--- LINKETINDER - MENU PRINCIPAL ---"
            println "1. Candidatos"
            println "2. Empresas"
            println "3. Competencias"
            println "0. Sair"
            print "Opcao: "

            String opcao = scanner.nextLine().trim()

            switch (opcao) {
                case '1': menuCandidato.exibir();   break
                case '2': menuEmpresa.exibir();     break
                case '3': menuCompetencia.exibir(); break
                case '0':
                    println "Saindo... Ate logo!"
                    sair = true
                    break
                default:
                    println "Opcao invalida."
            }
            println ""
        }
    }
}