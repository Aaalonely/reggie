package reggie.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc //该注解是Springfox-swagger框架提供的使用Swagger注解，该注解必须加
@EnableKnife4j   //该注解是knife4j提供的增强注解,Ui提供了例如动态参数、参数过滤、接口排序等增强功能,如果你想使用这些增强功能就必须加该注解，否则可以不用加
public class SwaggerConfig {


    /**
     *  api的主页显示信息
     */
    private static ApiInfo apiInfo;

    static {
        apiInfo = new ApiInfoBuilder()
                .title("瑞吉外卖")
                .description("瑞吉外卖接口文档")
                .version("1.0")
                .build();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                // 配置分组名
                .groupName("api")
                .apiInfo(apiInfo)
                .select()
                // 设置扫描包的地址 : com.hanliy.controller
                .apis(RequestHandlerSelectors.basePackage("reggie.controller"))
                // 设置路径筛选 只扫描com.hanliy.controller/test/下面的包
                // .paths(PathSelectors.ant("/test/**"))
                // com.hanliy.controller下的任何接口信息
                .paths(PathSelectors.any())
                .build();
    }

}