package de.lvkaas.groupmanager.bungee.Misc;

import de.lvkaas.groupmanager.bungee.GroupManager;
import net.md_5.bungee.BungeeCord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Lukas on 22.07.2017.
 * Lieber Decompiler: du bist ein dummer Popo!
 * Ich bin ich, du bist du, ein dummer Esel, dass bist du
 */
public class Group {

    String rankid, name, prefix, chatcolor;
    Integer ranking;
    ArrayList<String> permissions, flags;


    public Group(String rankid, String name, String prefix, String chatcolor, Integer ranking) {
        this.rankid = rankid;
        this.name = name;
        this.prefix = prefix;
        this.chatcolor = chatcolor;
        this.ranking = ranking;
        this.permissions = new ArrayList<>();
        this.flags = new ArrayList<>();
    }

    public void push() {
        GroupManager.getMysql().execute("INSERT INTO groups(groupid, name, prefix, chatcolor, ranking) VALUES ('" + getRankid() + "','" + getName() + "','" + getPrefix() + "','" + getChatcolor() + "'," + ranking + ")");
        BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aGroup §3" + getName() + " §asuccessfully pushed to database.");
        CloudSyncroniser.sendToCloudServers("GroupManager|Push|" + getRankid());
        GroupManager.getGroupHashMap().put(name, this);
    }

    public ArrayList<String> getFlags() {
        return flags;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public ArrayList<String> getMembers() {
        ArrayList<String> list = new ArrayList<>();

        ResultSet rs = GroupManager.getMysql().executeQuery("SELECT name FROM users WHERE rank='" + rankid + "'");
        try {
            while (rs.next()) {
                list.add(rs.getString("name"));
            }
        } catch(SQLException ex ){
            BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§cError while loading members from database.");
            ex.printStackTrace();
        }

        return list;
    }

    public void addPlayer(UUID uuid) {
        GroupManager.getMysql().execute("UPDATE users SET rank='" + getRankid() + "' WHERE uuid='" + uuid + "'");
        BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aPlayer §3" + uuid + " §asuccessfully Rank changed to " + getName());
        CloudSyncroniser.sendToCloudServers("GroupManager|Change|Player|" + uuid + "|" + getRankid());
    }

    public void delete() {
        GroupManager.getMysql().execute("DELETE FROM groups WHERE groupid='" + getRankid() + "'");
        GroupManager.getMysql().execute("UPDATE users SET rank='" + GroupManager.getGroupHashMap().get("User").getRankid() + "' WHERE rank='" + getRankid() + "'");
        GroupManager.getMysql().execute("DELETE FROM perms WHERE groupid='" + rankid + "'");
        GroupManager.getMysql().execute("DELETE FROM flags WHERE groupid='" + rankid + "'");
        CloudSyncroniser.sendToCloudServers("GroupManager|Delete|" + getRankid());
        BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aGroup §3" + getName() + " §asuccessfully removed from database.");
        BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aPermissions of §3" + getName() + " §asuccessfully removed from database.");
        BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aFlags of §3" + getName() + " §asuccessfully removed from database.");

        GroupManager.getGroupHashMap().remove(name);
    }

    public void setRankid(String rankid) {
        this.rankid = rankid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setChatcolor(String chatcolor) {
        this.chatcolor = chatcolor;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public Integer getRanking() {
        return ranking;
    }

    public String getRankid() {
        return rankid;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getChatcolor() {
        return chatcolor;
    }
}
