package ms.uk.eclipse.scoreboard;

import ms.uk.eclipse.PracticePlugin;
import ms.uk.eclipse.core.board.Board;
import ms.uk.eclipse.core.utils.message.CC;
import ms.uk.eclipse.entity.Profile;
import ms.uk.eclipse.managers.PlayerManager;
import ms.uk.eclipse.match.Match;
import ms.uk.eclipse.util.ProfileList;

public class PartyMatchScoreboard extends Scoreboard {
    PlayerManager playerManager = PracticePlugin.INSTANCE.getPlayerManager();

    public PartyMatchScoreboard(Profile p) {
        super(p);
    }

    @Override
    public void updateBoard(Board board) {
        Match match = p.getMatch();

        if (match == null) {
            return;
        }

        ProfileList team = match.getTeam(p);
        ProfileList opponents = new ProfileList(match.getParticipants());
        opponents.removeAll(team);

        board.updateLines(CC.BOARD_SEPARATOR, "Your Team Remaining: " + CC.SECONDARY + team.size(),
                "Their Team Remaining: " + CC.SECONDARY + opponents.size(), CC.BOARD_SEPARATOR);
    }
}
