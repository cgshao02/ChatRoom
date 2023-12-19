package com.cgshao.chatroom.ws;

import com.alibaba.fastjson.JSON;
import com.cgshao.chatroom.config.GetHttpSessionConfig;
import com.cgshao.chatroom.utils.MessageUtils;
import com.cgshao.chatroom.ws.pojo.Message;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 */
@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfig.class)
@Component//交给Spring管理
public class ChatEndpoint {

    private static final Map<String,Session> onlineUsers = new ConcurrentHashMap<>();

    private HttpSession httpSession;
    /**
     * 建立websocket连接后，被调用
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        //1，将session进行保存
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        String user = (String) this.httpSession.getAttribute("user");
        onlineUsers.put(user,session);
        //2、广播消息，需要将登录的所有的用户推送给所有的用户
        String message = MessageUtils.getMessage(true, null, getFriends());
        broadcastAllUsers(message);
    }

    public Set getFriends() {
        Set<String> set = onlineUsers.keySet();
        return set;
    }

    private void broadcastAllUsers(String message) {
        try {
            //遍历map集合
            Set<Map.Entry<String, Session>> entries = onlineUsers.entrySet();
            for (Map.Entry<String, Session> entry : entries) {
                //获取到所有用户对应的session对象
                Session session = entry.getValue();
                //发送消息
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            //记录日志
            System.out.println(e.getMessage());
        }

    }

    /**
     * 浏览器发送消息到服务端，该方法被调用
     *
     * 张三   ---》 李四
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {

        try {
            //将消息推送给指定的用户
            Message msg = JSON.parseObject(message, Message.class);

            //获取 消息接受方的用户名
            String toName = msg.getToName();
            String mess = msg.getMessage();
            // 获取消息接收方用户对象的session 对象
            Session session = onlineUsers.get(toName);
            String user = (String) this.httpSession.getAttribute("user");
            String msg1 = MessageUtils.getMessage(false, user, mess);
            session.getBasicRemote().sendText(msg1);

        } catch (Exception e){

        }

    }

    /**
     * 断开 websocket 连接时被调用
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        //1，从onlineUsers中剔除当前用户的session对象
        String user = (String) this.httpSession.getAttribute("user");
        onlineUsers.remove(user);
        //2，通知其他所有的用户，当前用户下线了
        String message = MessageUtils.getMessage(true, null, getFriends());
        broadcastAllUsers(message);
    }

}
