package rb.aczg.controller

import rb.aczg.interfaces.controller.IEmpresaController
import rb.aczg.interfaces.service.IEmpresaService
import rb.aczg.model.Empresa

class EmpresaController implements IEmpresaController {

    private final IEmpresaService empresaService

    EmpresaController(IEmpresaService empresaService) {
        this.empresaService = empresaService
    }

    @Override
    Empresa cadastrar(Empresa empresa) {
        return empresaService.cadastrar(empresa)
    }

    @Override
    List<Empresa> listarTodas() {
        return empresaService.listarTodas()
    }

    @Override
    Empresa buscarPorId(int id) {
        return empresaService.buscarPorId(id)
    }

    @Override
    Empresa atualizar(Empresa empresa) {
        return empresaService.atualizar(empresa)
    }

    @Override
    void remover(int id) {
        empresaService.remover(id)
    }
}
