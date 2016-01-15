package com.airhacks.threading.statistics.boundary;

import com.airhacks.porcupine.execution.entity.Rejection;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author airhacks.com
 */
@Singleton
@ServerEndpoint("/rejections")
public class RejectionsNotifier {

    private Session session;

    @OnOpen
    public void open(Session session) {
        this.session = session;
    }

    @OnClose
    public void close() {
        this.session = null;
    }

    public void onNewRejection(@Observes Rejection rejectedTask) {
        String msg = "Server overloaded, task: " + rejectedTask.getTaskClass() + " is rejected!";
        if (this.session != null && this.session.isOpen()) {
            this.session.getAsyncRemote().sendText(msg);
        }
    }

}
