package de.lvkaas.groupmanager.bungee;

import de.lvkaas.groupmanager.bungee.Commands.GroupCommand;
import de.lvkaas.groupmanager.bungee.Database.MySQL;
import de.lvkaas.groupmanager.bungee.Events.BungeeCordEvents;
import de.lvkaas.groupmanager.bungee.Misc.Group;
import de.lvkaas.groupmanager.bungee.Misc.GroupAPI;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Lukas on 22.07.2017.
 * Lieber Decompiler: du bist ein dummer Popo!
 * Ich bin ich, du bist du, ein dummer Esel, dass bist du
 */
public class GroupManager extends Plugin {

    static String prefix = "§b§lGroups §8| §7";
    static String noperm = prefix + "§cDazu fehlt dir die Berechtigung.";

    static HashMap<String, Group> groupHashMap = new HashMap<>();
    static HashMap<UUID, Group> playergroups = new HashMap<>();

    static MySQL mysql = new MySQL("root2.lvkaas.de", "groups", "groups", "9252wcEKUqoLtVo4", 3306);

    static GroupManager instance;

    @Override
    public void onEnable() {
        System.out.println("\n   _____                                      \n" +
                "  / ____|                                     \n" +
                " | |  __   _ __    ___    _   _   _ __    ___ \n" +
                " | | |_ | | '__|  / _ \\  | | | | | '_ \\  / __|\n" +
                " | |__| | | |    | (_) | | |_| | | |_) | \\__ \\\n" +
                "  \\_____| |_|     \\___/   \\__,_| | .__/  |___/\n" +
                "                                 | |          \n" +
                "                                 |_|          ");

        instance = this;

        mysql.connect();
        mysql.execute("CREATE TABLE IF NOT EXISTS groups(groupid varchar(100), name varchar(100), prefix varchar(100), chatcolor varchar(100), ranking INT)");
        mysql.execute("CREATE TABLE IF NOT EXISTS users(uuid varchar(100), rank varchar(100), name varchar(100))");
        mysql.execute("CREATE TABLE IF NOT EXISTS perms(groupid varchar(100), permission varchar(100))");
        mysql.execute("CREATE TABLE IF NOT EXISTS flags(groupid varchar(100), flag varchar(100))");

        GroupAPI.loadGroupsFromDatabase();
        GroupAPI.loadPermissionsFromDatabase();
        GroupAPI.loadFlagsFromDatabase();

        if(getGroupHashMap().get("Admin") == null) {
            Group admin = new Group(UUID.randomUUID().toString().replace("-", ""), "Admin", "§4Admin §8| §7", "§4", 99999);
            admin.push();
            getGroupHashMap().put("Admin", admin);
        }

        if(getGroupHashMap().get("User") == null) {
            Group user = new Group(UUID.randomUUID().toString().replace("-", ""), "User", "§aUser §8| §7", "§a", 1);
            user.push();
            getGroupHashMap().put("User", user);
        }

        BungeeCord.getInstance().getPluginManager().registerListener(this, new BungeeCordEvents());
        BungeeCord.getInstance().getPluginManager().registerCommand(this, new GroupCommand("group"));


    }

    public static HashMap<UUID, Group> getPlayergroups() {
        return playergroups;
    }

    public static HashMap<String, Group> getGroupHashMap() {
        return groupHashMap;
    }

    public static String getPrefix() {
        return prefix;
    }

    public static String getNoperm() {
        return noperm;
    }

    public static MySQL getMysql() {
        return mysql;
    }

    public static GroupManager getInstance() {
        return instance;
    }
}
