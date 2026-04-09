package rb.aczg.model

class Empresa {
    int id
    String nome
    String cnpj
    String email
    String descricao
    String senhaHash
    Endereco endereco = new Endereco()
    List<Vaga> vagas = []

    @Override
    String toString() {
        String local = endereco ? "${endereco.cidade} - ${endereco.estado}" : 'N/A'
        String vagasTitulos = vagas ? vagas*.titulo.join(', ') : 'Nenhuma'
        """\
--- EMPRESA #${id} ---
  Nome        : ${nome}
  CNPJ        : ${cnpj}
  Email       : ${email}
  Localizacao : ${local}
  Descricao   : ${descricao ?: '-'}
  Vagas       : ${vagasTitulos}
"""
    }
}
