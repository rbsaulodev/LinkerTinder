package rb.aczg.model

class Candidato {
    int id
    String nome
    String sobrenome
    String email
    String cpf
    int idade
    String estado
    String cep
    String descricao
    List<Competencia> competencias = []

    @Override
    public String toString() {
        return "Candidato{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", sobrenome='" + sobrenome + '\'' +
                ", email='" + email + '\'' +
                ", cpf='" + cpf + '\'' +
                ", idade=" + idade +
                ", estado='" + estado + '\'' +
                ", cep='" + cep + '\'' +
                ", descricao='" + descricao + '\'' +
                ", competencias=" + competencias +
                '}';
    }
}