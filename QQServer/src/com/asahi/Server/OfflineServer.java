package com.asahi.Server;

import com.asahi.QQC.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * @author Asahi
 * 离线留言/文件
 */
@SuppressWarnings({"all"})
public class OfflineServer {
    private static HashMap<String, Message[]> hp = new HashMap<>();

    /**
     * 将消息/文件保存到离线系统中
     * @param id
     * @param message
     */
    public static void addOfflineMessage(String id, Message message) {//离线系统
        if (hp.containsKey(id)) {
            Message[] messages = hp.get(id);
            Message[] messages1 = new Message[messages.length + 1];
            for (int i = 0; i < messages.length; i++) {
                messages1[i] = messages[i];
            }
            messages1[messages1.length - 1] = message;
            hp.put(id, messages1);
        } else {
            Message[] messages = new Message[1];
            messages[0] = message;
            hp.put(id, messages);
        }
    }

    /**
     * 获得离线系统中对应的留言/文件
     * @param id
     */
    public static void getOfflineMessage(String id) {
        if (hp.containsKey(id)) {
            Message[] messages = hp.get(id);
            ServerThread serverThread = ManageServerThread.getServerThread(id);
            try {
                for (int i = 0; i < messages.length; i++) {
                    ObjectOutputStream oos = new ObjectOutputStream(serverThread.getSocket().getOutputStream());
                    oos.writeObject(messages[i]);
                }
                hp.remove(id);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
