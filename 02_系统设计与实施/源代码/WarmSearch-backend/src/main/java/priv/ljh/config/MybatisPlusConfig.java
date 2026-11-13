package priv.ljh.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis-Plus 配置（3.0.5 使用 PaginationInterceptor）
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public PaginationInterceptor paginationInterceptor() {
        // Mybatis-Plus 3.0.5 分页插件，默认即可
        return new PaginationInterceptor();
    }
}
