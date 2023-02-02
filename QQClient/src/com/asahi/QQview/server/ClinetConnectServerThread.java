package com.asahi.QQview.server;

import com.asahi.QQC.Message;
import com.asahi.QQC.MessageType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;

/**
 * @author Asahi
 * 保持和服务器建立连接，后台持续运行
 */
@SuppressWarnings({"all"})
public class ClinetConnectServerThread extends Thread {
    private Socket socket = null;
    private boolean loop = false;

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public ClinetConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public void run() {
        while (loop) {
            try {
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message mes = (Message) ois.readObject();
                if (mes.getMasType().equals(MessageType.MESSAGE_GET_ONLINE_USER)) {
                    String[] onlineusers = mes.getContent().split(" ");
                    System.out.println("========在线用户列表========");
                    for (int i = 0; i < onlineusers.length; i++) {
                        System.out.println("用户:" + onlineusers[i]);
                    }
                } else if (mes.getMasType().equals(MessageType.MESSAGE_EXIT)) {
                    ois.close();
                    socket.close();
                    setLoop(false);
                } else if (mes.getMasType().equals(MessageType.MESSAGE_PRIVATECHAT)) {
                    System.out.println("\n" + mes.getSender() + "对你说:" + mes.getContent() + "\t\t\t" + sdf.format(mes.getSendTime()));
                } else if (mes.getMasType().equals(MessageType.MESSAGE_PRIVATECHAT_FAIL)) {
                    System.out.println(mes.getContent());
                } else if (mes.getMasType().equals(MessageType.MESSAGE_COMM_MES)) {
                    System.out.println("\n" + mes.getSender() + "对大家说:" + mes.getContent());
                } else if (mes.getMasType().equals(MessageType.MESSAGE_FILE_FAIL)) {
                    System.out.println(mes.getContent());
                } else if (mes.getMasType().equals(MessageType.MESSAGE_NEWS)) {
                    System.out.println("\n" + mes.getSender() + "给" + mes.getGetter() + "推送了新闻:" + mes.getContent());
                } else if (mes.getMasType().equals(MessageType.MESSAGE_FILE)) {
                    System.out.println("\n" + mes.getSender() + "将" + mes.getSrc() + "文件发送到" + "你的电脑目录" + mes.getDest() + "下");
                    File file = new File(mes.getDest());
                    FileOutputStream fos = null;
                    int len = 0;
                    if (file.exists()) {
                        System.out.println("\n文件存在");
                    } else {
                        fos = new FileOutputStream(file);
                        fos.write(mes.getFile(), len, mes.getFilelenth());
                        fos.close();
                        System.out.println("\n保存成功");
                    }
                } else {
                    System.out.println("\n其他Message类型暂不处理");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
