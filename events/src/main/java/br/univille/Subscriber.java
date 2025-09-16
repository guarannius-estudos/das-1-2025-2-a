package br.univille;

import com.azure.core.amqp.AmqpTransportType;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.models.ServiceBusReceiveMode;

public class Subscriber {
    public static void main(String[] args) {
        var servidor = "sbdas12025a.servicebus.windows.net";
        var topicName = "topic-das1-a";
        var assinatura = "subscription-gustavoantonius";
        String chave = System.getenv("CHAVE");
        ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
                .fullyQualifiedNamespace(servidor)
                .transportType(AmqpTransportType.AMQP_WEB_SOCKETS)
                .connectionString(chave)
                .processor()
                .topicName(topicName)
                .subscriptionName(assinatura)
                .receiveMode(ServiceBusReceiveMode.PEEK_LOCK)
                .processMessage(context -> {
                    System.out.println("MSG: " + context
                            .getMessage()
                            .getBody()
                            .toString());
                    context.complete();
                })
                .processError(context -> {
                    System.out.println("Deu bom não.");
                })
                .buildProcessorClient();
        processorClient.start();
    }
}
