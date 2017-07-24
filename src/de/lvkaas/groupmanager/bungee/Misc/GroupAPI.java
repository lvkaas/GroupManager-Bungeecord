package de.lvkaas.groupmanager.bungee.Misc;

import de.lvkaas.groupmanager.bungee.GroupManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by Lukas on 22.07.2017.
 * Lieber Decompiler: du bist ein dummer Popo!
 * Ich bin ich, du bist du, ein dummer Esel, dass bist du
 */
public class GroupAPI {

    public static void loadGroupsFromDatabase() {
        ResultSet rs = GroupManager.getMysql().executeQuery("SELECT * FROM groups");
        try {
            while (rs.next()) {
                Group g = new Group(rs.getString("groupid"), rs.getString("name"), rs.getString("prefix"), rs.getString("chatcolor"), rs.getInt("ranking"));
                GroupManager.getGroupHashMap().put(rs.getString("name"), g);
                BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aGroup §3" + g.getName() + " §aloaded");
            }
            BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aSuccessfully loaded groups from database");
        } catch(SQLException ex ) {
            ex.printStackTrace();
        }
    }

    public static void loadPermissionsFromDatabase() {
        ResultSet rs = GroupManager.getMysql().executeQuery("SELECT * FROM perms");
        try {
            while (rs.next()) {
                Group g = GroupManager.getGroupHashMap().get(GroupAPI.getNameByGroupID(rs.getString("groupid")));
                g.getPermissions().add(rs.getString("permission"));
            }
            BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aSuccessfully loaded permissions from database");
        } catch(SQLException ex ) {
            ex.printStackTrace();
        }
    }

    public static void loadFlagsFromDatabase() {
        ResultSet rs = GroupManager.getMysql().executeQuery("SELECT * FROM flags");
        try {
            while (rs.next()) {
                Group g = GroupManager.getGroupHashMap().get(GroupAPI.getNameByGroupID(rs.getString("groupid")));
                g.getFlags().add(rs.getString("flag"));
            }
            BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aSuccessfully loaded flags from database");
        } catch(SQLException ex ) {
            ex.printStackTrace();
        }
    }

    public static boolean isInAdminGroup(ProxiedPlayer p) {
        Group has = getGroup(p.getUniqueId());
        if(has.getRanking() >= 9000) {
            return true;
        }
        return false;
    }

    public static boolean hasGroupWithFlag(UUID uuid, String flag) {
        Group has = getGroup(uuid);
        if(has.getFlags().contains(flag)) {
            return true;
        }
        return false;
    }

    public static String getNameByGroupID(String name) {
        for(Group x : GroupManager.getGroupHashMap().values()) {
            if(x.getRankid().equalsIgnoreCase(name)) {
                return x.name;
            }
        }
        return null;
    }

    public static boolean hasMinimumRank(ProxiedPlayer p, String group) {
        Group has = GroupManager.getPlayergroups().get(p.getUniqueId());
        Group arg = GroupManager.getGroupHashMap().get(group);
        if(has.getRanking() >= arg.getRanking()) {
            return true;
        }
        return false;
    }

    public static void loadMySQLGroup(UUID uuid) throws NullPointerException{
        String string1 = getNameByGroupID(GroupManager.getMysql().getSpezificString("users", "rank", "uuid", uuid.toString()));
        Group g = GroupManager.getGroupHashMap().get(string1);
        GroupManager.getPlayergroups().put(uuid, g);
    }

    public static void unloadMySQLgroup(UUID uuid) {
        GroupManager.getPlayergroups().remove(uuid);
    }

    public static Group getGroup(UUID uuid) {
        return GroupManager.getPlayergroups().get(uuid);
    }

    public static boolean hasGroup(UUID uuid) {
        return getGroup(uuid) != null;
    }


}
