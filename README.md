# Linketinder

**Autor:** Saulo Rodrigues Brilhante

Sistema de match entre candidatos e vagas, inspirado no LinkedIn com mecânica de curtidas mútuas (estilo Tinder).

---

## Design Patterns aplicados

### 1. Factory — `IConnectionFactory`

**Onde:** `interfaces/factory/IConnectionFactory.groovy`, `dao/factory/PostgresConnectionFactory.groovy`, `dao/factory/H2ConnectionFactory.groovy`

**Por quê:** Os DAOs dependiam diretamente de `ConexaoBD`, que estava acoplada ao driver do PostgreSQL. Com a Factory, a criação de conexões ficou isolada em implementações intercambiáveis. Trocar de PostgreSQL para H2 (ou qualquer outro banco) é uma linha no `MenuPrincipal` — nenhum DAO ou service precisa mudar.

```groovy
// produção
ConexaoBD.configurar(new PostgresConnectionFactory())

// testes de integração
ConexaoBD.configurar(new H2ConnectionFactory())
```

---

### 2. Singleton — `ConexaoBD`

**Onde:** `dao/ConexaoBD.groovy`

**Por quê:** A factory de conexões é um recurso compartilhado que deve ter uma única instância na aplicação. O Singleton garante isso com `synchronized` para segurança em ambientes multi-thread. O método `configurar()` permite substituir a factory antes do primeiro uso, mantendo o Singleton testável.

```groovy
static synchronized ConexaoBD instancia()         // obtém ou cria
static synchronized void configurar(factory)      // substitui (testes)
static synchronized void resetar()                // limpa entre testes
```

---

### 3. Observer — `MatchObserver`

**Onde:** `interfaces/observer/MatchObserver.groovy`, `service/observer/LogMatchObserver.groovy`, `service/observer/NotificacaoMatchObserver.groovy`

**Por quê:** O match mútuo é um **evento de domínio** — quando acontece, vários subsistemas precisam reagir (log, notificação, ranking futuro). Sem Observer, cada reação ficaria misturada dentro de `curtirVaga`/`curtirCandidato`, violando o princípio Aberto/Fechado. Com Observer, adicionar uma nova reação é criar uma nova classe que implementa `MatchObserver` e registrá-la no `MenuPrincipal`.

```groovy
// registrar
candidatoService.registrarObserver(new LogMatchObserver())
candidatoService.registrarObserver(new NotificacaoMatchObserver())

// disparo automático quando match ocorre
void curtirVaga(int candidatoId, int vagaId) {
    Match match = vagaDAO.gerarMatchSeAmbosCurtiram(candidatoId, vagaId)
    if (match) notificarObservers(match)  // todos os observers recebem
}
```

---

### 4. Extração de VagaService (SRP / Clean Code)

**Onde:** `interfaces/service/IVagaService.groovy`, `service/VagaService.groovy`

**Por quê:** `EmpresaService` acumulava responsabilidades de dois domínios: empresa e vaga. `Vaga` tem ciclo de vida próprio (publicar, curtir candidato, gerar match) e não pertence ao service de empresa. A extração aplica o **Princípio da Responsabilidade Única**, tornando cada classe menor, mais coesa e mais fácil de testar isoladamente.

| Antes | Depois |
|---|---|
| `EmpresaService` com 13 métodos | `EmpresaService` com 5 métodos (CRUD) |
| Tudo numa interface | `IEmpresaService` + `IVagaService` separados |
| `MenuEmpresa` com 1 serviço | `MenuEmpresa` com 2 serviços injetados |


