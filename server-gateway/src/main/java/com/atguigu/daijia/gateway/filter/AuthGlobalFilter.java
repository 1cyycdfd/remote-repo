package com.atguigu.daijia.gateway.filter;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.atguigu.daijia.common.util.AuthContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 全局Filter，统一处理会员登录与外部不允许访问的服务
 * </p>
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private final RedisTemplate redisTemplate;
    private final LoginProperties loginProperties;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        /*ServerHttpRequest request = exchange.getRequest();
        System.err.println("begin");
        if (isExclude(request.getPath().toString()))
            return chain.filter(exchange);
        System.out.println(request.getPath().toString());
        System.err.println("end");
        String token=null;
        List<String> headers = request.getHeaders().get("token");
        if(headers!=null&&!headers.isEmpty()){
            token = headers.get(0);
        }
        System.out.println("token"+token);
        String ID=(String) redisTemplate.opsForValue().get("user:login:" + token);
        redisTemplate.opsForValue().set("user:login:","user:login:",60*60*24*100, TimeUnit.SECONDS);
        System.out.println("ID"+ID);
        System.out.println("user:login:" + token);
        //如果数据有异常应该是立刻判断,因为多写一点，导致错误的原因就会多一点
        if(StringUtils.isNotEmpty(ID)){
            Long  id = Long.parseLong(ID);
            AuthContextHolder.setUserId(id);
            return chain.filter(exchange);
        }
        throw new RuntimeException("token无效");*/
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
    private boolean isExclude(String antPath) {
        for (String pathPattern : loginProperties.getExcludePaths()) {
            //match方法：判断是否匹配
            if(antPathMatcher.match(pathPattern, antPath)){
                return true;
            }
        }
        return false;
    }
}
