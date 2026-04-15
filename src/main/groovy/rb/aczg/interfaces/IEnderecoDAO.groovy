package rb.aczg.interfaces

import rb.aczg.model.Endereco

interface IEnderecoDAO {
    Endereco inserir(Endereco endereco)
    Endereco buscarPorId(int id)
    Endereco buscarPorCep(String cep)
    boolean atualizar(Endereco endereco)
    List<Endereco> listarTodos()
}
