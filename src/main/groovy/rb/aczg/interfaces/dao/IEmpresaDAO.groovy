package rb.aczg.interfaces.dao

import rb.aczg.model.Empresa

interface IEmpresaDAO {
    Empresa inserir(Empresa empresa)
    List<Empresa> listarTodas()
    Empresa buscarPorId(int id)
    boolean atualizar(Empresa empresa)
    boolean deletar(int id)
}
