package com.asahi.QQview.server;

import com.asahi.QQC.Message;
import com.asahi.QQC.MessageType;
import com.asahi.QQC.User;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

/**
 * @author Asahi
 * 检验是否登录成功并持续联系服务器
 */
@SuppressWarnings({"all"})
public class UserServer {
    private User user = new User();
    private Socket socket = null;
    private boolean ver = false;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public UserServer() {
    }

    public UserServer(User user, Socket socket) {
        this.user = user;
        this.socket = socket;
    }

    /**
     * 登录
     * @param id
     * @param pwd
     * @return
     */
    public boolean verifyUser(String id, String pwd) {//登录
        user.setUserId(id);
        user.setPasswd(pwd);
        try {
            socket = new Socket(InetAddress.getLocalHost(), 9999);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message mesOut = new Message();
            mesOut.setMesType(MessageType.MESSAGE_LOGIN);
            oos.writeObject(mesOut);
            Message mesIn = (Message) ois.readObject();
            if (mesIn.getMasType().equals(MessageType.MESSAGE_LOGIN)) {
                //发送user对象到服务器
                oos.writeObject(user);
                //接收服务器发送的对象
                //接收对象并向下转型
                Message mes = (Message) ois.readObject();
                if (mes.getMasType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)) {
                    //创建一个持续通信的线程
                    ClinetConnectServerThread ccst = new ClinetConnectServerThread(socket);
                    ccst.setLoop(true);
                    ccst.start();
                    user.setLoad(true);
                    ManageThread.addThread(id, ccst);
                } else {

                    socket.close();
                    oos.close();
                }
            } else {
                System.out.println("无法登录");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return user.isLoad();
    }

    /**
     * 获取在线用户列表
     */
    public void getOnlineUser() {//获取在线用户列表
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_USER);
        message.setSender(user.getUserId());
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageThread.getCCST(user.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 登出
     */
    public void signOut() {//登出
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_EXIT);
        message.setSender(user.getUserId());
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageThread.getCCST(user.getUserId()).getSocket().getOutputStream());
            oos.writeObject(message);
            ClinetConnectServerThread ccst = ManageThread.getCCST(user.getUserId());
            if (!ccst.isLoop()) {
                oos.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param id 注册账号id
     * @param pwd 注册账号密码
     */
    public void registeredAccount(String id, String pwd) {//注册账号
        user.setUserId(id);
        user.setPasswd(pwd);
        try {
            socket = new Socket(InetAddress.getLocalHost(), 9999);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message mesOut = new Message();
            mesOut.setMesType(MessageType.MESSAGE_REGISTERED);
            oos.writeObject(mesOut);
            Message mes = (Message) ois.readObject();
            if (mes.getMasType().equals(MessageType.MESSAGE_REGISTERED)) {
                //发送user对象到服务器
                oos.writeObject(user);
                //接收服务器发送的对象
                //接收对象并向下转型
                Message mesIn = (Message) ois.readObject();
                if (mesIn.getMasType().equals(MessageType.MESSAGE_REGISTERED_SUCCEED)) {
                    System.out.println("========用户(" + id + " " + pwd + ")注册成功=======");
                } else if (mesIn.getMasType().equals(MessageType.MESSAGE_REGISTERED_FAIL)){
                    System.out.println("========用户(" + id + " " + pwd + ")注册失败=======");
                }
                ois.close();
                oos.close();
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param id 发送者
     * @param privateId 接收者
     * @param content 发送内容
     */
    public void privateChat(String id,String privateId,String content){//私聊
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_PRIVATECHAT);
        message.setSender(id);
        message.setGetter(privateId);
        message.setContent(content);
        message.setSendTime(new Date());
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param id 发送者id
     * @param content 发送的内容
     */
    public void groupChat(String id,String content){//群发消息
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        message.setSender(id);
        message.setContent(content);
        message.setSendTime(new Date());
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param senderId 发送者id
     * @param getterId 接收者id
     * @param src 源文件路径
     * @param dest 将要存放在什么地方的路径
     */
    public void privatesendFile(String senderId,String getterId,String src,String dest){//发送文件
        Message mesFile = new Message();
        mesFile.setMesType(MessageType.MESSAGE_FILE);
        mesFile.setSender(senderId);
        mesFile.setGetter(getterId);
        mesFile.setDest(dest);
        mesFile.setSrc(src);
        mesFile.setFilelenth((int) new File(src).length());
        FileInputStream fis = null;
        byte[] readbyte = new byte[(int) new File(src).length()];
        try {
            fis = new FileInputStream(src);
            fis.read(readbyte);
            mesFile.setFile(readbyte);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("\n" + "你将" + src + "文件发送到" + getterId + "的电脑目录" + dest + "下");
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(mesFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
