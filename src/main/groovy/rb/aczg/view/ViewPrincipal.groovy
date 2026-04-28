package rb.aczg.view

import rb.aczg.controller.CandidatoController
import rb.aczg.controller.CompetenciaController
import rb.aczg.controller.EmpresaController
import rb.aczg.controller.VagaController
import rb.aczg.dao.*
import rb.aczg.dao.factory.PostgresConnectionFactory
import rb.aczg.interfaces.controller.ICandidatoController
import rb.aczg.interfaces.controller.ICompetenciaController
import rb.aczg.interfaces.controller.IEmpresaController
import rb.aczg.interfaces.controller.IVagaController
import rb.aczg.interfaces.dao.*
import rb.aczg.interfaces.service.ICandidatoService
import rb.aczg.interfaces.service.ICompetenciaService
import rb.aczg.interfaces.service.IEmpresaService
import rb.aczg.interfaces.service.IVagaService
import rb.aczg.service.CandidatoService
import rb.aczg.service.CompetenciaService
import rb.aczg.service.EmpresaService
import rb.aczg.service.VagaService
import rb.aczg.service.observer.LogMatchObserver
import rb.aczg.service.observer.NotificacaoMatchObserver
import rb.aczg.service.validation.CandidatoValidator
import rb.aczg.service.validation.EmpresaValidator

class ViewPrincipal {

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
        ICandidatoService candidatoService = new CandidatoService(candidatoDAO, enderecoDAO, competenciaDAO, vagaDAO, new CandidatoValidator(), matchObservers)

        ICandidatoController candidatoController = new CandidatoController(candidatoService)
        IEmpresaController empresaController = new EmpresaController(empresaService)
        IVagaController vagaController = new VagaController(vagaService)
        ICompetenciaController competenciaController = new CompetenciaController(competenciaService)

        Scanner scanner = new Scanner(System.in)
        ViewCandidato viewCandidato = new ViewCandidato(scanner, candidatoController)
        ViewEmpresa viewEmpresa = new ViewEmpresa(scanner, empresaController, vagaController)
        ViewCompetencia viewCompetencia  = new ViewCompetencia(scanner, competenciaController)

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
                case '1': viewCandidato.exibir();   break
                case '2': viewEmpresa.exibir();     break
                case '3': viewCompetencia.exibir(); break
                case '0': println "Ate logo!"; sair = true; break
                default:  println "Opcao invalida."
            }
        }
    }
}
