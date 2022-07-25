package com.ctrip.framework.apollo.configservice.async;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单的servlet 3.0异步请求 实现模拟客户端聊天室收发消息
 */
@WebServlet(name = "asyServlet",urlPatterns = "/asyServlet.do",asyncSupported = true)
public class SimpleAsyServlet extends HttpServlet {
    private static final long serialVersionUID = -5921107422679220387L;
    //存储请求的所有客户端
    private static final Map<String,GetClient> clients = new ConcurrentHashMap<String,GetClient>();

    static {
        //启动一个线程，扫描，关闭所有超时的连接
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                long current = System.currentTimeMillis();
                for (Map.Entry<String, GetClient> entry : clients.entrySet()) {
                    GetClient client = entry.getValue();
                    if (client == null)
                        return;
                    if (current - client.time > 20 * 1000) {
                        client.complete();
                        clients.remove(entry.getKey());
                    }
                }
            }
        }, 10, 5000);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if("get".equals(action)){//拉数据
            final AsyncContext asyncContext = req.startAsync();
            asyncContext.setTimeout(0L);
            String uid = req.getParameter("uid");
            clients.put(uid,new GetClient(asyncContext,uid));
        }else if("set".equals(action)){//设置数据
            String msg = req.getParameter("msg");
            sendMsg(msg);
            resp.getWriter().print("set success");
        }
    }

    //消息发送给所有客户端
    private void sendMsg(String msg) {
        Iterator<String> users = clients.keySet().iterator();
        while (users.hasNext()){
            GetClient cl = clients.get(users.next());
            if(cl != null) cl.response(msg);
        }
    }

}
