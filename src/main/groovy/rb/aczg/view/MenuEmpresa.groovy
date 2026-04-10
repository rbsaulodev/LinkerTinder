package rb.aczg.view

import rb.aczg.model.Candidato
import rb.aczg.model.Empresa
import rb.aczg.model.Endereco
import rb.aczg.model.Match
import rb.aczg.model.Vaga
import rb.aczg.service.EmpresaService

class MenuEmpresa {

    private final EmpresaService service = new EmpresaService()
    private final Scanner scanner

    MenuEmpresa(Scanner scanner) {
        this.scanner = scanner
    }

    void exibir() {
        boolean voltar = false
        while (!voltar) {
            println ""
            println "--- MENU EMPRESAS ---"
            println "1.  Cadastrar empresa"
            println "2.  Listar todas as empresas"
            println "3.  Buscar empresa por ID"
            println "4.  Atualizar empresa"
            println "5.  Remover empresa"
            println "--- GESTAO DE VAGAS ---"
            println "6.  Publicar vaga"
            println "7.  Listar vagas da empresa"
            println "8.  Atualizar vaga"
            println "9.  Remover vaga"
            println "--- INTERACOES ---"
            println "10. Ver candidatos compativeis por competencia"
            println "11. Curtir candidato para uma vaga"
            println "12. Ver todos os matches da empresa"
            println "0.  Voltar"
            print "Opcao: "

            String opcao = scanner.nextLine().trim()
            switch (opcao) {
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
                case '12': verMatchesEmpresa();   break
                case '0':  voltar = true; break
                default:   println "Opcao invalida."
            }
        }
    }

    private void cadastrar() {
        println "\n--- Nova Empresa ---"
        Empresa empresa = new Empresa()
        empresa.nome = ler("Nome da empresa: ")
        empresa.email = ler("Email: ")
        empresa.cnpj = ler("CNPJ (somente numeros): ")
        empresa.descricao = ler("Descricao (opcional): ")

        empresa.endereco = new Endereco()
        empresa.endereco.cep = ler("CEP (somente numeros): ")
        empresa.endereco.logradouro  = ler("Logradouro/Rua: ")
        empresa.endereco.numero = ler("Numero: ")
        empresa.endereco.complemento = ler("Complemento (opcional): ")
        empresa.endereco.bairro = ler("Bairro: ")
        empresa.endereco.cidade = ler("Cidade: ")
        empresa.endereco.estado = ler("Estado (UF): ")
        empresa.endereco.pais = ler("Pais: ")

        try {
            service.cadastrar(e)
            println "Empresa cadastrada com sucesso!"
        } catch (Exception ex) { println "Erro: ${ex.message}" }
    }

    private void listarTodas() {
        List<Empresa> lista = service.listarTodas()
        if (lista.isEmpty()) { println "\nNenhuma empresa cadastrada."; return }
        println "\n--- Empresas ---"
        lista.each { println it }
    }

    private void buscarPorId() {
        int id = lerInt("ID da empresa: ")
        try { println service.buscarPorId(id) }
        catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void atualizar() {
        int id = lerInt("ID da empresa a atualizar: ")
        try {
            Empresa empresa = service.buscarPorId(id)
            println "Deixe em branco para manter o valor atual."

            String nome  = ler("Nome [${empresa.nome}]: ")
            if (nome){
                empresa.nome = nome
            }

            String email = ler("Email [${empresa.email}]: ")
            if (email){
                empresa.email = email
            }

            String cnpj  = ler("CNPJ [${empresa.cnpj}]: ")
            if (cnpj){
                empresa.cnpj = cnpj
            }

            String desc  = ler("Descricao [${empresa.descricao ?: ''}]: ")
            if (desc){
                empresa.descricao = desc
            }

            String logr  = ler("Logradouro [${empresa.endereco.logradouro ?: ''}]: ")
            if (logr) {
                empresa.endereco.logradouro = logr
            }

            String cidade = ler("Cidade [${e.endereco.cidade ?: ''}]: ")
            if (cidade){
                empresa.endereco.cidade = cidade
            }

            String est = ler("Estado [${e.endereco.estado ?: ''}]: ")
            if (est){
                empresa.endereco.estado = est
            }

            service.atualizar(e)
            println "Empresa atualizada."
        } catch (Exception ex) { println "Erro: ${ex.message}" }
    }

    private void remover() {
        int id = lerInt("ID da empresa a remover: ")
        print "Confirmar remocao? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            service.remover(id)
            println "Empresa removida."
        } else { println "Operacao cancelada." }
    }

