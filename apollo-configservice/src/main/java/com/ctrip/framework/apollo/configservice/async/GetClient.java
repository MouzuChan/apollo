package com.ctrip.framework.apollo.configservice.async;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GetClient {
    public AsyncContext asyncContext;
    public String uid;
    public long time;

    public GetClient(AsyncContext asyncContext, String uid) {
        this.asyncContext = asyncContext;
        this.uid = uid;
        this.time = System.currentTimeMillis();
    }

    //关闭连接
    public void complete() {
        response("connect end");
        asyncContext.complete();
    }

    public static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //响应
    public void response(String message) {
        Map<String, Object> content = new HashMap<String, Object>();
        content.put("messages", message);
        try {
            HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
            PrintWriter writer = response.getWriter();
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            writer.println(format.format(new Date()) + ",receive:" + content + "<br/>");
            writer.flush();
        } catch (Exception se) {
        }
    }

}
