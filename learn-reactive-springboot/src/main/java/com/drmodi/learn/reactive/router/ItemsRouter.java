package com.drmodi.learn.reactive.router;

import com.drmodi.learn.reactive.handler.ItemHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


import static com.drmodi.learn.reactive.util.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;
import static com.drmodi.learn.reactive.util.ItemConstants.ITEM_FUNCTIONAL_STREAM_END_POINT_V1;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemRoute(ItemHandler handler){

        return RouterFunctions
                .route(GET(ITEM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON))
                        ,handler::getAllItems)
                .andRoute(GET(ITEM_FUNCTIONAL_END_POINT_V1+"/{id}").and(accept(MediaType.APPLICATION_JSON))
                        ,handler::getItemById)
                .andRoute(POST(ITEM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON))
                        ,handler::saveAnItem)
                .andRoute(DELETE(ITEM_FUNCTIONAL_END_POINT_V1+"/{id}").and(accept(MediaType.APPLICATION_JSON))
                        ,handler::deleteItemById)
                .andRoute(PUT(ITEM_FUNCTIONAL_END_POINT_V1+"/{id}").and(accept(MediaType.APPLICATION_JSON))
                        ,handler::updateAnItem);
                //.andRoute(GET(ITEM_FUNCTIONAL_END_POINT_V1+"/exception").and(accept(MediaType.APPLICATION_JSON)), handler::getRuntimeException);

    }

    @Bean
    public RouterFunction<ServerResponse> itemRouteException(ItemHandler handler){

        return RouterFunctions
                    .route(GET("/test/exception").and(accept(MediaType.APPLICATION_JSON)), handler::getRuntimeException);
    }

    @Bean
    public RouterFunction<ServerResponse> itemStreamRoute(ItemHandler itemHandler){
        return RouterFunctions
                .route(GET(ITEM_FUNCTIONAL_STREAM_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON))
                    ,itemHandler::itemCappedStream);


    }

}
