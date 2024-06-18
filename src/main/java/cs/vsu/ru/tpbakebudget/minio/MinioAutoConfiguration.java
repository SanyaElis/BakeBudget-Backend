package cs.vsu.ru.tpbakebudget.minio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({MinioProperties.class})
public class MinioAutoConfiguration {

    private final MinioProperties properties;

    @Autowired
    public MinioAutoConfiguration(@Qualifier("minioProperties") MinioProperties properties) {
        this.properties = properties;
    }


    @Bean
    @ConditionalOnMissingBean(MinioTemplate.class)
    @ConditionalOnProperty(name = "minio.url")
    public MinioTemplate template() {
        return new MinioTemplate(properties);
    }
}


