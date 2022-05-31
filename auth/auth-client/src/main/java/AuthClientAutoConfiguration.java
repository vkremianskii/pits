import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(AuthClient.class)
@EnableConfigurationProperties(AuthProperties.class)
public class AuthClientAutoConfiguration {

}
