package com.asahi.QQview.view;

import com.asahi.QQview.server.UserServer;
import com.asahi.QQview.utility.Utility;

/**
 * @author Asahi
 */
@SuppressWarnings({"all"})
public class index {
    private boolean loop = true;//控制显示
    private String key;
    private UserServer userServer = new UserServer();

    public static void main(String[] args) {
        new index().mainView();
        System.out.println("客户端退出...");
    }

    public void mainView() {
        while (loop) {
            System.out.println("==========欢迎登录通讯系统==========");
            System.out.println("\t\t 1 登录系统");
            System.out.println("\t\t 2 注册账号");
            System.out.println("\t\t 9 退出系统");
            System.out.print("输入:");
            key = Utility.readString(1);

            switch (key) {
                case "1":
                    System.out.print("用户ID:");
                    String userId = Utility.readString(50);
                    System.out.print("密码:");
                    String pwd = Utility.readString(50);
                    //要判断是否合法
                    if (userServer.verifyUser(userId,pwd)) {
                        System.out.print("=======欢迎用户(" + userId + ")=========");
                        secondaryMenu view = new secondaryMenu(userId, pwd, userServer);
                        view.view2();
                        //loop = view.isLoop();
                    } else {
                        System.out.println("======登录失败======");
                    }
                    break;
                case "2":
                    System.out.print("注册用户ID:");
                    String reId = Utility.readString(50);
                    System.out.print("注册密码:");
                    String repwd = Utility.readString(50);
                    userServer.registeredAccount(reId,repwd);
                    break;
                case "9":
                    loop = false;
                    System.exit(0);
                    break;
            }
        }
    }
}
