package rb.aczg.view

class MenuPrincipal {

    static void main(String[] args) {
        Scanner scanner = new Scanner(System.in)

        MenuCandidato  menuCandidato  = new MenuCandidato(scanner)
        MenuEmpresa menuEmpresa    = new MenuEmpresa(scanner)
        MenuCompetencia menuCompetencia = new MenuCompetencia(scanner)

        boolean sair = false
        while (!sair) {
            println """
LINKETINDER — Menu Principal   
1. Candidatos                       
2. Empresas                         
3. Competências                     
0. Sair                             
"""
            print "Opção: "
            String opcao = scanner.nextLine().trim()

            switch (opcao) {
                case '1': menuCandidato.exibir();   break
                case '2': menuEmpresa.exibir();     break
                case '3': menuCompetencia.exibir(); break
                case '0':
                    println "\nAté logo! 👋"
                    sair = true
                    break
                default:
                    println "Opção inválida."
            }
        }
    }
}