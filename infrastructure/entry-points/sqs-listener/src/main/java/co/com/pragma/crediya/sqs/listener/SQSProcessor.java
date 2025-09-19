package co.com.pragma.crediya.sqs.listener;

import co.com.pragma.crediya.model.loan.ApprovedApplication;
import co.com.pragma.crediya.usecase.loan.ApprovedApplicationSummaryUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final ObjectMapper objectMapper;

    private final ApprovedApplicationSummaryUseCase approvedApplicationSummaryUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        try {
            ApprovedApplication response = objectMapper.readValue(message.body(), ApprovedApplication.class);

            return approvedApplicationSummaryUseCase.recordApprovedApplication(response);
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

}
