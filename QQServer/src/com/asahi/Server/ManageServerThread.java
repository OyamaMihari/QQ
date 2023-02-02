package com.asahi.Server;

import com.asahi.QQC.User;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Asahi
 * 管理服务器与用户保持连接的线程
 */
@SuppressWarnings({"all"})
public class ManageServerThread {
    private static ConcurrentHashMap<String,ServerThread> hp = new ConcurrentHashMap<>();
    public static void addServerThread(String id,ServerThread serverThread){
        hp.put(id,serverThread);
    }
    public static ServerThread getServerThread(String id){
        return hp.get(id);
    }
    //获取在线用户列表
    public static String GetOnlineUser(){
        Set<String> strings = hp.keySet();
        Iterator<String> iterator = strings.iterator();
        String OnlineUsers = "";
        while (iterator.hasNext()) {
              OnlineUsers += iterator.next().toString() + " ";
        }
        return OnlineUsers;
    }
    public static void signOut(String id){//登出
        ServerThread serverThread = hp.get(id);
        User user = QQServer.userHashMap.get(id);
        user.setLoad(false);
        serverThread.setLoop(false);
        hp.remove(id);
    }

    public static ConcurrentHashMap<String, ServerThread> getHp() {
        return hp;
    }
}
