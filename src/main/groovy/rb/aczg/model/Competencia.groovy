package rb.aczg.model

class Competencia {
    int id
    String nome
    String nivel
    boolean obrigatorio

    @Override
    String toString() {
        "[${id}] ${nome}"
    }
}
