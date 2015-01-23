package com.airhacks.threading.messages.boundary;

import com.airhacks.porcupine.execution.control.Dedicated;
import com.airhacks.threading.messages.control.MessageReceiver;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 *
 * @author airhacks.com
 */
@Stateless
public class MessagesService {

    @Inject
    @Dedicated
    ExecutorService es;

    @Inject
    MessageReceiver fetcher;

    public void getMessage(Consumer<String> messageConsumer) {
        CompletableFuture.supplyAsync(fetcher::receiveMessage, es).thenAccept(messageConsumer);
    }

}
