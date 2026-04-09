package rb.aczg.model

import java.time.LocalDate

class Candidato {
    int id
    String nome
    String sobrenome
    String email
    String cpf
    LocalDate dataNasc
    String descricao
    String senhaHash
    Endereco endereco = new Endereco()
    List<Competencia> competencias = []

    @Override
    String toString() {
        String comps = competencias ? competencias*.nome.join(', ') : 'Nenhuma'
        String local = endereco ? "${endereco.cidade} - ${endereco.estado}" : 'N/A'
        """\
--- CANDIDATO #${id} ---
  Nome        : ${nome} ${sobrenome}
  Email       : ${email}
  CPF         : ${cpf}
  Nascimento  : ${dataNasc}
  Localizacao : ${local}
  Descricao   : ${descricao ?: '-'}
  Competencias: ${comps}
"""
    }
}
