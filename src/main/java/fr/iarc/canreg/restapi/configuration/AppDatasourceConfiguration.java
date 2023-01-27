package fr.iarc.canreg.restapi.configuration;


import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class AppDatasourceConfiguration {

    @Bean(name = "appDatasource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource appDatasource() {
        final DataSource build = DataSourceBuilder.create().build();
        return build;
    }
}
