package rb.aczg.interfaces.observer

import rb.aczg.model.Match

interface MatchObserver {
    void onMatch(Match match)
}
