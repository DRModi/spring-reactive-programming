package com.drmodi.learn.reactive.router;

import com.drmodi.learn.reactive.handler.SampleHandlerFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class RouterFunctionConfig {

    @Autowired
    SampleHandlerFunction handlerFunction;

    @Bean
    public RouterFunction<ServerResponse> route(){
        return RouterFunctions
                .route(GET("/functional/flux").and(accept(MediaType.APPLICATION_JSON)),handlerFunction::fluxOfInteger)
                .andRoute(GET("/functional/mono").and(accept(MediaType.APPLICATION_JSON)),handlerFunction::monoOfInteger);
    }
}
