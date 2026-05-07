# Linketinder – REST API

API RESTful do Linketinder implementada em **Groovy + Gradle + Apache Tomcat 10**, usando **jakarta.servlet** como lib Java para os endpoints — sem frameworks web (Spring, Micronaut, Grails, etc.).

---

## Índice

1. [Pré-requisitos](#pré-requisitos)
2. [Portas utilizadas](#portas-utilizadas)
3. [Configuração do banco de dados](#configuração-do-banco-de-dados)
4. [Como executar](#como-executar)
5. [Endpoints disponíveis](#endpoints-disponíveis)
6. [Exemplos de requisição](#exemplos-de-requisição-curl)
7. [Referências](#referências)

---

## Pré-requisitos

| Ferramenta | Instalação (Fedora) |
|---|---|
| Java 21 | `sudo dnf install java-21-openjdk` |
| Gradle | via wrapper `./gradlew` (já incluso no projeto) |
| Apache Tomcat 10 | `sudo dnf install tomcat tomcat-webapps` |
| PostgreSQL | `sudo dnf install postgresql postgresql-server` |

---

## Portas utilizadas

| Serviço | Porta  |
|---|--------|
| Tomcat (API) | `8080` |
| PostgreSQL local | `5432` |

---

### 1. Configurar as credenciais no projeto

Edita o arquivo `src/main/resources/database.properties`:

```properties
url=jdbc:postgresql://localhost:5432/linketinder
usuario=postgres
senha=sua_senha_aqui
```

---

## Como executar

### 1. Gerar o WAR

```bash
./gradlew clean war
```

O arquivo gerado fica em: `build/libs/linketinder.war`

### 2. Parar o Tomcat e limpar deploy antigo

```bash
sudo systemctl stop tomcat
sudo rm -rf /var/lib/tomcat/webapps/linketinder
sudo rm -f  /var/lib/tomcat/webapps/linketinder.war
```

### 3. Copiar o WAR novo e subir o Tomcat

```bash
sudo cp build/libs/linketinder.war /var/lib/tomcat/webapps/
sudo systemctl daemon-reload
sudo systemctl start tomcat
```

### 4. Verificar se subiu corretamente

```bash
sudo journalctl -u tomcat -f
```

Quando aparecer essa linha, está funcionando:
```
Deployment of web application archive [/var/lib/tomcat/webapps/linketinder.war] has finished
```

### 5. Testar no navegador ou Bruno/Insomnia/Postman

```
http://localhost:8080/linketinder/api/candidatos
```

---

## Endpoints disponíveis

### Candidatos

| Método | URI | Ação | Status |
|---|---|---|---|
| `GET` | `/api/candidatos` | Lista todos | 200 |
| `GET` | `/api/candidatos/{id}` | Busca por ID | 200 |
| `POST` | `/api/candidatos` | Cadastra novo | 201 |
| `PUT` | `/api/candidatos/{id}` | Atualiza | 200 |
| `DELETE` | `/api/candidatos/{id}` | Remove | 204 |

### Empresas

| Método | URI | Ação | Status |
|---|---|---|---|
| `GET` | `/api/empresas` | Lista todas | 200 |
| `GET` | `/api/empresas/{id}` | Busca por ID | 200 |
| `POST` | `/api/empresas` | Cadastra nova | 201 |
| `PUT` | `/api/empresas/{id}` | Atualiza | 200 |
| `DELETE` | `/api/empresas/{id}` | Remove | 204 |

### Vagas

| Método | URI | Ação | Status |
|---|---|---|---|
| `GET` | `/api/vagas` | Lista todas | 200 |
| `GET` | `/api/vagas?empresa={id}` | Filtra por empresa | 200 |
| `GET` | `/api/vagas/{id}` | Busca por ID | 200 |
| `POST` | `/api/vagas` | Insere nova | 201 |
| `PUT` | `/api/vagas/{id}` | Atualiza | 200 |
| `DELETE` | `/api/vagas/{id}` | Remove | 204 |

### Competências

| Método | URI | Ação | Status |
|---|---|---|---|
| `GET` | `/api/competencias` | Lista todas | 200 |
| `POST` | `/api/competencias` | Cadastra nova | 201 |

---

## Exemplos de requisição (JSON)
### Candidatos

**POST** `/api/candidatos`
```json
{
  "nome": "Sandubinha",
  "sobrenome": "Silva",
  "email": "sandubinha@email.com",
  "cpf": "12345678900",
  "dataNasc": "2000-05-15",
  "descricao": "Desenvolvedor Groovy apaixonado por tecnologia",
  "senhaHash": "123456",
  "endereco": {
    "cep": "01310-100",
    "logradouro": "Avenida Paulista",
    "numero": "1000",
    "complemento": "Apto 42",
    "bairro": "Bela Vista",
    "cidade": "São Paulo",
    "estado": "SP",
    "pais": "Brasil"
  },
  "competencias": [
    { "nome": "Groovy", "nivel": "Pleno" },
    { "nome": "PostgreSQL", "nivel": "Júnior" }
  ]
}
```

**PUT** `/api/candidatos/1`
```json
{
  "nome": "Sandubinha",
  "sobrenome": "Santos",
  "email": "sandubinha.novo@email.com",
  "cpf": "12345678900",
  "dataNasc": "2000-05-15",
  "descricao": "Descrição atualizada"
}
```

---

### Empresas

**POST** `/api/empresas`
```json
{
  "nome": "TechCorp Ltda",
  "cnpj": "12345678000190",
  "email": "rh@techcorp.com",
  "descricao": "Empresa de tecnologia inovadora",
  "senhaHash": "123456",
  "endereco": {
    "cep": "04538-133",
    "logradouro": "Rua Funchal",
    "numero": "418",
    "complemento": "Andar 5",
    "bairro": "Vila Olímpia",
    "cidade": "São Paulo",
    "estado": "SP",
    "pais": "Brasil"
  }
}
```

**PUT** `/api/empresas/1`
```json
{
  "nome": "TechCorp Atualizada",
  "cnpj": "12345678000190",
  "email": "contato@techcorp.com",
  "descricao": "Descrição atualizada da empresa"
}
```

---

### Vagas

**POST** `/api/vagas`
```json
{
  "empresaId": 1,
  "titulo": "Desenvolvedor Groovy Pleno",
  "descricao": "Vaga para desenvolvedor com experiência em Groovy e PostgreSQL",
  "status": "Aberta",
  "competencias": [
    { "nome": "Groovy", "obrigatorio": true },
    { "nome": "PostgreSQL", "obrigatorio": true },
    { "nome": "Docker", "obrigatorio": false }
  ]
}
```

**PUT** `/api/vagas/1`
```json
{
  "empresaId": 1,
  "titulo": "Desenvolvedor Groovy Sênior",
  "descricao": "Vaga atualizada para nível sênior",
  "status": "Aberta"
}
```

---

### Competências

**POST** `/api/competencias`
```json
{
  "nome": "Groovy"
}
```

---

### GETs e DELETEs (sem body)

| Método | URL |
|---|---|
| GET | `http://localhost:8080/linketinder/api/candidatos` |
| GET | `http://localhost:8080/linketinder/api/candidatos/1` |
| GET | `http://localhost:8080/linketinder/api/empresas` |
| GET | `http://localhost:8080/linketinder/api/empresas/1` |
| GET | `http://localhost:8080/linketinder/api/vagas` |
| GET | `http://localhost:8080/linketinder/api/vagas/1` |
| GET | `http://localhost:8080/linketinder/api/vagas?empresa=1` |
| GET | `http://localhost:8080/linketinder/api/competencias` |
| DELETE | `http://localhost:8080/linketinder/api/candidatos/1` |
| DELETE | `http://localhost:8080/linketinder/api/empresas/1` |
| DELETE | `http://localhost:8080/linketinder/api/vagas/1` |
---

## Referências

### YouTube — Servlets e REST sem framework

**Servlet & JSP Full Course — Telusko**
Cobre `HttpServlet`, `doGet`, `doPost` e configuração do Tomcat do zero.
🔗 https://www.youtube.com/watch?v=OuBUUkQfBYM
---

**Baeldung — @WebServlet annotation**
Referência completa sobre `@WebServlet`, `urlPatterns` e atributos da anotação.
🔗 https://www.baeldung.com/javaee-web-annotations

