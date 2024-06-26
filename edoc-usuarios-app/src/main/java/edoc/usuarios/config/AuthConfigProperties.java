package edoc.usuarios.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "auth")
@Data
public class AuthConfigProperties {
    
    private boolean publicSignUpUrlEnable;
    private String jwtSecret;
    private Long jwtExpirationMiliseg;
    private Long resetPasswordTokenExpirationMiliseg;
}