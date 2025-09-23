package co.com.pragma.crediya.config;

import co.com.bancolombia.secretsmanager.api.GenericManagerAsync;
import co.com.bancolombia.secretsmanager.api.exceptions.SecretException;
import co.com.bancolombia.secretsmanager.config.AWSSecretsManagerConfig;
import co.com.bancolombia.secretsmanager.connector.AWSSecretManagerConnectorAsync;
import co.com.pragma.crediya.jwt.config.JwtProperties;
import co.com.pragma.crediya.sqs.listener.config.SQSProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;

@Configuration
public class SecretsConfig {

    @Bean
    public GenericManagerAsync getSecretManager(@Value("${aws.region}") String region) {
        return new AWSSecretManagerConnectorAsync(getConfig(region));
    }

    private AWSSecretsManagerConfig getConfig(String region) {
        return AWSSecretsManagerConfig.builder()
                .region(Region.of(region))
                .cacheSize(5)
                .cacheSeconds(3600)
                .build();
    }

    @Bean
    public JwtProperties jwtProperties(
            final GenericManagerAsync connector, @Value("${aws.jwt.secretName}") String secretName) throws SecretException {
        return connector.getSecret(secretName, JwtProperties.class)
                .block();
    }

    @Bean
    public SQSProperties sqsProperties(
            final GenericManagerAsync connector, @Value("${aws.sqs.listener.secretName}") String secretName) throws SecretException {
        return connector.getSecret(secretName, SQSProperties.class)
                .block();
    }

}
