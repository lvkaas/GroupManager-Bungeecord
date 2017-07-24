package de.lvkaas.groupmanager.bungee.Commands;

import de.lvkaas.groupmanager.bungee.GroupManager;
import de.lvkaas.groupmanager.bungee.Misc.CloudSyncroniser;
import de.lvkaas.groupmanager.bungee.Misc.Group;
import de.lvkaas.groupmanager.bungee.Misc.GroupAPI;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.UUID;

/**
 * Created by Lukas on 22.07.2017.
 * Lieber Decompiler: du bist ein dummer Popo!
 * Ich bin ich, du bist du, ein dummer Esel, dass bist du
 */
public class GroupCommand extends Command {

    public GroupCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {

        CommandSender p = commandSender;

        if(commandSender instanceof ProxiedPlayer) {
            if(!GroupAPI.isInAdminGroup((ProxiedPlayer)p)) {
                ((ProxiedPlayer) commandSender).sendMessage(GroupManager.getNoperm());
                return;
            }
        }

        if(args.length == 0) {
            p.sendMessage("§8================{ §bGroups §8}================");
            p.sendMessage("   ");
            p.sendMessage("  §8» §7/Group info <Gruppe> §e➟ §7Infos über die Gruppe");
            p.sendMessage("  §8» §7/Group list <Gruppe> §e➟ §7Spieler in der Gruppe");
            p.sendMessage("  §8» §7/Group show §e➟ §7Liste der Gruppen");
            p.sendMessage("  §8» §7/Group set <Spieler> <Gruppe> §e➟ §7Spieler in die Gruppe");
            p.sendMessage("  §8» §7/Group clear <Gruppe> §e➟ §7Clear der Gruppe");
            p.sendMessage("  §8» §7/Group option <Gruppe> <Option> <Value> §e➟ §7Änderung");
            p.sendMessage("  §8» §7/Group create <Gruppe> §e➟ §7Erstellt eine Gruppe");
            p.sendMessage("  §8» §7/Group delete <Gruppe> §e➟ §7Löscht eine Gruppe");
            p.sendMessage("  §8» §7/Group perm <Gruppe> <option> <Permission> §e➟ §7Permission");
            p.sendMessage("   ");
            p.sendMessage("§8================{ §bGroups §8}================");
            return;
        }

        try {

            if(args[0].equalsIgnoreCase("info")) {
                if(GroupManager.getGroupHashMap().get(args[1]) == null) {
                    p.sendMessage(GroupManager.getPrefix() + "§cDiesen Rang gibt es nicht.");
                    return;
                }

                Group target = GroupManager.getGroupHashMap().get(args[1]);

                p.sendMessage("§8================{ " + target.getChatcolor() + target.getName() + " §8}================");
                p.sendMessage("   ");
                p.sendMessage("  §8» §7Name §e➟ §7" + target.getName());
                p.sendMessage("  §8» §7RankID §e➟ §7" + target.getRankid());
                p.sendMessage("  §8» §7Ranking §e➟ §7" + target.getRanking());
                p.sendMessage("  §8» §7Prefix §e➟ §7" + target.getPrefix());
                p.sendMessage("  §8» §7Farbe §e➟ §7" + target.getChatcolor() + "█");
                p.sendMessage("  §8» §7Permissions §e➟ §7" + target.getPermissions().toString().replace("[", "").replace("]", ""));
                p.sendMessage("  §8» §7/Group flag <Gruppe> <option> <Flag> §e➟ §7Flags");
                p.sendMessage("   ");
                p.sendMessage("§8================{ " + target.getChatcolor() + target.getName() + " §8}================");
                return;
            }

            if(args[0].equalsIgnoreCase("list")) {
                if(GroupManager.getGroupHashMap().get(args[1]) == null) {
                    p.sendMessage(GroupManager.getPrefix() + "§cDiesen Rang gibt es nicht.");
                    return;
                }

                Group target = GroupManager.getGroupHashMap().get(args[1]);
                ArrayList<String> pull = target.getMembers();

                Collections.sort(pull, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });

                p.sendMessage(GroupManager.getPrefix() + "Spieler in der Gruppe§8(" + target.getChatcolor() + pull.size() + "§8)§7: " + target.getChatcolor() + pull.toString().replace("[", "").replace("]", ""));
                return;
            }

            if(args[0].equalsIgnoreCase("show")) {

                ArrayList<String> pull = new ArrayList<>();

                for(Group x : GroupManager.getGroupHashMap().values()) {
                    pull.add(x.getName());
                }

                Collections.sort(pull, new Comparator<String>() {
                    @Override
                    public int compare(String s1, String s2) {
                        return s1.compareToIgnoreCase(s2);
                    }
                });

                p.sendMessage(GroupManager.getPrefix() + "Gruppen im System§8(§b" + pull.size() + "§8)§7: §b" + pull.toString().replace("[", "").replace("]", ""));
                return;
            }

            if(args[0].equalsIgnoreCase("set")) {
                if(GroupManager.getGroupHashMap().get(args[2]) == null) {
                    p.sendMessage(GroupManager.getPrefix() + "§cDiesen Rang gibt es nicht.");
                    return;
                }

                if(GroupManager.getMysql().getSpezificString("users", "uuid", "name", args[1]) == null) {
                    p.sendMessage(GroupManager.getPrefix() + "§cDiesen Spieler gibt es nicht.");
                    return;
                }

                UUID uuid = UUID.fromString(GroupManager.getMysql().getSpezificString("users", "uuid", "name", args[1]));
                Group target = GroupManager.getGroupHashMap().get(args[2]);
                target.addPlayer(uuid);

                if(GroupManager.getPlayergroups().containsKey(uuid)) {
                    GroupManager.getPlayergroups().remove(uuid);
                    GroupManager.getPlayergroups().put(uuid, target);
                    BungeeCord.getInstance().getPlayer(uuid).sendMessage(GroupManager.getPrefix() + "Du bist jetzt in der Gruppe " + target.getChatcolor() + target.getName());
                }

                p.sendMessage(GroupManager.getPrefix() + "Du hast die Gruppe von §b§l" + args[1] + " §7zu §b" + target.getName() + " §7gesetzt.");
                return;
            }

            if(args[0].equalsIgnoreCase("clear")) {
                if(GroupManager.getGroupHashMap().get(args[1]) == null) {
                    p.sendMessage(GroupManager.getPrefix() + "§cDiesen Rang gibt es nicht.");
                    return;
                }

                Group target = GroupManager.getGroupHashMap().get(args[1]);
                GroupManager.getMysql().execute("UPDATE users SET rank='" + GroupManager.getGroupHashMap().get("User").getRankid() + "' WHERE rank='" + target.getRankid() + "'");
                CloudSyncroniser.sendToCloudServers("GroupManager|Clear");

                if(p instanceof ProxiedPlayer) {
                    BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aGroup §3" + target.getName() + " §awas cleared by §3" + p.getName());
                }
                p.sendMessage(GroupManager.getPrefix() + "Du hast die Gruppe §bgeleert§7.");
                return;
            }

            if(args[0].equalsIgnoreCase("option")) {
                if(GroupManager.getGroupHashMap().get(args[1]) == null) {
                    p.sendMessage(GroupManager.getPrefix() + "§cDiesen Rang gibt es nicht.");
                    return;
                }

                OptionType type = null;
                for(OptionType x : OptionType.values()) {
                    if(x.name().equalsIgnoreCase(args[2])) {
                        type = x;
                    }
                }

                if(type == null) {
                    p.sendMessage(GroupManager.getPrefix() + "§cDiese Option gibt es nicht.");
                    return;
                }

                Group target = GroupManager.getGroupHashMap().get(args[1]);
                String option = args[3].replace("&", "§").replace("_", " ");

                if(type == OptionType.CHATCOLOR) {
                    target.setChatcolor(option);
                    GroupManager.getMysql().execute("UPDATE groups SET chatcolor='" + option + "' WHERE groupid='" + target.getRankid() + "'");
                    CloudSyncroniser.sendToCloudServers("GroupManager|Setting|ChatColor|" + target.getRankid() + "|" + target.getChatcolor());
                }

                if(type == OptionType.PREFIX) {
                    target.setPrefix(option);
                    GroupManager.getMysql().execute("UPDATE groups SET prefix='" + option + "' WHERE groupid='" + target.getRankid() + "'");
                    CloudSyncroniser.sendToCloudServers("GroupManager|Setting|Prefix|" + target.getRankid() + "|" + target.getPrefix());
                }

                if(type == OptionType.RANKING) {
                    try {
                        target.setRanking(Integer.valueOf(option));
                    } catch(NumberFormatException ex) {
                        p.sendMessage(GroupManager.getPrefix() + "§cI bims keine Zahl vong Typ her :D ");
                        return;
                    }
                    GroupManager.getMysql().execute("UPDATE groups SET ranking=" + option + " WHERE groupid='" + target.getRankid() + "'");
                    CloudSyncroniser.sendToCloudServers("GroupManager|Setting|Ranking|" + target.getRankid() + "|" + target.getRanking());
                }

                if(p instanceof ProxiedPlayer) {
                    BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aGroup §3" + target.getName() + " §aoption §3" + type.name() + " §aset §3" + option);
                }

                p.sendMessage(GroupManager.getPrefix() + "Du hast die Option §b" + type.name() + " §7zu §b" + option + " §7gesetzt.");
                return;
            }

            if(args[0].equalsIgnoreCase("create")) {
                if(GroupManager.getGroupHashMap().get(args[1]) != null) {
                    p.sendMessage(GroupManager.getPrefix() + "§cDiesen Rang gibt es schon.");
                    return;
                }

                Group target = new Group(UUID.randomUUID().toString().replace("-", ""), args[1], "§f", "§f", 0);
                target.push();

                if(p instanceof ProxiedPlayer) {
                    BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aGroup §3" + target.getName() + " §awas created by §3" + p.getName());
                }
                p.sendMessage(GroupManager.getPrefix() + "Du hast die Gruppe §b" + target.getName() + " §7erstellt.");
                p.sendMessage(GroupManager.getPrefix() + "GroupID: §b" + target.getRankid());
                return;
            }

            if(args[0].equalsIgnoreCase("delete")) {
                if(GroupManager.getGroupHashMap().get(args[1]) == null) {
                    p.sendMessage(GroupManager.getPrefix() + "§cDiesen Rang gibt es nicht.");
                    return;
                }

                Group target = GroupManager.getGroupHashMap().get(args[1]);
                target.delete();

                if(p instanceof ProxiedPlayer) {
                    BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aGroup §3" + target.getName() + " §awas removed by §3" + p.getName());
                }
                p.sendMessage(GroupManager.getPrefix() + "Du hast die Gruppe §b" + target.getName() + " §7gelöscht.");
                return;
            }

            if(args[0].equalsIgnoreCase("perm")) {
                if(GroupManager.getGroupHashMap().get(args[1]) == null) {
                    p.sendMessage(GroupManager.getPrefix() + "§cDiesen Rang gibt es nicht.");
                    return;
                }

                OptionType type = null;
                for(OptionType x : OptionType.values()) {
                    if(x.name().equalsIgnoreCase(args[2])) {
                        type = x;
                    }
                }

                if(type == null) {
                    p.sendMessage(GroupManager.getPrefix() + "§cDiese Option gibt es nicht.");
                    return;
                }

                Group target = GroupManager.getGroupHashMap().get(args[1]);
                String option = args[3];

                if(type == OptionType.ADD) {
                    if(target.getPermissions().contains(option)) {
                        p.sendMessage(GroupManager.getPrefix() + "§cDiese Gruppe hat schon diese Permission");
                        return;
                    }
                    target.getPermissions().add(option);
                    GroupManager.getMysql().execute("INSERT INTO perms(groupid, permission) VALUES ('" + target.getRankid() + "','" + option + "')");
                    for(ProxiedPlayer x : BungeeCord.getInstance().getPlayers()) {
                        if(GroupAPI.getGroup(x.getUniqueId()) == target) {
                            x.setPermission(option, true);
                        }
                    }
                    p.sendMessage(GroupManager.getPrefix() + "Du hast die Permission §b" + option + " §7hinzugefügt.");
                }

                if(type == OptionType.REMOVE) {
                    if(!target.getPermissions().contains(option)) {
                        p.sendMessage(GroupManager.getPrefix() + "§cDiese Gruppe hat nicht diese Permission");
                        return;
                    }
                    target.getPermissions().remove(option);
                    GroupManager.getMysql().execute("DELETE FROM perms WHERE groupid='" + target.getRankid() + "' AND permission='" + option + "'");
                    for(ProxiedPlayer x : BungeeCord.getInstance().getPlayers()) {
                        if(GroupAPI.getGroup(x.getUniqueId()) == target) {
                            x.setPermission(option, false);
                        }
                    }
                    p.sendMessage(GroupManager.getPrefix() + "Du hast die Permission §b" + option + " §7entfernt.");
                }

                if(type == OptionType.CHECK) {
                    if(!target.getPermissions().contains(option)) {
                        p.sendMessage(GroupManager.getPrefix() + "Diese Gruppe hat die Permission §cnicht§7.");
                        return;
                    }
                    p.sendMessage(GroupManager.getPrefix() + "Diese Gruppe hat die Permission §aerhalten§7.");
                }

                if(p instanceof ProxiedPlayer) {
                    BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aGroup §3" + target.getName() + " §apermission §3" + type.name() + " §aat §3" + option);
                }

                return;
            }

            if(args[0].equalsIgnoreCase("flag")) {
                if(GroupManager.getGroupHashMap().get(args[1]) == null) {
                    p.sendMessage(GroupManager.getPrefix() + "§cDiesen Rang gibt es nicht.");
                    return;
                }

                OptionType type = null;
                for(OptionType x : OptionType.values()) {
                    if(x.name().equalsIgnoreCase(args[2])) {
                        type = x;
                    }
                }

                if(type == null) {
                    p.sendMessage(GroupManager.getPrefix() + "§cDiese Option gibt es nicht.");
                    return;
                }

                Group target = GroupManager.getGroupHashMap().get(args[1]);
                String option = args[3];

                if(type == OptionType.ADD) {
                    if(target.getFlags().contains(option)) {
                        p.sendMessage(GroupManager.getPrefix() + "§cDiese Gruppe hat schon diese Flag");
                        return;
                    }
                    target.getFlags().add(option);
                    GroupManager.getMysql().execute("INSERT INTO flags(groupid, flag) VALUES ('" + target.getRankid() + "','" + option + "')");
                    p.sendMessage(GroupManager.getPrefix() + "Du hast die Flag §b" + option + " §7hinzugefügt.");
                }

                if(type == OptionType.REMOVE) {
                    if(!target.getFlags().contains(option)) {
                        p.sendMessage(GroupManager.getPrefix() + "§cDiese Gruppe hat nicht diese Flag");
                        return;
                    }
                    target.getFlags().remove(option);
                    GroupManager.getMysql().execute("DELETE FROM flags WHERE groupid='" + target.getRankid() + "' AND flag='" + option + "'");
                    p.sendMessage(GroupManager.getPrefix() + "Du hast die Flag §b" + option + " §7entfernt.");
                }

                if(type == OptionType.CHECK) {
                    if(!target.getFlags().contains(option)) {
                        p.sendMessage(GroupManager.getPrefix() + "Diese Gruppe hat die Flag §cnicht§7.");
                        return;
                    }
                    p.sendMessage(GroupManager.getPrefix() + "Diese Gruppe hat die Flag §aerhalten§7.");
                }

                if(p instanceof ProxiedPlayer) {
                    BungeeCord.getInstance().getConsole().sendMessage(GroupManager.getPrefix() + "§aGroup §3" + target.getName() + " §aflag §3" + type.name() + " §aat §3" + option);
                }

                return;
            }


        } catch(ArrayIndexOutOfBoundsException ex) {
            p.sendMessage("§8================{ §bGroups §8}================");
            p.sendMessage("   ");
            p.sendMessage("  §8» §7/Group info <Gruppe> §e➟ §7Infos über die Gruppe");
            p.sendMessage("  §8» §7/Group list <Gruppe> §e➟ §7Spieler in der Gruppe");
            p.sendMessage("  §8» §7/Group show §e➟ §7Liste der Gruppen");
            p.sendMessage("  §8» §7/Group set <Spieler> <Gruppe> §e➟ §7Spieler in die Gruppe");
            p.sendMessage("  §8» §7/Group clear <Gruppe> §e➟ §7Clear der Gruppe");
            p.sendMessage("  §8» §7/Group option <Gruppe> <Option> <Value> §e➟ §7Änderung");
            p.sendMessage("  §8» §7/Group create <Gruppe> §e➟ §7Erstellt eine Gruppe");
            p.sendMessage("  §8» §7/Group delete <Gruppe> §e➟ §7Löscht eine Gruppe");
            p.sendMessage("  §8» §7/Group perm <Gruppe> <option> <Permission> §e➟ §7Permission");
            p.sendMessage("  §8» §7/Group flag <Gruppe> <option> <Flag> §e➟ §7Flags");
            p.sendMessage("   ");
            p.sendMessage("§8================{ §bGroups §8}================");
        }

    }

    public enum OptionType {
        PREFIX,
        CHATCOLOR,
        RANKING,
        ADD,
        REMOVE,
        CHECK;

    }

}
