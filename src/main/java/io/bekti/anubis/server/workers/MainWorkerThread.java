package io.bekti.anubis.server.workers;

import io.bekti.anubis.server.types.InboundMessage;
import io.bekti.anubis.server.types.OutboundMessage;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainWorkerThread extends Thread {

    private static Logger log = LoggerFactory.getLogger(MainWorkerThread.class);

    private BlockingQueue<InboundMessage> inboundQueue = new LinkedBlockingQueue<>();
    private BlockingQueue<OutboundMessage> outboundQueue = new LinkedBlockingQueue<>();

    private Session session;
    private AtomicBoolean running = new AtomicBoolean(false);

    private DispatcherThread dispatcherThread;
    private PublisherThread publisherThread;
    private ConsumerThread consumerThread;
    private PingThread pingThread;

    public MainWorkerThread(Session session) {
        this.session = session;
    }

    @Override
    public void run() {
        log.info("Starting threads...");
        running.set(true);

        dispatcherThread = new DispatcherThread(inboundQueue, session);
        dispatcherThread.start();

        publisherThread = new PublisherThread(outboundQueue);
        publisherThread.start();

        pingThread = new PingThread(session, this);
        pingThread.start();

        log.debug("Entering main client loop...");
    }

    public boolean isRunning() {
        return running.get();
    }

    public void shutdown() {
        if (running.get()) {
            running.set(false);

            try {
                if (dispatcherThread.isRunning()) {
                    dispatcherThread.shutdown();
                    dispatcherThread.join();
                }

                if (publisherThread.isRunning()) {
                    publisherThread.shutdown();
                    publisherThread.join();
                }

                if (consumerThread != null && consumerThread.isRunning()) {
                    consumerThread.shutdown();
                    consumerThread.join();
                }

                if (pingThread.isRunning()) {
                    pingThread.shutdown();
                    pingThread.join();
                }
            } catch (InterruptedException ignored) {

            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public void enqueueOutboundMessage(OutboundMessage outboundMessage) {
        try {
            outboundQueue.put(outboundMessage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void subscribe(List<String> topics, String groupId) {
        try {
            if (consumerThread != null && consumerThread.isRunning()) {
                consumerThread.shutdown();
                consumerThread.join();
            }

            consumerThread = new ConsumerThread(topics, groupId, inboundQueue);
            consumerThread.start();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void unsubscribe() {
        try {
            if (consumerThread != null && consumerThread.isRunning()) {
                consumerThread.shutdown();
                consumerThread.join();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void commit(String topic, int partitionId, long offset) {
        if (consumerThread != null && consumerThread.isRunning()) {
            consumerThread.commit(topic, partitionId, offset);
        }
    }

    public void seek(String topic, String offset) {
        if (consumerThread != null && consumerThread.isRunning()) {
            consumerThread.seek(topic, offset);
        }
    }

}