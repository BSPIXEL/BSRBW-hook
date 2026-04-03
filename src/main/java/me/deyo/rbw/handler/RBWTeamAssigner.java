package me.deyo.rbw.handler;

import com.andrei1058.bedwars.api.arena.IArena;
import com.andrei1058.bedwars.api.arena.team.ITeam;
import com.andrei1058.bedwars.api.arena.team.ITeamAssigner;
import com.andrei1058.bedwars.api.events.gameplay.TeamAssignEvent;
import me.deyo.rbw.service.game.GameSetupService;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Optional;

public class RBWTeamAssigner implements ITeamAssigner, Listener {

    @Override
    public void assignTeams(IArena arena) {
        GameSetupService.RankedGame rankedGame = GameSetupService.getArenaToGameMapping().get(arena.getArenaName());
        if (rankedGame == null) return;

        List<List<String>> teamNames = rankedGame.getTeamNames();
        if (teamNames.size() < 2) return;
        List<String> team1Names = teamNames.get(0);
        List<String> team2Names = teamNames.get(1);

        Optional<ITeam> redTeamOpt = findTeamByName(arena, "Red");
        Optional<ITeam> greenTeamOpt = findTeamByName(arena, "Green");
        if (redTeamOpt.isEmpty() || greenTeamOpt.isEmpty()) return;
        ITeam redTeam = redTeamOpt.get();
        ITeam greenTeam = greenTeamOpt.get();

        redTeam.getMembers().clear();
        greenTeam.getMembers().clear();

        assignPlayersToTeam(team1Names, redTeam, arena);
        assignPlayersToTeam(team2Names, greenTeam, arena);
    }

    @EventHandler
    public void onTeamAssign(TeamAssignEvent event) {
        event.setCancelled(true);
        assignTeams(event.getArena());
    }

    private Optional<ITeam> findTeamByName(IArena arena, String teamName) {
        return arena.getTeams().stream()
                .filter(team -> team.getName().equalsIgnoreCase(teamName))
                .findFirst();
    }

    private void assignPlayersToTeam(List<String> playerNames, ITeam targetTeam, IArena arena) {
        for (String playerName : playerNames) {
            Player player = Bukkit.getPlayerExact(playerName);
            if (player != null && arena.getPlayers().contains(player)) {
                targetTeam.addPlayers(player);
            }
        }
    }
}