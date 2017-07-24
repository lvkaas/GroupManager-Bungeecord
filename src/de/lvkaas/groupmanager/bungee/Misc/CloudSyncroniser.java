package de.lvkaas.groupmanager.bungee.Misc;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Lukas on 22.07.2017.
 * Lieber Decompiler: du bist ein dummer Popo!
 * Ich bin ich, du bist du, ein dummer Esel, dass bist du
 */
public class CloudSyncroniser {

    public static void sendToCloudServers(String message) {
        for(ServerInfo x : BungeeCord.getInstance().getServers().values()) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(stream);
            try {
                out.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
            x.sendData("BungeeCord", stream.toByteArray());
        }
    }

}
