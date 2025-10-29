package itmo.soa.heroes.config;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RoundRobinRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Ribbon Load Balancer (как требуется в задании)
 * Используется Round Robin стратегия для балансировки нагрузки
 */
@Configuration
public class RibbonConfiguration {

    @Bean
    public IRule ribbonRule() {
        // Round Robin - циклический выбор сервера
        return new RoundRobinRule();
    }
}
