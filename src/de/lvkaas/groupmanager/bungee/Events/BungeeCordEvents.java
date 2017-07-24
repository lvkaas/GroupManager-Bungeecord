package de.lvkaas.groupmanager.bungee.Events;

import de.lvkaas.groupmanager.bungee.GroupManager;
import de.lvkaas.groupmanager.bungee.Misc.GroupAPI;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Lukas on 22.07.2017.
 * Lieber Decompiler: du bist ein dummer Popo!
 * Ich bin ich, du bist du, ein dummer Esel, dass bist du
 */
public class BungeeCordEvents implements Listener {

    @EventHandler
    public void onLogin(LoginEvent e) {
        try {
            GroupAPI.loadMySQLGroup(e.getConnection().getUniqueId());
        } catch(NullPointerException ex) {
            ex.printStackTrace();
        }

        if(GroupAPI.getGroup(e.getConnection().getUniqueId()) == null) {
            GroupManager.getMysql().execute("INSERT INTO users(uuid, rank, name) VALUES ('" + e.getConnection().getUniqueId() + "','" + GroupManager.getGroupHashMap().get("User").getRankid() + "','" + e.getConnection().getName() + "')");
            GroupManager.getPlayergroups().put(e.getConnection().getUniqueId(), GroupManager.getGroupHashMap().get("User"));
            BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aDer Spieler §3" + e.getConnection().getName() + " §ahat von §3CONSOLE §aden Rang §3User §abekommen.");
        }

    }

    @EventHandler
    public void onPost(PostLoginEvent e) {
        for(String x : GroupAPI.getGroup(e.getPlayer().getUniqueId()).getPermissions()) {
            e.getPlayer().setPermission(x, true);
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent e) {
        GroupAPI.unloadMySQLgroup(e.getPlayer().getUniqueId());
    }


}
