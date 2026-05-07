package rb.aczg.servlet

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import rb.aczg.dao.*

import java.time.LocalDate
import java.time.LocalDateTime

abstract class BaseServlet extends HttpServlet {

    protected CandidatoDAO candidatoDAO
    protected EmpresaDAO empresaDAO
    protected VagaDAO vagaDAO
    protected CompetenciaDAO competenciaDAO
    protected EnderecoDAO enderecoDAO

    @Override
    void init() {
        ConexaoBD db = ConexaoBD.instancia()
        competenciaDAO = new CompetenciaDAO(db)
        enderecoDAO = new EnderecoDAO(db)
        candidatoDAO = new CandidatoDAO(db, competenciaDAO)
        vagaDAO = new VagaDAO(db, competenciaDAO)
        empresaDAO = new EmpresaDAO(db, vagaDAO)
    }

    protected Map parseBody(HttpServletRequest req) {
        String body = req.reader.text?.trim()
        body ? (new JsonSlurper().parseText(body) as Map) : [:]
    }

    protected int extractId(HttpServletRequest req) {
        String last = req.requestURI.split('/').last()
        try { last.toInteger() } catch (NumberFormatException ignored) { -1 }
    }

    protected void ok(HttpServletResponse resp, Object obj) {
        writeJson(resp, 200, obj)
    }
    protected void created(HttpServletResponse resp, Object obj){
        writeJson(resp, 201, obj)
    }
    protected void noContent(HttpServletResponse resp) {
        resp.status = 204
    }
    protected void badRequest(HttpServletResponse resp, String msg) {
        writeJson(resp, 400, [erro: msg])
    }
    protected void notFound(HttpServletResponse resp, String msg) {
        writeJson(resp, 404, [erro: msg])
    }
    protected void serverError(HttpServletResponse resp, String msg) {
        writeJson(resp, 500, [erro: msg])
    }

    private void writeJson(HttpServletResponse resp, int status, Object obj) {
        resp.status = status
        resp.contentType = 'application/json'
        resp.characterEncoding = 'UTF-8'
        resp.writer.print(new JsonBuilder(toSerializable(obj)).toString())
        resp.writer.flush()
    }

    protected static Object toSerializable(Object obj) {
        switch (obj) {
            case List:
                return (obj as List).collect { toSerializable(it) }
            case Map:
                return (obj as Map).collectEntries { k, v -> [k, toSerializable(v)] }
            case { it?.class?.name?.startsWith('rb.aczg.model') }:
                return obj.properties
                        .findAll { k, v -> k != 'class' }
                        .collectEntries { k, v -> [k, toSerializable(v)] }
            case LocalDate:
                return obj.toString()
            case LocalDateTime:
                return obj.toString()
            default:
                return obj
        }
    }
}
