package rb.aczg.interfaces.controller

import rb.aczg.model.Empresa

interface IEmpresaController {
    Empresa cadastrar(Empresa empresa)
    List<Empresa> listarTodas()
    Empresa buscarPorId(int id)
    Empresa atualizar(Empresa empresa)
    void remover(int id)
}
