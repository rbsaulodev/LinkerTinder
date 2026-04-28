package rb.aczg.view

import rb.aczg.interfaces.controller.IEmpresaController
import rb.aczg.interfaces.controller.IVagaController
import rb.aczg.model.*

class ViewEmpresa {

    private final IEmpresaController empresaController
    private final IVagaController vagaController
    private final Scanner scanner

    ViewEmpresa(Scanner scanner, IEmpresaController empresaController, IVagaController vagaController) {
        this.scanner           = scanner
        this.empresaController = empresaController
        this.vagaController    = vagaController
    }

    void exibir() {
        boolean voltar = false
        while (!voltar) {
            println ""
            println "--- MENU EMPRESAS ---"
            println "1.  Cadastrar empresa"
            println "2.  Listar todas"
            println "3.  Buscar por ID"
            println "4.  Atualizar empresa"
            println "5.  Remover empresa"
            println "6.  Publicar vaga"
            println "7.  Listar vagas"
            println "8.  Atualizar vaga"
            println "9.  Remover vaga"
            println "10. Ver candidatos compativeis"
            println "11. Curtir candidato"
            println "12. Ver matches da empresa"
            println "0.  Voltar"
            print "Opcao: "

            switch (scanner.nextLine().trim()) {
                case '1':  cadastrar(); break
                case '2':  listarTodas(); break
                case '3':  buscarPorId(); break
                case '4':  atualizar(); break
                case '5':  remover(); break
                case '6':  publicarVaga(); break
                case '7':  listarVagas(); break
                case '8':  atualizarVaga(); break
                case '9':  removerVaga(); break
                case '10': verCandidatosCompat();  break
                case '11': curtirCandidato(); break
                case '12': verMatchesEmpresa(); break
                case '0':  voltar = true; break
                default:   println "Opcao invalida."
            }
        }
    }

    private void cadastrar() {
        println "\n--- Nova Empresa ---"
        Empresa empresa = new Empresa()
        empresa.nome = ler("Nome: ")
        empresa.email = ler("Email: ")
        empresa.cnpj = ler("CNPJ (somente numeros): ")
        empresa.descricao = ler("Descricao (opcional): ")
        empresa.endereco  = lerEndereco()

        try { empresaController.cadastrar(empresa); println "Empresa cadastrada!" }
        catch (Exception ex) { println "Erro: ${ex.message}" }
    }

    private void listarTodas() {
        List<Empresa> lista = empresaController.listarTodas()
        if (lista.isEmpty()) { println "\nNenhuma empresa."; return }
        println "\n--- Empresas ---"; lista.each { println it }
    }

    private void buscarPorId() {
        try { println empresaController.buscarPorId(lerInt("ID: ")) }
        catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void atualizar() {
        int id = lerInt("ID da empresa: ")
        try {
            Empresa empresa = empresaController.buscarPorId(id)
            println "Deixe em branco para manter."

            String nome = ler("Nome [${empresa.nome}]: ");
            if (nome){
                empresa.nome = nome
            }

            String email = ler("Email [${empresa.email}]: ");
            if (email){
                empresa.email = email
            }

            String cnpj = ler("CNPJ [${empresa.cnpj}]: ");
            if (cnpj) {
                empresa.cnpj = cnpj
            }

            String desc = ler("Descricao [${empresa.descricao ?: ''}]: ");
            if (desc) {
                empresa.descricao    = desc
            }

            String cidade = ler("Cidade [${empresa.endereco.cidade ?: ''}]: ");
            if (cidade){
                empresa.endereco.cidade = cidade
            }

            String est = ler("Estado [${empresa.endereco.estado ?: ''}]: ");
            if (est) {
                empresa.endereco.estado = est
            }

            empresaController.atualizar(empresa)
            println "Empresa atualizada."
        } catch (Exception ex) { println "Erro: ${ex.message}" }
    }

    private void remover() {
        int id = lerInt("ID: ")
        print "Confirmar? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            empresaController.remover(id); println "Removida."
        } else { println "Cancelado." }
    }

    private void publicarVaga() {
        println "\n--- Nova Vaga ---"
        Vaga vaga = new Vaga()
        vaga.empresaId = lerInt("ID da empresa: ")
        vaga.titulo = ler("Titulo: ")
        vaga.descricao = ler("Descricao: ")
        vaga.status = ler("Status (Aberta/Fechada/Pausada) [Aberta]: ") ?: 'Aberta'
        vaga.endereco  = lerEndereco()

        print "Competencias (separadas por virgula): "
        String comps = scanner.nextLine().trim()

        try {
            Vaga inserida = vagaController.publicar(vaga, comps)
            println "Vaga publicada! ID: ${inserida.id}"
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void listarVagas() {
        try {
            List<Vaga> vagas = vagaController.listarPorEmpresa(lerInt("ID da empresa: "))
            if (vagas.isEmpty()) { println "\nNenhuma vaga." }
            else { println "\n--- Vagas ---"; vagas.each { println it } }
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void atualizarVaga() {
        Vaga vaga = new Vaga(id: lerInt("ID da vaga: "), empresaId: lerInt("ID da empresa: "))
        vaga.titulo = ler("Novo titulo: ")
        vaga.descricao = ler("Nova descricao: ")
        vaga.status = ler("Status: ")
        try { vagaController.atualizar(vaga); println "Vaga atualizada." }
        catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void removerVaga() {
        int id = lerInt("ID da vaga: ")
        print "Confirmar? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            vagaController.remover(id); println "Vaga removida."
        } else { println "Cancelado." }
    }

    private void verCandidatosCompat() {
        try {
            List<Candidato> lista = vagaController.candidatosCompativeis(lerInt("ID da vaga: "))
            if (lista.isEmpty()) { println "\nNenhum candidato compativel." }
            else { println "\n--- Candidatos Compativeis ---"; lista.each { println it } }
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void curtirCandidato() {
        try {
            vagaController.curtirCandidato(lerInt("ID da vaga: "), lerInt("ID do candidato: "))
            println "Curtida registrada!"
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void verMatchesEmpresa() {
        try {
            List<Match> matches = vagaController.matchesDaEmpresa(lerInt("ID da empresa: "))
            if (matches.isEmpty()) { println "\nNenhum match." }
            else { println "\n--- Matches ---"; matches.each { println it } }
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
}
