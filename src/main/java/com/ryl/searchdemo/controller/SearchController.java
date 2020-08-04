package com.ryl.searchdemo.controller;

import com.ryl.framework.base.ResultModel;
import com.ryl.searchdemo.model.Item;
import com.ryl.searchdemo.service.SearchService;
import com.ryl.searchdemo.util.bulk.BulkInsert;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
    public ResultModel<List<Item>> search() throws IOException {
        List<Item> itemList = searchService.search("idx_item",new MatchAllQueryBuilder());
        return ResultModel.success(itemList);
    }

    @RequestMapping("/bulkInsert")
    public ResultModel<Item> bulkInsert() throws IOException {
        List<Item> itemList = searchService.search("idx_item",new MatchAllQueryBuilder());
        BulkInsert<Item> bulkInsert = new BulkInsert<>();
        BulkResponse resp = bulkInsert.setSources(itemList).setIndex("my_temp_index").bulk();
        return ResultModel.success(resp.status());
    }

    public static void main(String[] args) {
        List<String> nameList1 = new ArrayList<>();
        nameList1.add("zhangsan");
        nameList1.add("lisi");
        nameList1.add("wangwu");

        List<String> nameList2 = new ArrayList<>();
        nameList2.add("lisi");
        nameList2.add("xiaoxiong");
        nameList2.add("xiaobo");

        boolean b = nameList2.removeAll(nameList1);
        System.out.println(nameList2);

    }
}
