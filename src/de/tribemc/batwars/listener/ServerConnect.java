package de.tribemc.batwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import de.tribemc.batwars.main.BatWarsManager;
import de.tribemc.batwars.main.BatWarsManager.ServerStatus;
import de.tribemc.batwars.objects.BatData;
import de.tribemc.batwars.objects.BatLobbyData;
import de.tribemc.mglobby.objects.MGLobbyLoadCompleteEvent;
import de.tribemc.tribeessentials.eventlistener.tribe.SpielerJoinEvent;
import de.tribemc.tribeessentials.eventlistener.tribe.SpielerQuitEvent;

public class ServerConnect implements Listener {

	private BatWarsManager bwm;

	public ServerConnect(BatWarsManager bwm) {
		this.bwm = bwm;
	}

	@EventHandler
	public void onServerJoin(SpielerJoinEvent e) {
		if (bwm.getStatus().equals(ServerStatus.LOBBY)) {
			return;
		}
		if (bwm.getStatus().equals(ServerStatus.POSTGAME)
				|| bwm.getServer().equals(ServerStatus.RESTART)) {
			e.getSpieler().kickPlayer(
					"§cDer BatWars-Server befindet sich zurzeit im Restart!");
			return;
		}
		e.setJoinMessage(null);
		e.getSpieler().setExtra(bwm.getMap().getSpecData());
		this.bwm.specJoin(e.getSpieler());
	}

	@EventHandler
	public void onQuit(SpielerQuitEvent e) {
		e.setQuitMessage(null);
		if (bwm.getStatus().equals(ServerStatus.INGAME)
				&& e.getSpieler().getExtra() instanceof BatData)
			this.bwm.onPlayerLeave(e.getSpieler(), true);
		else if (bwm.getStatus().equals(ServerStatus.POSTGAME)) {
			if (bwm.getServer().getOnlineSpieler().size() <= 1)
				bwm.getShow().end();
		}
	}

	@EventHandler
	public void onLobbyFinish(MGLobbyLoadCompleteEvent e) {
		this.bwm.onLobbyComplete(new BatLobbyData(e.getMapVoteManager()
				.getWinner().getName()));
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onDemage(EntityDamageEvent e) {
		if (!bwm.getStatus().equals(ServerStatus.INGAME)) {
			e.setCancelled(true);
		}
	}
}
