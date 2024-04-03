package com.linksauce.linksauceclientsdk;

import com.linksauce.linksauceclientsdk.client.linkSauceClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("linksauce.client")
@Data
@ComponentScan
public class LinkSauceClientConfig {

    private String accessKey;
    private String secretKey;

    @Bean
    public linkSauceClient linkSauceClient() {
        return new linkSauceClient(accessKey, secretKey);
    }

}
