package priv.ljh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * WarmSearch 校园失物招领平台 后端启动类
 * 开源地址：https://github.com/lijinghailjh/WarmSearch
 * 二次开发新增：信用认领机制、保卫处贵重物品处理流程
 */
@SpringBootApplication
@MapperScan("priv.ljh.mapper")
public class WarmSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarmSearchApplication.class, args);
        System.out.println("==========================================");
        System.out.println("  WarmSearch 校园失物招领平台 后端启动成功 ");
        System.out.println("  接口文档：http://localhost:8091/doc.html ");
        System.out.println("==========================================");
    }
}
