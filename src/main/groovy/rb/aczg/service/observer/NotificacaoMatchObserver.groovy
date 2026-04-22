package rb.aczg.service.observer

import rb.aczg.interfaces.observer.MatchObserver
import rb.aczg.model.Match

class NotificacaoMatchObserver implements MatchObserver {

    @Override
    void onMatch(Match match) {
        println "[NOTIF] Parabenizando candidato #${match.candidatoId}: voce fez match com a vaga #${match.vagaId}!"
        println "[NOTIF] Avisando empresa sobre novo match na vaga #${match.vagaId} com candidato #${match.candidatoId}."
    }
}
