package rb.aczg.view

import rb.aczg.model.Candidato
import rb.aczg.model.Empresa
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
            println """
MENU — EMPRESAS 
                      
1. Cadastrar empresa                
2. Listar todas as empresas         
3. Buscar empresa por ID            
4. Atualizar empresa                
5. Remover empresa        
                    
── Gestão de Vagas ──               
6. Publicar vaga                    
7. Listar vagas da empresa          
8. Atualizar vaga                   
9. Remover vaga                     
10. Ver candidatos compatíveis      
0. Voltar                           
"""
            print "Opção: "
            String opcao = scanner.nextLine().trim()

            switch (opcao) {
                case '1':  cadastrar();         break
                case '2':  listarTodas();       break
                case '3':  buscarPorId();       break
                case '4':  atualizar();         break
                case '5':  remover();           break
                case '6':  publicarVaga();      break
                case '7':  listarVagas();       break
                case '8':  atualizarVaga();     break
                case '9':  removerVaga();       break
                case '10': verMatches();        break
                case '0':  voltar = true;       break
                default:   println "Opção inválida."
            }
        }
    }

    private void cadastrar() {
        println "\n─── Nova Empresa ───"
        Empresa e = new Empresa()
        e.nome = ler("Nome da empresa: ")
        e.email = ler("Email: ")
        e.cnpj = ler("CNPJ: ")
        e.pais = ler("País: ")
        e.estado = ler("Estado (UF): ")
        e.cep = ler("CEP: ")
        e.descricao = ler("Descrição (opcional): ")

        try {
            service.cadastrar(e)
        } catch (Exception ex) {
            println "Erro: ${ex.message}"
        }
    }

    private void listarTodas() {
        List<Empresa> lista = service.listarTodas()
        if (lista.isEmpty()) {
            println "\nℹ️  Nenhuma empresa cadastrada."
            return
        }
        println "\n─── Empresas ───"
        lista.each { println it }
    }

    private void buscarPorId() {
        int id = lerInt("ID da empresa: ")
        try {
            println service.buscarPorId(id)
        } catch (Exception e) {
            println "❌ ${e.message}"
        }
    }

    private void atualizar() {
        int id = lerInt("ID da empresa a atualizar: ")
        try {
            Empresa e = service.buscarPorId(id)
            println "Deixe em branco para manter o valor atual."
            String nome  = ler("Nome [${e.nome}]: ")
            if (nome){
                e.nome = nome
            }

            String email = ler("Email [${e.email}]: ")
            if (email){
                e.email = email
            }

            String cnpj  = ler("CNPJ [${e.cnpj}]: ")
            if (cnpj){
                e.cnpj = cnpj
            }

            String pais  = ler("País [${e.pais}]: ")
            if (pais){
                e.pais = pais
            }

            String est   = ler("Estado [${e.estado}]: ")
            if (est){
                e.estado = est
            }

            String cep   = ler("CEP [${e.cep}]: ")
            if (cep){
                e.cep = cep
            }

            String desc  = ler("Descrição [${e.descricao ?: ''}]: ")
            if (desc){
                e.descricao = desc
            }
            service.atualizar(e)
        } catch (Exception ex) {
            println "${ex.message}"
        }
    }

    private void remover() {
        int id = lerInt("ID da empresa a remover: ")
        print "Confirmar remoção? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            service.remover(id)
        } else {
            println "Operação cancelada."
        }
    }

    //VAGAS
    private void publicarVaga() {
        println "\n─── Nova Vaga ───"
        int empresaId = lerInt("ID da empresa: ")
        Vaga v = new Vaga()
        v.empresaId = empresaId
        v.titulo    = ler("Título da vaga: ")
        v.descricao = ler("Descrição: ")
        v.local     = ler("Local: ")
        String sal  = ler("Salário (deixe em branco para não informar): ")
        if (sal) v.salario = new BigDecimal(sal.replace(',', '.'))

        print "Competências necessárias (separadas por vírgula): "
        String comps = scanner.nextLine().trim()

        try {
            Vaga inserida = service.publicarVaga(v)
            if (comps) {
                comps.split(',').each { nome ->
                    if (nome.trim()) service.adicionarCompetenciaVaga(inserida.id, nome.trim())
                }
            }
        } catch (Exception e) {
            println "Erro: ${e.message}"
        }
    }

    private void listarVagas() {
        int empresaId = lerInt("ID da empresa: ")
        try {
            List<Vaga> vagas = service.listarVagas(empresaId)
            if (vagas.isEmpty()) {
                println "\nℹ️  Nenhuma vaga cadastrada para essa empresa."
            } else {
                println "\n─── Vagas ───"
                vagas.each { println it }
            }
        } catch (Exception e) {
            println "❌ ${e.message}"
        }
    }

    private void atualizarVaga() {
        int id = lerInt("ID da vaga a atualizar: ")
        int empresaId = lerInt("ID da empresa (para validação): ")
        println "Deixe em branco para manter o valor atual."
        Vaga v = new Vaga(id: id, empresaId: empresaId)
        v.titulo = ler("Título: ")
        v.descricao = ler("Descrição: ")
        v.local = ler("Local: ")
        String sal  = ler("Salário: ")
        if (sal) v.salario = new BigDecimal(sal.replace(',', '.'))
        try {
            service.atualizarVaga(v)
        } catch (Exception e) {
            println "❌ ${e.message}"
        }
    }

    private void removerVaga() {
        int id = lerInt("ID da vaga a remover: ")
        print "Confirmar remoção? (s/N): "
        if (scanner.nextLine().trim().equalsIgnoreCase('s')) {
            service.removerVaga(id)
        } else {
            println "Operação cancelada."
        }
    }

    private void verMatches() {
        int vagaId = lerInt("ID da vaga: ")
        try {
            List<Candidato> candidatos = service.verMatchesDeCandidatos(vagaId)
            if (candidatos.isEmpty()) {
                println "\nℹ️  Nenhum candidato compatível encontrado."
            } else {
                println "\n─── Candidatos compatíveis ───"
                candidatos.each { println it }
            }
        } catch (Exception e) {
            println "${e.message}"
        }
    }

    private String ler(String label) {
        print label
        return scanner.nextLine().trim()
    }

    private int lerInt(String label) {
        while (true) {
            print label
            String entrada = scanner.nextLine().trim()
            try {
                return entrada.toInteger()
            } catch (NumberFormatException ignored) {
                println "Digite um número válido."
            }
        }
    }
}