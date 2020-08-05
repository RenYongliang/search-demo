package com.ryl.searchdemo.controller;

import com.alibaba.fastjson.JSON;
import com.ryl.framework.base.ResultModel;
import com.ryl.searchdemo.model.Item;
import com.ryl.searchdemo.service.SearchService;
import com.ryl.searchdemo.util.es.SearchClient;
import com.ryl.searchdemo.util.es.entity.ElasticEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: ryl
 * @description:
 * @date: 2020-05-14 16:01:31
 */
@Api(tags = "搜索接口")
@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;
    @Autowired
    private SearchClient client;


    @PostMapping("/autocomplete")
    @ApiOperation("搜索词自动补全")
    public ResultModel<List<String>> autocomplete(@RequestParam("prefix") String prefix) {
        List<String> suggests = searchService.suggest(prefix);
        return ResultModel.success(suggests);
    }

    @PostMapping("/search")
    @ApiOperation("搜索")
    public ResultModel<List<Item>> search() throws IOException {
        List<Item> itemList = searchService.search("idx_item",new MatchAllQueryBuilder());
        return ResultModel.success(itemList);
    }

    @PostMapping("/bulkInsert")
    @ApiOperation("批量新增")
    public ResultModel<Item> bulkInsert() throws IOException {
        List<Item> itemList = searchService.search("idx_item",new MatchAllQueryBuilder());
        List<ElasticEntity> entityList = new ArrayList<>();
        itemList.forEach(item -> {
            entityList.add(new ElasticEntity(item.getItemId(), JSON.toJSONString(item)));
        });
        BulkResponse bulkResponse = client.insertBatch("my_temp_index", entityList);
        List<BulkItemResponse> errorList = new ArrayList<>();
        if (bulkResponse.hasFailures()) {
            errorList = Stream.of(bulkResponse.getItems()).filter(itemResp -> itemResp.isFailed() == true).collect(Collectors.toList());
        }
        return ResultModel.success(errorList);
    }

}
