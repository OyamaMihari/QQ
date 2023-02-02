package com.asahi.QQview.view;

import com.asahi.QQview.server.UserServer;
import com.asahi.QQview.utility.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Asahi
 */
@SuppressWarnings({"all"})
public class privateChat {
    private String ID;
    private String privateUserId;
    private UserServer userServer = new UserServer();
    private boolean loop = true;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public privateChat(String ID, String privateUserId, UserServer userServer) {
        this.ID = ID;
        this.privateUserId = privateUserId;
        this.userServer = userServer;
    }

    public void view3() {
        System.out.println("========正在与用户(" + privateUserId + ")聊天=========");
        System.out.println("\t\t 输入\"结束聊天\"以结束聊天");
        while (loop) {
            System.out.print("输入内容:");
            String content = Utility.readString(100);
            if (content.equals("结束聊天")) {
                break;
            }
            System.out.println("你对" + privateUserId + "说:" + content + "\t\t\t" + sdf.format(new Date()));
            userServer.privateChat(ID, privateUserId, content);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
