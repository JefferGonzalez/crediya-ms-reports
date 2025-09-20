package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.config.security.SecurityConfig;
import co.com.pragma.crediya.api.config.security.SecurityContextRepository;
import co.com.pragma.crediya.api.constants.ApiConstants;
import co.com.pragma.crediya.api.dto.ApprovedApplicationsSummaryResponse;
import co.com.pragma.crediya.api.exceptions.handler.CustomAccessDeniedHandler;
import co.com.pragma.crediya.api.mapper.ApprovedApplicationsSummaryRestMapper;
import co.com.pragma.crediya.model.common.constants.DomainConstants;
import co.com.pragma.crediya.model.loan.ApprovedApplicationSummary;
import co.com.pragma.crediya.model.loan.constants.ApprovedApplicationSummaryFieldNames;
import co.com.pragma.crediya.usecase.loan.ApprovedApplicationSummaryUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {
        RouterRest.class,
        ReportsHandler.class,
        SecurityConfig.class
})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private SecurityContextRepository securityContextRepository;

    @MockitoBean
    private CustomAccessDeniedHandler accessDeniedHandler;

    @MockitoBean
    private ApprovedApplicationsSummaryRestMapper mapper;

    @MockitoBean
    private ApprovedApplicationSummaryUseCase approvedApplicationSummaryUseCase;

    private ApprovedApplicationSummary approvedApplicationSummary;

    private ApprovedApplicationsSummaryResponse approvedApplicationsSummaryResponse;

    @BeforeEach
    void setup() {
        approvedApplicationSummary = new ApprovedApplicationSummary(ApprovedApplicationSummaryFieldNames.SUMMARY, 0, BigDecimal.ZERO, null);

        approvedApplicationsSummaryResponse = new ApprovedApplicationsSummaryResponse(0, BigDecimal.ZERO, null);
    }

    @Test
    @WithMockUser(authorities = {DomainConstants.ADMIN_ROLE})
    void shouldGetApprovedApplicationsSummarySuccessfully() {
        when(approvedApplicationSummaryUseCase.getApprovedApplicationsSummary())
                .thenReturn(Mono.just(approvedApplicationSummary));

        when(mapper.toResponse(any(ApprovedApplicationSummary.class)))
                .thenReturn(approvedApplicationsSummaryResponse);

        webTestClient.get()
                .uri(ApiConstants.REPORTS_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApprovedApplicationsSummaryResponse.class)
                .value(response -> {
                            assertThat(response).isInstanceOf(ApprovedApplicationsSummaryResponse.class);
                            assertThat(response).isNotNull();
                            assertThat(response.getApprovedApplicationsCount()).isZero();
                        }
                );
    }

}
