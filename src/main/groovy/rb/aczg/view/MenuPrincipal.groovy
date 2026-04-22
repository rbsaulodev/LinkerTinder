package rb.aczg.view

import rb.aczg.dao.*
import rb.aczg.dao.factory.PostgresConnectionFactory
import rb.aczg.interfaces.dao.*
import rb.aczg.interfaces.service.*
import rb.aczg.service.*
import rb.aczg.service.observer.*
import rb.aczg.service.validation.*

class MenuPrincipal {

    static void main(String[] args) {

        ConexaoBD.configurar(new PostgresConnectionFactory())
        IConexao conexao = ConexaoBD.instancia()

        IEnderecoDAO enderecoDAO = new EnderecoDAO(conexao)
        ICompetenciaDAO competenciaDAO = new CompetenciaDAO(conexao)
        IVagaDAO vagaDAO = new VagaDAO(conexao, competenciaDAO)
        ICandidatoDAO candidatoDAO = new CandidatoDAO(conexao, competenciaDAO)
        IEmpresaDAO empresaDAO = new EmpresaDAO(conexao, vagaDAO)

        def matchObservers = [new LogMatchObserver(), new NotificacaoMatchObserver()]

        ICompetenciaService competenciaService = new CompetenciaService(competenciaDAO)
        IEmpresaService empresaService = new EmpresaService(empresaDAO, enderecoDAO, new EmpresaValidator())
        IVagaService vagaService = new VagaService(vagaDAO, enderecoDAO, competenciaDAO, candidatoDAO, matchObservers)

        ICandidatoService candidatoService = new CandidatoService(
                candidatoDAO, enderecoDAO, competenciaDAO, vagaDAO,
                new CandidatoValidator(), matchObservers)

        Scanner scanner = new Scanner(System.in)
        MenuCandidato menuCandidato = new MenuCandidato(scanner, candidatoService)
        MenuEmpresa menuEmpresa = new MenuEmpresa(scanner, empresaService, vagaService)
        MenuCompetencia menuCompetencia = new MenuCompetencia(scanner, competenciaService)

        boolean sair = false
        while (!sair) {
            println ""
            println "=== LINKETINDER - MENU PRINCIPAL ==="
            println "1. Candidatos"
            println "2. Empresas"
            println "3. Competencias"
            println "0. Sair"
            print "Opcao: "

            switch (scanner.nextLine().trim()) {
                case '1': menuCandidato.exibir();   break
                case '2': menuEmpresa.exibir();     break
                case '3': menuCompetencia.exibir(); break
                case '0': println "Ate logo!"; sair = true; break
                default:  println "Opcao invalida."
            }
        }
    }
}
