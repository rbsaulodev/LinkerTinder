package rb.aczg.model

class Endereco {
    int id
    String cep
    String logradouro
    String numero
    String complemento
    String bairro
    String cidade
    String estado
    String pais

    @Override
    String toString() {
        "${logradouro ?: ''}, ${numero ?: 's/n'} — ${bairro ?: ''}, ${cidade} - ${estado}, ${pais} (CEP: ${cep})"
    }
}
