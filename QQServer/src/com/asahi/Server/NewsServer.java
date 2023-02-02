package com.asahi.Server;

import com.asahi.QQC.Message;
import com.asahi.QQC.MessageType;
import com.asahi.QQC.User;
import com.asahi.utility.Utility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Asahi
 * 新闻推送
 */
@SuppressWarnings({"all"})
public class NewsServer extends Thread {
    @Override
    public void run() {
        while (true) {
            System.out.print("输入要推送的新闻:");
            String news = Utility.readString(100);
            SendNews(news);
        }
    }

    /**
     * 发送新闻
     * @param news
     */
    public void SendNews(String news) {
        Iterator<String> iterator = QQServer.userHashMap.keySet().iterator();
        while (iterator.hasNext()) {
            String onlineId = iterator.next();
            if (QQServer.userHashMap.get(onlineId).isLoad()) {
                try {
                    Message message = new Message();
                    message.setSender("ServerNews");
                    message.setContent(news);
                    message.setMesType(MessageType.MESSAGE_NEWS);
                    message.setSendTime(new Date());
                    message.setGetter(onlineId);
                    ObjectOutputStream oos = new ObjectOutputStream(ManageServerThread.getServerThread(onlineId).getSocket().getOutputStream());
                    oos.writeObject(message);
                    System.out.println(message.getSender() + "向" + onlineId + "推送成功");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Message message = new Message();
                message.setSender("ServerNews");
                message.setContent(news);
                message.setMesType(MessageType.MESSAGE_NEWS);
                message.setSendTime(new Date());
                message.setGetter(onlineId);
                OfflineServer.addOfflineMessage(message.getGetter(), message);
                System.out.println(message.getSender() + "向" + message.getGetter() + "留言成功");
            }
        }
    }
}
