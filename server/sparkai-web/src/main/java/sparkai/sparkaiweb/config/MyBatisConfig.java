package sparkai.sparkaiweb.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"sparkai.service.mapper"})
public class MyBatisConfig {
}