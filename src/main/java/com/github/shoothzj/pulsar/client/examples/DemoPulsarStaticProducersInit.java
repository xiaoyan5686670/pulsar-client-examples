package com.github.shoothzj.pulsar.client.examples;

import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Producer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author hezhangjian
 */
@Slf4j
public class DemoPulsarStaticProducersInit {

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new DefaultThreadFactory("pulsar-consumer-init"));

    private CopyOnWriteArrayList<Producer<byte[]>> producers;

    private int initIndex;

    private List<String> topics;

    public DemoPulsarStaticProducersInit(List<String> topics) {
        this.topics = topics;
    }

    public void init() {
        executorService.scheduleWithFixedDelay(this::initWithRetry, 0, 10, TimeUnit.SECONDS);
    }

    private void initWithRetry() {
        if (initIndex == topics.size()) {
            return;
        }
        for (; initIndex < topics.size(); initIndex++) {
            try {
                final DemoPulsarClientInit instance = DemoPulsarClientInit.getInstance();
                final Producer<byte[]> producer = instance.getPulsarClient().newProducer().topic(topics.get(initIndex)).create();
                producers.add(producer);
            } catch (Exception e) {
                log.error("init pulsar producer error, exception is ", e);
                break;
            }
        }
    }

    public CopyOnWriteArrayList<Producer<byte[]>> getProducers() {
        return producers;
    }

}