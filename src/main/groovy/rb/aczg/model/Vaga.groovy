package rb.aczg.model

import java.time.LocalDateTime

class Vaga {
    int id
    int empresaId
    String nomeEmpresa
    String titulo
    String descricao
    String status
    LocalDateTime dataPublicacao
    int enderecoId
    Endereco endereco = new Endereco()
    List<Competencia> competencias = []

    @Override
    String toString() {
        String comps = competencias ? competencias*.nome.join(', ') : 'Nenhuma'
        String local = endereco ? "${endereco.cidade} - ${endereco.estado}" : 'N/A'
        """\
--- VAGA #${id} ---
  Titulo      : ${titulo}
  Empresa     : ${nomeEmpresa ?: '#' + empresaId}
  Status      : ${status ?: 'Aberta'}
  Local       : ${local}
  Descricao   : ${descricao ?: '-'}
  Competencias: ${comps}
"""
    }
}
