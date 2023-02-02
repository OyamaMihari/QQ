package com.asahi.QQview.view;

import com.asahi.QQview.server.UserServer;
import com.asahi.QQview.utility.Utility;

/**
 * @author Asahi
 */
@SuppressWarnings({"all"})
public class secondaryMenu {
    private String ID;
    private String pwd;
    private UserServer userServer = new UserServer();
    public secondaryMenu(String ID, String pwd, UserServer userServer) {
        this.ID = ID;
        this.pwd = pwd;
        this.userServer = userServer;
    }

    private boolean loop = true;//控制显示
    private String key = "0";

    public boolean isLoop() {
        return loop;
    }

    public void view2(){
        while (loop){
            System.out.println("\n==========通讯系统二级菜单(用户" + ID + ")==========");
            System.out.println("\t\t 1 显示在线用户列表");
            System.out.println("\t\t 2 群发消息");
            System.out.println("\t\t 3 私聊消息");
            System.out.println("\t\t 4 发送文件");
            System.out.println("\t\t 9 退出登录");
            System.out.print("输入:");
            key = Utility.readString(1);

            switch (key){
                case "1":
                    userServer.getOnlineUser();
                    break;
                case "2":
                    groupChat groupChat = new groupChat(ID, userServer);
                    groupChat.view3();
                    break;
                case "3":
                    System.out.print("输入要私聊的对象:");
                    String privateUserId = Utility.readString(50);
                    privateChat privateChat = new privateChat(ID, privateUserId, userServer);
                    privateChat.view3();
                    break;
                case "4":
                    System.out.print("输入发送的对象:");
                    String getter = Utility.readString(10);
                    System.out.print("输入源文件的目录:");
                    String src = Utility.readString(50);
                    System.out.print("输入文件保存的目录:");
                    String dest = Utility.readString(50);
                    userServer.privatesendFile(ID,getter,src,dest);
                    break;
                case "9":
                    loop = false;
                    userServer.signOut();
                    System.out.println("==========用户(" + ID + ")退出登录=======");
                    break;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