    private void publicarVaga() {
        println "\n--- Nova Vaga ---"
        int empresaId = lerInt("ID da empresa anunciante: ")
        Vaga vaga = new Vaga()
        vaga.empresaId = empresaId
        vaga.titulo = ler("Titulo da vaga: ")
        vaga.descricao = ler("Descricao: ")
        vaga.status = ler("Status (Aberta/Fechada/Pausada) [Aberta]: ")
        if (!vaga.status) vaga.status = 'Aberta'

        vaga.endereco = new Endereco()
        vaga.endereco.cep = ler("CEP da vaga (somente numeros): ")
        vaga.endereco.logradouro = ler("Logradouro: ")
        vaga.endereco.numero = ler("Numero: ")
        vaga.endereco.bairro = ler("Bairro: ")
        vaga.endereco.cidade = ler("Cidade: ")
        vaga.endereco.estado = ler("Estado (UF): ")
        vaga.endereco.pais = ler("Pais: ")

        print "Competencias necessarias (separadas por virgula): "
        String comps = scanner.nextLine().trim()

        try {
            Vaga inserida = service.publicarVaga(v)
            if (comps) {
                comps.split(',').each { nome ->
                    if (nome.trim()) service.adicionarCompetenciaVaga(inserida.id, nome.trim())
                }
            }
            println "Vaga publicada com sucesso! ID: ${inserida.id}"
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void listarVagas() {
        int empresaId = lerInt("ID da empresa: ")
        try {
            List<Vaga> vagas = service.listarVagas(empresaId)
            if (vagas.isEmpty()) { println "\nNenhuma vaga cadastrada para esta empresa." }
            else { println "\n--- Vagas da Empresa ---"; vagas.each { println it } }
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void atualizarVaga() {
        int id = lerInt("ID da vaga: ")
        int empresaId = lerInt("ID da empresa (validacao): ")
        Vaga vaga = new Vaga(id: id, empresaId: empresaId)
        vaga.titulo = ler("Novo titulo: ")
        vaga.descricao = ler("Nova descricao: ")
        vaga.status = ler("Status (Aberta/Fechada/Pausada): ")
        try {
            service.atualizarVaga(vaga)
            println "Vaga atualizada."
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void removerVaga() {
        int id = lerInt("ID da vaga a remover: ")
        print "Confirmar remocao? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            service.removerVaga(id)
            println "Vaga removida."
        } else { println "Operacao cancelada." }
    }

    private void verCandidatosCompat() {
        int vagaId = lerInt("ID da vaga: ")
        try {
            List<Candidato> candidatos = service.verMatchesDeCandidatos(vagaId)
            if (candidatos.isEmpty()) { println "\nNenhum candidato compativel." }
            else { println "\n--- Candidatos Compativeis ---"; candidatos.each { println it } }
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void curtirCandidato() {
        int vagaId = lerInt("ID da vaga: ")
        int candidatoId = lerInt("ID do candidato: ")
        try {
            service.curtirCandidato(vagaId, candidatoId)
            println "Curtida registrada!"
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private void verMatchesEmpresa() {
        int empresaId = lerInt("ID da empresa: ")
        try {
            List<Match> matches = service.verMatchesDaEmpresa(empresaId)
            if (matches.isEmpty()) { println "\nNenhum match encontrado." }
            else { println "\n--- Matches da Empresa ---"; matches.each { println it } }
        } catch (Exception e) { println "Erro: ${e.message}" }
    }

    private String ler(String label) {
        print label
        return scanner.nextLine().trim()
    }

    private int lerInt(String label) {
        while (true) {
            print label
            try { return scanner.nextLine().trim().toInteger() }
            catch (NumberFormatException ignored) { println "Digite um numero valido." }
        }
    }
}
