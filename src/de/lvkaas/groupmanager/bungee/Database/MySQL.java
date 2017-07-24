package de.lvkaas.groupmanager.bungee.Database;

import de.lvkaas.groupmanager.bungee.GroupManager;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;

import java.security.acl.Group;
import java.sql.*;
import java.util.UUID;

/**
 * Created by Lukas on 22.07.2017.
 * Lieber Decompiler: du bist ein dummer Popo!
 * Ich bin ich, du bist du, ein dummer Esel, dass bist du
 */
public class MySQL {
    private String host, user, database, password;
    private Connection con;
    int port;

    public MySQL(String host, String user, String database, String password, int port) {
        this.host = host;
        this.user = user;
        this.database = database;
        this.password = password;
        this.port = port;
    }

    public Connection getCon() {
        return con;
    }

    public void connect() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
        } catch (SQLException e) {
            System.out.println("[]------[ MYSQL-ERROR ]------[]");
            System.out.println("Error whilest connecting to mysql-server");
            System.out.println("-> " + e.getMessage());
            BungeeCord.getInstance().stop("Error while starting MasterServer");
        }
    }

    public void disconnect() {
        if(!isConnected()) return;
        try {
            con.close();
        } catch (SQLException e) {
            System.out.println("[]------[ MYSQL-ERROR ]------[]");
            System.out.println("Error whilest closing connection to mysql-server");
            System.out.println("-> " + e.getMessage());
        }

    }

    public boolean isConnected() {
        try {
            if(con == null || con.isClosed()) return false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void execute(final String query) {
        if (!isConnected()) return;
        ProxyServer.getInstance().getScheduler().runAsync(GroupManager.getInstance(),
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            con.createStatement().execute(query);
                        } catch (SQLException e) {
                            System.out.println("[]------[ MYSQL-ERROR ]------[]");
                            System.out.println("Error whilest performing execute.");
                            System.out.println("Command: " + query);
                            System.out.println("-> " + e.getMessage());
                        }
                    }
                }
        );
    }

    public void updateQuery(final String qry) {
        if (!isConnected()) return;
        ProxyServer.getInstance().getScheduler().runAsync(GroupManager.getInstance(),
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Statement st = con.createStatement();
                            st.executeUpdate(qry);
                        } catch (SQLException e) {
                            System.out.println("[]------[ MYSQL-ERROR ]------[]");
                            System.out.println("Error whilest performing update.");
                            System.out.println("Command: " + qry);
                            System.out.println("-> " + e.getMessage());
                        }
                    }
                });
    }


    public ResultSet executeQuery(final String qry) {
        if (!isConnected()) return null;
        try {
            Statement st = con.createStatement();
            return st.executeQuery(qry);
        } catch (SQLException e) {
            System.out.println("[]------[ MYSQL-ERROR ]------[]");
            System.out.println("Error whilest executing query.");
            System.out.println("Command: " + qry);
            System.out.println("-> " + e.getMessage());
        }
        return null;
    }

    public String getSpezificString(String table, String whatreturn, String bedingung, String value) {
        ResultSet rs = executeQuery("SELECT * FROM " + table + " WHERE " + bedingung + "='" + value + "'");
        String returner = null;

        try {
            if(rs.next()) {
                returner = rs.getString(whatreturn);
            }
        } catch (SQLException e) {
            return null;
        }

        return returner;
    }

    public String getSpezificString(String table, String whatreturn, String bedingung, UUID value) {
        ResultSet rs = executeQuery("SELECT * FROM " + table + " WHERE " + bedingung + "='" + value + "'");
        String returner = null;

        try {
            if(rs.next()) {
                returner = rs.getString(whatreturn);
            }
        } catch (SQLException e) {
            return null;
        }

        return returner;
    }

    public Integer getSpezificInteger(String table, String whatreturn, String bedingung, String value) {
        ResultSet rs = executeQuery("SELECT * FROM " + table + " WHERE " + bedingung + "='" + value + "'");
        Integer returner = null;

        try {
            if(rs.next()) {
                returner = rs.getInt(whatreturn);
            }
        } catch (SQLException e) {
            return null;
        }

        return returner;
    }

    public String getSpezificString(String table, String whatreturn, UUID uuid) {
        ResultSet rs = executeQuery("SELECT * FROM " + table + " WHERE uuid='" + uuid + "'");
        String returner = null;

        try {
            if(rs.next()) {
                returner = rs.getString(whatreturn);
            }
        } catch (SQLException e) {
            return null;
        }

        return returner;
    }



    public String getString(String table, String whatreturn) {
        ResultSet rs = executeQuery("SELECT * FROM " + table);
        String returner = null;

        try {
            if(rs.next()) {
                returner = rs.getString(whatreturn);
            }
        } catch (SQLException e) {
            return null;
        }

        return returner;
    }

}
