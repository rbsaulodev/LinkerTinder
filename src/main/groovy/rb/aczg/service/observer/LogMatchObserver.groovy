package rb.aczg.service.observer

import rb.aczg.interfaces.observer.MatchObserver
import rb.aczg.model.Match

class LogMatchObserver implements MatchObserver {

    @Override
    void onMatch(Match match) {
        println "[LOG] MATCH CONFIRMADO: Candidato #${match.candidatoId} <-> Vaga #${match.vagaId} | ID do match: ${match.id} | Em: ${match.matchedEm}"
    }
}
