package com.ryl.searchdemo.controller;

import com.ryl.searchdemo.model.Item;
import com.ryl.searchdemo.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: ryl
 * @description:
 * @date: 2020-05-14 16:01:31
 */
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;


    @RequestMapping("/autocomplete")
    public List<String> autocomplete(@RequestParam("prefix") String prefix) {
        return searchService.suggest(prefix);
    }

    @RequestMapping("/search")
    public List<Item> search() {
        List<Item> itemList = new ArrayList<>();
////        try {
//        SearchRequest request = new SearchRequest("idx_item");
//        SearchSourceBuilder builder = new SearchSourceBuilder();
//        QueryBuilder boolQueryBuilder = functionScore1();
//        builder.query(boolQueryBuilder);
//        request.source(builder);
//
//        System.out.println(String.format("[=======================>搜索语句为:%s\r\n]", boolQueryBuilder.toString()));
//
////            SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
////            SearchHit[] hits = search.getHits().getHits();
////
////            System.out.println("记录数:" + hits.length);
////
////            for (SearchHit hit : hits) {
////                Item item = JSON.parseObject(hit.getSourceAsString(),Item.class);
////                itemList.add(item);
////            }
////            return itemList;
////        } catch(IOException e) {
////            e.printStackTrace();
////        }
        return itemList;
    }
}
