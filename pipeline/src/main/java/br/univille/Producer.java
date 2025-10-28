package br.univille;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

public interface Producer {
    public void publishEvent(Event event);
}

class ProducerImpl implements Producer {
    private final KafkaProducer<String, byte[]> producer;
    private final String topic;

    public ProducerImpl(String server, String password) {
        topic = "pixel-events"; 
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, server);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        props.put("security.protocol", "SASL_SSL");
        props.put("sasl.mechanism", "PLAIN");

        props.put("sasl.jaas.config", 
                "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                "username=\"$ConnectionString\" " +
                "password=\"" + password + "\";"
        );
        
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        this.producer = new KafkaProducer<>(props);
    }

    @Override
    public void publishEvent(Event event) {
        try {
            String key =  String.valueOf(System.currentTimeMillis());
            byte[] eventData = event.toString().getBytes();
            ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, key, eventData);
            
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.err.println("Erro ao publicar evento: " + exception.getMessage());
                } else {
                    System.out.println("Evento publicado no tópico " + metadata.topic() + 
                                       " partição " + metadata.partition() + 
                                       " offset " + metadata.offset());
                }
            });
        } catch (Exception e) {
            throw new RuntimeException("Falha ao publicar evento", e);
        }
    }

    public void close() {
        if (producer != null) {
            producer.flush();
            producer.close();
        }
    }
}
