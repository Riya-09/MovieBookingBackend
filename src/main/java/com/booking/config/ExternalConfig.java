package main.java.com.booking.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalConfig {

    @Bean
    public OkHttpClient createOkHttpClient() {
        return new OkHttpClient();
    }

    @Bean
    public Request.Builder createOkHttpRequestBuilder() {
        return new Request.Builder();
    }

    @Bean
    public ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }
}
