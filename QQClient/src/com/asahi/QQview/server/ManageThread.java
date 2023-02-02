package com.asahi.QQview.server;

import java.util.HashMap;

/**
 * @author Asahi
 * 管理客户端的线程
 */
@SuppressWarnings({"all"})
public class ManageThread {
    private static HashMap<String,ClinetConnectServerThread> hp = new HashMap<>();
    public static void addThread(String id,ClinetConnectServerThread thread){
        hp.put(id,thread);
    }
    public static ClinetConnectServerThread getCCST(String id){
        return hp.get(id);
    }
}
