package co.com.pragma.crediya.sqs.listener.config;

public record SQSProperties(
        String region,
        String queueUrl,
        String loanApprovedEventsQueueQueueName,
        int waitTimeSeconds,
        int visibilityTimeoutSeconds,
        int maxNumberOfMessages,
        int numberOfThreads) {
}
