package br.univille;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        String kafkaServer = System.getenv("KAFKA_SERVER");
        String kafkaPassword = System.getenv("KAFKA_PASSWORD");
        var producer = new ProducerImpl(kafkaServer,kafkaPassword);
        var consumer = new ConsumerImpl(kafkaServer,kafkaPassword);
        SwingUtilities.invokeLater(() -> new Form("PRODUTOR",producer,null));
        SwingUtilities.invokeLater(() -> new Form("CONSUMIDOR",null,consumer));
    }
}
