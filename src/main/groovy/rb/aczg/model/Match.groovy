package rb.aczg.model

import java.time.LocalDateTime

class Match {
    int id
    int candidatoId
    String nomeCanditado
    int vagaId
    String tituloVaga
    String nomeEmpresa
    LocalDateTime matchedEm

    @Override
    String toString() {
        """\
--- MATCH #${id} ---
  Candidato : ${nomeCanditado ?: '#' + candidatoId}
  Vaga      : ${tituloVaga ?: '#' + vagaId}
  Empresa   : ${nomeEmpresa ?: '-'}
  Em        : ${matchedEm}
"""
    }
}
