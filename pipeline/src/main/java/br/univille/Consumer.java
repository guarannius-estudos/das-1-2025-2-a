package br.univille;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

public interface Consumer {
    List<Event> getEvents();
}

class ConsumerImpl implements Consumer {
    private final KafkaConsumer<String, byte[]> consumer;
    private final String topic;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread consumerThread;
    private List<Event> events = Collections.synchronizedList(new ArrayList<>());

    public ConsumerImpl(String server, String password) {
        topic = "pixel-events";
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "PLAIN");
        
        props.put("sasl.jaas.config",
                "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                        "username=\"$ConnectionString\" " +
                        "password=\"" + password + "\";"
        );

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, 50 * 1024 * 1024); // 50MB
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 10 * 1024 * 1024); // 10MB por partição
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000); // 5 minutos
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000); // 30 segundos
        this.consumer = new KafkaConsumer<>(props);
        startConsuming();
    }

    private void startConsuming() {
        if (running.compareAndSet(false, true)) {
            final CountDownLatch shutdownLatch = new CountDownLatch(1);

            consumerThread = new Thread(() -> {
                try {
                    consumer.subscribe(Collections.singletonList(topic));

                    while (running.get()) {
                        ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofSeconds(1));

                        if (!records.isEmpty()) {
                            System.out.println("Recebido lote de " + records.count() + " registros.");

                            for (ConsumerRecord<String, byte[]> record : records) {
                                try {
                                    processRecord(record);
                                } catch (Exception e) {
                                    System.err.println("Erro ao processar registro individualmente: " + e.getMessage() +
                                            " para offset " + record.offset() + " na partição " + record.partition());
                                }
                            }

                            try {
                                consumer.commitSync();
                                System.out.println("Offsets commitados sincronamente após processar " + records.count() + " registros.");
                            } catch (CommitFailedException e) {
                                System.err.println("Falha no commit síncrono: " + e.getMessage());
                            }
                        }
                    }
                } catch (WakeupException e) {
                    if (running.get()) {
                        System.err.println("WakeupException recebida, mas o consumidor não estava parando: " + e.getMessage());
                    }
                } catch (Exception e) {
                    System.err.println("Erro inesperado no loop de consumo: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    System.out.println("Fechando o consumidor Kafka.");
                    consumer.close();
                    shutdownLatch.countDown();
                }
            });

            consumerThread.setName("KafkaConsumerThread-" + topic);
            consumerThread.start();
        }
    }

    private void processRecord(ConsumerRecord<String, byte[]> record) {
        String eventAsString = new String(record.value());
        System.out.print("Processando evento: " + eventAsString);
        System.out.print("  do tópico: " + record.topic());
        System.out.print("  na partição: " + record.partition());
        System.out.print("  no offset: " + record.offset());
        System.out.println("  com chave: " + record.key());
        Event event = Event.fromJson(eventAsString);
        events.add(event);
    }

    public void stopConsuming() {
        if (running.compareAndSet(true, false)) {
            System.out.println("Sinalizando consumidor para parar...");
            consumer.wakeup();

            try {
                if (consumerThread != null) {
                    consumerThread.join(5000);

                    if (consumerThread.isAlive()) {
                        System.err.println("Thread do consumidor não parou após o timeout. Interrompendo...");
                        consumerThread.interrupt();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Interrompido enquanto aguardava a parada do consumidor");
            }
            System.out.println("Consumidor parado.");
        }
    }

    @Override
    public List<Event> getEvents() {
        List<Event> tempList;

        synchronized (events) {
            if (events.isEmpty()) {
                return Collections.emptyList();
            }

            tempList = new ArrayList<>(events);
            events.clear();
        }
        
        System.out.println("Retornando " + tempList.size() + " eventos e limpando a lista interna.");
        return tempList;
    }
}
