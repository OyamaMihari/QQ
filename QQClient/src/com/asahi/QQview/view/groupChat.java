package com.asahi.QQview.view;

import com.asahi.QQview.server.UserServer;
import com.asahi.QQview.utility.Utility;

/**
 * @author Asahi
 */
@SuppressWarnings({"all"})
public class groupChat {
    private String ID;
    private UserServer userServer = new UserServer();
    private boolean loop = true;

    public groupChat(String ID, UserServer userServer) {
        this.ID = ID;
        this.userServer = userServer;
    }

    public void view3(){
        System.out.println("=============在线群聊============");
        System.out.println("\t 输入\"退出群聊\"以退出群聊");
        while (loop){
            System.out.print("输入内容:");
            String content = Utility.readString(100);
            if (content.equals("退出群聊")) {
                break;
            }
            userServer.groupChat(ID,content);
            System.out.println("你对大家说:" + content);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
