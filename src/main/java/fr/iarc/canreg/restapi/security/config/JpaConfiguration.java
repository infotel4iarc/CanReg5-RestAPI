package fr.iarc.canreg.restapi.security.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
@EntityScan("fr.iarc.canreg.restapi.model")
@EnableJpaRepositories
@EnableTransactionManagement
public class JpaConfiguration implements TransactionManagementConfigurer {

    private final TransactionManager transactionManager;

    /**
     * Constructor
     *
     * @param transactionManager the transactionManager name 'transactionManager'
     */
    public JpaConfiguration(@Qualifier("transactionManager") TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public TransactionManager annotationDrivenTransactionManager() {
        return transactionManager;
    }
}