package co.com.pragma.crediya.sqs.listener.helper;

import co.com.pragma.crediya.sqs.listener.config.SQSProperties;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

@Log4j2
@Builder
public class SQSListener {

    private final SqsAsyncClient client;

    private final SQSProperties properties;

    private final Function<Message, Mono<Void>> processor;

    private String operation;

    public SQSListener start() {
        this.operation = "MessageFrom:" + properties.queueUrl();

        ExecutorService service = Executors.newFixedThreadPool(properties.numberOfThreads());
        Flux<Void> flow = listenRetryRepeat().publishOn(Schedulers.fromExecutorService(service));
        for (var i = 0; i < properties.numberOfThreads(); i++) {
            flow.subscribe();
        }

        return this;
    }

    private Flux<Void> listenRetryRepeat() {
        return listen()
                .doOnError(e -> log.error("Error listening sqs queue", e))
                .repeat();
    }

    private Flux<Void> listen() {
        return getMessages()
                .flatMap(message -> processor.apply(message)
                        .name(properties.loanApprovedEventsQueueQueueName())
                        .tag("operation", operation)
                        .then(confirm(message)))
                .onErrorContinue((e, o) -> log.error("Error listening sqs message", e));
    }

    private Mono<Void> confirm(Message message) {
        String queueUrl = properties.queueUrl() + properties.loanApprovedEventsQueueQueueName();

        return Mono.fromCallable(() -> getDeleteMessageRequest(message.receiptHandle(), queueUrl))
                .flatMap(request -> Mono.fromFuture(client.deleteMessage(request)))
                .then();
    }

    private Flux<Message> getMessages() {
        String queueUrl = properties.queueUrl() + properties.loanApprovedEventsQueueQueueName();

        return Mono.fromCallable(() -> getReceiveMessageRequest(queueUrl))
                .flatMap(request -> Mono.fromFuture(client.receiveMessage(request)))
                .doOnNext(response -> log.info("{} received messages from sqs", response.messages().size()))
                .flatMapMany(response -> Flux.fromIterable(response.messages()));
    }

    private ReceiveMessageRequest getReceiveMessageRequest(String queueUrl) {
        return ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(properties.maxNumberOfMessages())
                .waitTimeSeconds(properties.waitTimeSeconds())
                .visibilityTimeout(properties.visibilityTimeoutSeconds())
                .build();
    }

    private DeleteMessageRequest getDeleteMessageRequest(String receiptHandle, String queueUrl) {
        return DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();
    }

}
