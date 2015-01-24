package com.airhacks.threading.messages.boundary;

import com.airhacks.porcupine.execution.boundary.Dedicated;
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
    ExecutorService light;

    @Inject
    @Dedicated
    ExecutorService heavy;

    @Inject
    MessageReceiver fetcher;

    public void getLightMessage(Consumer<String> messageConsumer) {
        CompletableFuture.supplyAsync(fetcher::receiveLightMessage, light).thenAccept(messageConsumer);
    }

    public void getHeavyMessage(Consumer<String> messageConsumer) {
        CompletableFuture.supplyAsync(fetcher::receiveHeavyMessage, heavy).thenAccept(messageConsumer);
    }

}
