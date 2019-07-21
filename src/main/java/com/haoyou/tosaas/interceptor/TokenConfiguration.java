package com.haoyou.tosaas.interceptor;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * token 拦截器配置
 * @author gxj
 * @date 2019/06/12
 */
@Configuration
public class TokenConfiguration implements WebMvcConfigurer {

    @Autowired
    VerifyTokenInterceptor verifyTokenInterceptor;

    private static org.slf4j.Logger log = LoggerFactory.getLogger(VerifyTokenInterceptor.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //设置允许跨域的路径
        registry.addMapping("/**")
                //设置允许跨域请求的域名
                .allowedOrigins("*")
                //是否允许证书 不再默认开启
                .allowCredentials(true)
                //设置允许的方法
                .allowedMethods("*").allowedHeaders("Origin,Content-Type,Accept,token,X-Requested-With").maxAge(3600);
        //跨域允许时间
        System.out.println("addCorsMappings=");
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        InterceptorRegistration verifyToken = registry.addInterceptor(verifyTokenInterceptor);
        System.out.println("addInterceptors");
        //配置拦截器的路径
        verifyToken.addPathPatterns("/**/*.do");
        //忽略拦截路径
        verifyToken.excludePathPatterns("/user/reg.do");
        verifyToken.excludePathPatterns("/user/login.do");
        verifyToken.excludePathPatterns("/adminuser/login.do");
        verifyToken.excludePathPatterns("/adminuser/isInit.do");
        verifyToken.excludePathPatterns("/adminuser/init.do");

    }
}
