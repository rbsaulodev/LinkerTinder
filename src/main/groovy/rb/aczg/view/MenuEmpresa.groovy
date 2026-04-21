package rb.aczg.view

import rb.aczg.model.Candidato
import rb.aczg.model.Empresa
import rb.aczg.model.Endereco
import rb.aczg.model.Match
import rb.aczg.model.Vaga
import rb.aczg.interfaces.service.IEmpresaService

class MenuEmpresa {

    private final IEmpresaService service
    private final Scanner scanner

    MenuEmpresa(Scanner scanner, IEmpresaService service) {
        this.scanner = scanner
        this.service = service
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
                case '1':  cadastrar();           break
                case '2':  listarTodas();         break
                case '3':  buscarPorId();         break
                case '4':  atualizar();           break
                case '5':  remover();             break
                case '6':  publicarVaga();        break
                case '7':  listarVagas();         break
                case '8':  atualizarVaga();       break
                case '9':  removerVaga();         break
                case '10': verCandidatosCompat(); break
                case '11': curtirCandidato();     break
                case '12': verMatchesEmpresa();   break
                case '0':  voltar = true;         break
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

        e.endereco = new Endereco()
        e.endereco.cep = ler("CEP: ")
        e.endereco.logradouro  = ler("Logradouro: ")
        e.endereco.numero = ler("Numero: ")
        e.endereco.complemento = ler("Complemento (opcional): ")
        e.endereco.bairro = ler("Bairro: ")
        e.endereco.cidade = ler("Cidade: ")
        e.endereco.estado = ler("Estado (UF): ")
        e.endereco.pais = ler("Pais: ")

        try { service.cadastrar(e); println "Empresa cadastrada!" }
        catch (Exception ex) { println "Erro: ${ex.message}" }
    }

    private void listarTodas() {
        List<Empresa> lista = service.listarTodas()
        if (lista.isEmpty()) { println "\nNenhuma empresa."; return }
        println "\n--- Empresas ---"; lista.each { println it }
    }

    private void buscarPorId() {
        try { println service.buscarPorId(lerInt("ID: ")) }
        catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void atualizar() {
        int id = lerInt("ID da empresa: ")
        try {
            Empresa e = service.buscarPorId(id)
            println "Deixe em branco para manter."
            String nome  = ler("Nome [${e.nome}]: ");         if (nome)  e.nome = nome
            String email = ler("Email [${e.email}]: ");       if (email) e.email = email
            String cnpj  = ler("CNPJ [${e.cnpj}]: ");        if (cnpj)  e.cnpj = cnpj
            String desc  = ler("Descricao [${e.descricao ?: ''}]: "); if (desc) e.descricao = desc
            String cidade = ler("Cidade [${e.endereco.cidade ?: ''}]: "); if (cidade) e.endereco.cidade = cidade
            String est   = ler("Estado [${e.endereco.estado ?: ''}]: "); if (est) e.endereco.estado = est
            service.atualizar(e); println "Empresa atualizada."
        } catch (Exception ex) { println "Erro: ${ex.message}" }
    }

    private void remover() {
        int id = lerInt("ID: ")
        print "Confirmar? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            service.remover(id); println "Removida."
        } else { println "Cancelado." }
    }

    private void publicarVaga() {
        println "\n--- Nova Vaga ---"
        Vaga v   = new Vaga()
        v.empresaId = lerInt("ID da empresa: ")
        v.titulo    = ler("Titulo: ")
        v.descricao = ler("Descricao: ")
        v.status    = ler("Status (Aberta/Fechada/Pausada) [Aberta]: ")
        if (!v.status) v.status = 'Aberta'

        v.endereco             = new Endereco()
        v.endereco.cep         = ler("CEP da vaga: ")
        v.endereco.logradouro  = ler("Logradouro: ")
        v.endereco.numero      = ler("Numero: ")
        v.endereco.bairro      = ler("Bairro: ")
        v.endereco.cidade      = ler("Cidade: ")
        v.endereco.estado      = ler("Estado (UF): ")
        v.endereco.pais        = ler("Pais: ")

        print "Competencias (separadas por virgula): "
        String comps = scanner.nextLine().trim()

        try {
            Vaga inserida = service.publicarVaga(v)
            if (comps) {
                comps.split(',').each { nome ->
                    if (nome.trim()) service.adicionarCompetenciaVaga(inserida.id, nome.trim())
                }
            }
            println "Vaga publicada! ID: ${inserida.id}"
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void listarVagas() {
        try {
            List<Vaga> vagas = service.listarVagas(lerInt("ID da empresa: "))
            if (vagas.isEmpty()) println "\nNenhuma vaga."
            else { println "\n--- Vagas ---"; vagas.each { println it } }
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void atualizarVaga() {
        Vaga v = new Vaga(id: lerInt("ID da vaga: "), empresaId: lerInt("ID da empresa: "))
        v.titulo    = ler("Novo titulo: ")
        v.descricao = ler("Nova descricao: ")
        v.status    = ler("Status: ")
        try { service.atualizarVaga(v); println "Vaga atualizada." }
        catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void removerVaga() {
        int id = lerInt("ID da vaga: ")
        print "Confirmar? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            service.removerVaga(id); println "Vaga removida."
        } else { println "Cancelado." }
    }

    private void verCandidatosCompat() {
        try {
            List<Candidato> lista = service.verMatchesDeCandidatos(lerInt("ID da vaga: "))
            if (lista.isEmpty()) println "\nNenhum candidato compativel."
            else { println "\n--- Candidatos Compativeis ---"; lista.each { println it } }
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void curtirCandidato() {
        try {
            service.curtirCandidato(lerInt("ID da vaga: "), lerInt("ID do candidato: "))
            println "Curtida registrada!"
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void verMatchesEmpresa() {
        try {
            List<Match> matches = service.verMatchesDaEmpresa(lerInt("ID da empresa: "))
            if (matches.isEmpty()) println "\nNenhum match."
            else { println "\n--- Matches ---"; matches.each { println it } }
        } catch (Exception e) { println "Erro: ${e.message}" }
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
