package sparkai.sparkaiweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "sparkai")
public class SparkaiWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SparkaiWebApplication.class, args);
    }

}
