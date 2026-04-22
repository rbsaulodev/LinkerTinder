package rb.aczg.service

import rb.aczg.interfaces.dao.IEmpresaDAO
import rb.aczg.interfaces.dao.IEnderecoDAO
import rb.aczg.interfaces.service.IEmpresaService
import rb.aczg.model.Empresa
import rb.aczg.service.validation.EmpresaValidator

class EmpresaService implements IEmpresaService {

    private static final String EMPRESA_NAO_ENCONTRADA = 'Empresa #%d nao encontrada.'

    private final IEmpresaDAO    empresaDAO
    private final IEnderecoDAO   enderecoDAO
    private final EmpresaValidator validator

    EmpresaService(IEmpresaDAO empresaDAO, IEnderecoDAO enderecoDAO, EmpresaValidator validator) {
        this.empresaDAO  = empresaDAO
        this.enderecoDAO = enderecoDAO
        this.validator   = validator
    }

    @Override
    Empresa cadastrar(Empresa empresa) {
        validator.validar(empresa)
        salvarEnderecoSePresente(empresa)
        return empresaDAO.inserir(empresa)
    }

    @Override
    List<Empresa> listarTodas() {
        return empresaDAO.listarTodas()
    }

    @Override
    Empresa buscarPorId(int id) {
        Empresa empresa = empresaDAO.buscarPorId(id)
        if (!empresa) throw new RuntimeException(String.format(EMPRESA_NAO_ENCONTRADA, id))
        return empresa
    }

    @Override
    Empresa atualizar(Empresa empresa) {
        validator.validar(empresa)
        atualizarEnderecoSeExistente(empresa)
        empresaDAO.atualizar(empresa)
        return empresa
    }

    @Override
    void remover(int id) {
        empresaDAO.deletar(id)
    }

    private void salvarEnderecoSePresente(Empresa empresa) {
        if (empresa.endereco) {
            empresa.endereco = enderecoDAO.inserir(empresa.endereco)
        }
    }

    private void atualizarEnderecoSeExistente(Empresa empresa) {
        if (empresa.endereco?.id) {
            enderecoDAO.atualizar(empresa.endereco)
        }
    }
}
