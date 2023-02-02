package com.asahi.Server;

import com.asahi.QQC.Message;
import com.asahi.QQC.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Asahi
 * 服务器线程,与客户端保持连接
 */
@SuppressWarnings({"all"})

public class ServerThread extends Thread {
    private Socket socket = null;
    private String userId;//用户id
    private boolean loop = false;

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public Socket getSocket() {
        return socket;
    }

    public ServerThread(Socket socket, String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    @Override
    public void run() {//发送和接收消息
        while (loop) {
            try {
                System.out.print("\n服务端和客户端" + userId + "保持通讯");
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message mesOut = new Message();
                //ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                Message mesIn = (Message) ois.readObject();//向下转型
                //根据message来决定业务
                switch (mesIn.getMasType()) {
                    case MessageType.MESSAGE_COMM_MES:
                        System.out.println("用户:" + userId + "加入群聊");
                        String[] onlineId = ManageServerThread.GetOnlineUser().split(" ");
                        for (int i = 0; i < onlineId.length; i++) {
                            ServerThread serverThread = ManageServerThread.getServerThread(onlineId[i]);
                            if (!userId.equals(onlineId[i])) {
                                ObjectOutputStream oos1 = new ObjectOutputStream(serverThread.getSocket().getOutputStream());
                                mesOut = new Message();
                                mesOut = mesIn;
                                oos1.writeObject(mesOut);
                            }
                        }
                        break;
                    case MessageType.MESSAGE_GET_ONLINE_USER:
                        ObjectOutputStream oos2 = new ObjectOutputStream(socket.getOutputStream());
                        System.out.println("用户:" + userId + "请求拉去在线用户列表");
                        String OnlineUsers = ManageServerThread.GetOnlineUser();
                        mesOut = new Message();
                        mesOut.setMesType(MessageType.MESSAGE_GET_ONLINE_USER);
                        mesOut.setContent(OnlineUsers);
                        mesOut.setGetter(mesIn.getSender());
                        oos2.writeObject(mesOut);
                        break;
                    case MessageType.MESSAGE_EXIT:
                        ObjectOutputStream oos3 = new ObjectOutputStream(socket.getOutputStream());
                        System.out.println("用户:" + userId + "将要退出登录");
                        mesOut = new Message();
                        mesOut.setMesType(MessageType.MESSAGE_EXIT);
                        mesOut.setGetter(mesIn.getSender());
                        oos3.writeObject(mesOut);
                        ManageServerThread.signOut(userId);
                        oos3.close();
                        ois.close();
                        socket.close();
                        System.out.println("用户:" + userId + "退出登录成功");
                        break;
                    case MessageType.MESSAGE_PRIVATECHAT:
                        if (QQServer.chickUser(mesIn.getGetter())) {
                            System.out.println("用户:" + userId + "请求与" + mesIn.getGetter() + "私聊");
                            if (QQServer.chickOnline(mesIn.getGetter())) {
                                ObjectOutputStream oosGet = new ObjectOutputStream(ManageServerThread.getServerThread(mesIn.getGetter()).getSocket().getOutputStream());
                                mesOut = mesIn;
                                oosGet.writeObject(mesOut);
                            } else {
                                OfflineServer.addOfflineMessage(mesIn.getGetter(),mesIn);
                                ObjectOutputStream oos4 = new ObjectOutputStream(socket.getOutputStream());
                                mesOut = new Message();
                                mesOut.setMesType(MessageType.MESSAGE_PRIVATECHAT_FAIL);
                                mesOut.setContent("用户(" + mesIn.getGetter() + ")不在线,已存入离线系统");
                                oos4.writeObject(mesOut);
                            }
                        } else {
                            ObjectOutputStream oos4 = new ObjectOutputStream(socket.getOutputStream());
                            mesOut = new Message();
                            mesOut.setMesType(MessageType.MESSAGE_PRIVATECHAT_FAIL);
                            mesOut.setContent("用户(" + mesIn.getGetter() + ")不存在");
                            oos4.writeObject(mesOut);
                        }
                        break;
                    case MessageType.MESSAGE_FILE:
                        if (QQServer.chickUser(mesIn.getGetter())) {
                            System.out.println("用户:" + userId + "向" + mesIn.getGetter() + "发送文件");
                            if (QQServer.chickOnline(mesIn.getGetter())) {
                                ObjectOutputStream oosGet = new ObjectOutputStream(ManageServerThread.getServerThread(mesIn.getGetter()).getSocket().getOutputStream());
                                mesOut = new Message();
                                mesOut = mesIn;
                                oosGet.writeObject(mesOut);
                            } else {
                                OfflineServer.addOfflineMessage(mesIn.getGetter(),mesIn);
                                ObjectOutputStream oos5 = new ObjectOutputStream(socket.getOutputStream());
                                mesOut = new Message();
                                mesOut.setMesType(MessageType.MESSAGE_FILE_FAIL);
                                mesOut.setContent("用户(" + mesIn.getGetter() + ")不在线,已存入离线系统");
                                oos5.writeObject(mesOut);
                            }
                        } else {
                            ObjectOutputStream oos5 = new ObjectOutputStream(socket.getOutputStream());
                            mesOut = new Message();
                            mesOut.setMesType(MessageType.MESSAGE_FILE_FAIL);
                            mesOut.setContent("用户(" + mesIn.getGetter() + ")不存在");
                            oos5.writeObject(mesOut);
                        }
                        break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
