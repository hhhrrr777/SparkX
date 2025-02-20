package sparkai.sparkaiweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ComponentScan(basePackages = "sparkai")
@MapperScan(basePackages = {"sparkai.service.mapper"})
public class SparkaiWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SparkaiWebApplication.class, args);
    }

}
