package pitmotion.env.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {
    
    @Bean
    RestClient restClient(RestClient.Builder builder) {
        return builder
                .baseUrl("https://f1connectapi.vercel.app/api")
                .build();
    }
}