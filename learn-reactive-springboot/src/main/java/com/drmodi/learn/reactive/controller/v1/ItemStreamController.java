package com.drmodi.learn.reactive.controller.v1;

import com.drmodi.learn.reactive.document.ItemCapped;
import com.drmodi.learn.reactive.repository.ItemCappedReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import static com.drmodi.learn.reactive.util.ItemConstants.ITEM_STREAM_END_POINT_V1;

@RestController
public class ItemStreamController {

    @Autowired
    ItemCappedReactiveRepository itemCappedReactiveRepository;

    @GetMapping(value = ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getStreamOfItemCapped(){
        return itemCappedReactiveRepository.findItemCappedBy();
    }

}
