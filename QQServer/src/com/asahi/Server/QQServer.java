package com.asahi.Server;

import com.asahi.QQC.Message;
import com.asahi.QQC.MessageType;
import com.asahi.QQC.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Asahi
 * 服务端
 */
@SuppressWarnings({"all"})
public class QQServer {
    private ServerSocket serverSocket = null;
    //ConcurrentHashMap 处理了多线程安全问题
    public static ConcurrentHashMap<String, User> userHashMap = new ConcurrentHashMap<>();

    static {//初始化userhashmap
        userHashMap.put("100", new User("100", "123456"));
        userHashMap.put("101", new User("101", "123456"));
        userHashMap.put("asahi", new User("asahi", "324233"));
        userHashMap.put("300", new User("300", "123456"));
    }

    public QQServer() {
        try {
            serverSocket = new ServerSocket(9999);
            System.out.println("服务器端在9999端口监听");
            NewsServer newsServer = new NewsServer();
            newsServer.start();
            while (true) {
                Socket accept = serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(accept.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(accept.getOutputStream());
                Message mesIn = (Message) ois.readObject();
                Message message = new Message();
                if (mesIn.getMasType().equals(MessageType.MESSAGE_REGISTERED)) {
                    System.out.print("\n正在注册");
                    message.setMesType(MessageType.MESSAGE_REGISTERED);
                    oos.writeObject(message);
                    User user = (User) ois.readObject();
                    if (!userHashMap.containsKey(user.getUserId())) {
                        userHashMap.put(user.getUserId(), user);
                        Message message1 = new Message();
                        message1.setMesType(MessageType.MESSAGE_REGISTERED_SUCCEED);
                        System.out.print("\n用户(" + user.getUserId() + " " + user.getPasswd() + ")注册成功");
                        oos.writeObject(message1);
                    } else {
                        Message message2 = new Message();
                        message2.setMesType(MessageType.MESSAGE_REGISTERED_FAIL);
                        System.out.print("\n用户(" + user.getUserId() + " " + user.getPasswd() + ")注册失败");
                        oos.writeObject(message2);
                    }
                } else if (mesIn.getMasType().equals(MessageType.MESSAGE_LOGIN)) {
                    message.setMesType(MessageType.MESSAGE_LOGIN);
                    oos.writeObject(message);
                    User user = (User) ois.readObject();
                    //验证
                    if (isCheck(user.getUserId(), user.getPasswd())) {//登录验证成功
                        System.out.print("\n验证成功(用户" + user.getUserId() + ")");
                        Message message1 = new Message();
                        message1.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                        oos.writeObject(message1);
                        //创建一个线程，与客户端保持通讯
                        ServerThread serverThread = new ServerThread(accept, user.getUserId());
                        serverThread.setLoop(true);
                        serverThread.start();
                        ManageServerThread.addServerThread(user.getUserId(), serverThread);
                        OfflineServer.getOfflineMessage(user.getUserId());
                    } else {//登录验证失败
                        System.out.print("\n验证失败(用户 id=" + user.getUserId() + " passwd=" + user.getPasswd() + ")");
                        Message message2 = new Message();
                        message2.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                        oos.writeObject(message2);
                        accept.close();
                        oos.close();
                        ois.close();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 验证是否可以登录
     * @param id
     * @param pwd
     * @return
     */
    private boolean isCheck(String id, String pwd) {//验证是否可以登录
        User user = userHashMap.get(id);
        if (user.isLoad()) {
            return false;
        }
        if (user == null) {
            return false;
        }
        if (!user.getPasswd().equals(pwd)) {
            return false;
        }
        user.setLoad(true);
        return true;
    }

    /**
     * 是否存在该用户
     * @param id
     * @return
     */
    public static boolean chickUser(String id) {
        if (userHashMap.containsKey(id)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断该用户是否在线
     * @param id
     * @return
     */
    public static boolean chickOnline(String id) {
        User user = userHashMap.get(id);
        if (user.isLoad()) {
            return true;
        } else {
            return false;
        }
    }
}
