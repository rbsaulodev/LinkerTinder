package rb.aczg.model

class Vaga {
    int id
    int empresaId
    String nomeEmpresa
    String titulo
    String descricao
    String local
    BigDecimal salario
    List<Competencia> competencias = []

    @Override
    public String toString() {
        return "Vaga{" +
                "id=" + id +
                ", empresaId=" + empresaId +
                ", nomeEmpresa='" + nomeEmpresa + '\'' +
                ", titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", local='" + local + '\'' +
                ", salario=" + salario +
                ", competencias=" + competencias +
                '}';
    }
}
