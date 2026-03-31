package rb.aczg.model

class Empresa {
    int id
    String nome
    String email
    String cnpj
    String pais
    String estado
    String cep
    String descricao
    List<Vaga> vagas = []

    @Override
    public String toString() {
        return "Empresa{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", pais='" + pais + '\'' +
                ", estado='" + estado + '\'' +
                ", cep='" + cep + '\'' +
                ", descricao='" + descricao + '\'' +
                ", vagas=" + vagas +
                '}';
    }
}
