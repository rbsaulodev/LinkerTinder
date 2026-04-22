package rb.aczg.interfaces.service

import rb.aczg.model.Candidato
import rb.aczg.model.Empresa
import rb.aczg.model.Match
import rb.aczg.model.Vaga

interface IEmpresaService {
    Empresa cadastrar(Empresa empresa)
    List<Empresa> listarTodas()
    Empresa buscarPorId(int id)
    Empresa atualizar(Empresa empresa)
    void remover(int id)
}
