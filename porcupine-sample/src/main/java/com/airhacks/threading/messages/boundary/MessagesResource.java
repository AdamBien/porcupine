package com.airhacks.threading.messages.boundary;

import java.util.concurrent.TimeUnit;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;

/**
 *
 * @author airhacks.com
 */
@Stateless
@Path("messages")
public class MessagesResource {

    @Inject
    MessagesService mes;

    private final static int TIMEOUT_IN_SECONDS = 2;

    @GET
    public void getMessage(@Suspended AsyncResponse ar) {
        mes.getMessage(ar::resume);
        ar.setTimeoutHandler(this::onTimeout);
        ar.setTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
    }

    public void onTimeout(AsyncResponse ar) {
        Response response = Response.status(Response.Status.SERVICE_UNAVAILABLE).
                header("Retry-After", TIMEOUT_IN_SECONDS).
                header("x-message", "Server is overloaded").
                build();
        ar.resume(response);
    }

}
