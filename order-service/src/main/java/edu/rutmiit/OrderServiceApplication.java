package edu.rutmiit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication(scanBasePackages = "edu.rutmiit")
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
@EnableSpringDataWebSupport
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
