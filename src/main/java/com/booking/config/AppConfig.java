package main.java.com.booking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "application")
public class AppConfig {

    private String uiHost;
    private String movieServiceHost;

    public String getUiHost() {
        return uiHost;
    }

    public void setUiHost(String uiHost) {
        this.uiHost = uiHost;
    }

    public String getMovieServiceHost() {
        return movieServiceHost;
    }

    public void setMovieServiceHost(String movieServiceHost) {
        this.movieServiceHost = movieServiceHost;
    }
}
