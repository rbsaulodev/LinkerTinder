package rb.aczg.view

import rb.aczg.model.Candidato
import rb.aczg.model.Empresa
import rb.aczg.model.Endereco
import rb.aczg.model.Match
import rb.aczg.model.Vaga
import rb.aczg.interfaces.service.IEmpresaService
import rb.aczg.interfaces.service.IVagaService

class MenuEmpresa {

    private final IEmpresaService empresaService
    private final IVagaService    vagaService
    private final Scanner         scanner

    MenuEmpresa(Scanner scanner, IEmpresaService empresaService, IVagaService vagaService) {
        this.scanner        = scanner
        this.empresaService = empresaService
        this.vagaService    = vagaService
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
                case '10': verCandidatosCompat(); break
                case '11': curtirCandidato(); break
                case '12': verMatchesEmpresa(); break
                case '0':  voltar = true; break
                default:   println "Opcao invalida."
            }
        }
    }

    private void cadastrar() {
        println "\n--- Nova Empresa ---"
        Empresa e = new Empresa()
        e.nome = ler("Nome: ")
        e.email = ler("Email: ")
        e.cnpj = ler("CNPJ (somente numeros): ")
        e.descricao = ler("Descricao (opcional): ")
        e.endereco = lerEndereco()

        try { empresaService.cadastrar(e); println "Empresa cadastrada!" }
        catch (Exception ex) { println "Erro: ${ex.message}" }
    }

    private void listarTodas() {
        List<Empresa> lista = empresaService.listarTodas()
        if (lista.isEmpty()) { println "\nNenhuma empresa."; return }
        println "\n--- Empresas ---"; lista.each { println it }
    }

    private void buscarPorId() {
        try { println empresaService.buscarPorId(lerInt("ID: ")) }
        catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void atualizar() {
        int id = lerInt("ID da empresa: ")
        try {
            Empresa empresa = empresaService.buscarPorId(id)
            println "Deixe em branco para manter."

            String nome = ler("Nome [${empresa.nome}]: ");
            if (nome) {
                empresa.nome  = nome
            }

            String email = ler("Email [${empresa.email}]: ");
            if (email){
                empresa.email = email
            }

            String cnpj = ler("CNPJ [${empresa.cnpj}]: ");
            if (cnpj){
                empresa.cnpj  = cnpj
            }

            String desc = ler("Descricao [${empresa.descricao ?: ''}]: ");
            if (desc){
                empresa.descricao = desc
            }

            String cidade = ler("Cidade [${empresa.endereco.cidade ?: ''}]: ");
            if (cidade){
                empresa.endereco.cidade = cidade
            }

            String est = ler("Estado [${empresa.endereco.estado ?: ''}]: ");
            if (est){
                empresa.endereco.estado = est
            }
            empresaService.atualizar(e); println "Empresa atualizada."
        } catch (Exception ex) { println "Erro: ${ex.message}" }
    }

    private void remover() {
        int id = lerInt("ID: ")
        print "Confirmar? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            empresaService.remover(id); println "Removida."
        } else { println "Cancelado." }
    }

    private void publicarVaga() {
        println "\n--- Nova Vaga ---"
        Vaga vaga = new Vaga()
        vaga.empresaId = lerInt("ID da empresa: ")
        vaga.titulo = ler("Titulo: ")
        vaga.descricao = ler("Descricao: ")
        vaga.status = ler("Status (Aberta/Fechada/Pausada) [Aberta]: ") ?: 'Aberta'
        vaga.endereco = lerEndereco()

        print "Competencias (separadas por virgula): "
        String comps = scanner.nextLine().trim()

        try {
            Vaga inserida = vagaService.publicar(v)
            if (comps) {
                comps.split(',').each { nome ->
                    if (nome.trim()) vagaService.adicionarCompetencia(inserida.id, nome.trim())
                }
            }
            println "Vaga publicada! ID: ${inserida.id}"
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void listarVagas() {
        try {
            List<Vaga> vagas = vagaService.listarPorEmpresa(lerInt("ID da empresa: "))
            if (vagas.isEmpty()){
                println "\nNenhuma vaga."
            }
            else {
                println "\n--- Vagas ---";
                vagas.each {
                    println it
                }
            }
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void atualizarVaga() {
        Vaga v = new Vaga(id: lerInt("ID da vaga: "), empresaId: lerInt("ID da empresa: "))
        v.titulo = ler("Novo titulo: ")
        v.descricao = ler("Nova descricao: ")
        v.status = ler("Status: ")
        try {
            vagaService.atualizar(v); println "Vaga atualizada."
        }
        catch (Exception e) {
            println "Erro: ${e.message}"
        }
    }

    private void removerVaga() {
        int id = lerInt("ID da vaga: ")
        print "Confirmar? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            vagaService.remover(id); println "Vaga removida."
        } else {
            println "Cancelado."
        }
    }

    private void verCandidatosCompat() {
        try {
            List<Candidato> lista = vagaService.candidatosCompativeis(lerInt("ID da vaga: "))
            if (lista.isEmpty()) {
                println "\nNenhum candidato compativel."
            }
            else {
                println "\n--- Candidatos Compativeis ---";
                lista.each {
                    println it
                }
            }
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void curtirCandidato() {
        try {
            vagaService.curtirCandidato(lerInt("ID da vaga: "), lerInt("ID do candidato: "))
            println "Curtida registrada!"
        } catch (Exception e) {
            println "Erro: ${e.message}"
        }
    }

    private void verMatchesEmpresa() {
        try {
            List<Match> matches = vagaService.matchesDaEmpresa(lerInt("ID da empresa: "))
            if (matches.isEmpty()){
                println "\nNenhum match."
            }
            else {
                println "\n--- Matches ---";
                matches.each {
                    println it
                }
            }
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private Endereco lerEndereco() {
        Endereco end = new Endereco()
        end.cep = ler("CEP: ")
        end.logradouro = ler("Logradouro: ")
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
