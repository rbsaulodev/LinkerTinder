package rb.aczg.model

class Competencia {
    int id
    String nome

    @Override
    String toString() {
        "[${id}] ${nome}"
    }
}
