package com.ryl.searchdemo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author: ryl
 * @description:
 * @date: 2020-05-14 13:55:08
 */
@Service
@Slf4j
public class RestHighLevelClientService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 创建索引
     * @param indexName
     * @param settings
     * @param mapping
     * @return
     * @throws IOException
     */
    public CreateIndexResponse createIndex(String indexName, String settings, String mapping) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        if (settings != null && !settings.equals("")) {
            request.settings(settings, XContentType.JSON);
        }
        return restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
    }

    /**
     * 删除索引
     * @param indexNames
     * @return
     * @throws IOException
     */
    public AcknowledgedResponse deleteIndex(String... indexNames) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(indexNames);
        return restHighLevelClient.indices().delete(request,RequestOptions.DEFAULT);
    }

    /**
     * 判断索引是否存在
     * @param indexName
     * @return
     * @throws IOException
     */
    public boolean indexExists(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    /**
     * 简单模糊匹配 默认分页0,10
     * @param field
     * @param key
     * @param page
     * @param size
     * @param indexNames
     * @return
     * @throws IOException
     */
    public SearchResponse search(String field,String key,int page,int size, String... indexNames) throws IOException {
        SearchRequest request = new SearchRequest(indexNames);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(new MatchQueryBuilder(field,key))
                .from(page)
                .size(size);
        request.source(builder);
        return restHighLevelClient.search(request,RequestOptions.DEFAULT);
    }

    /**
     * 简单模糊匹配 不分页
     * @param field
     * @param key
     * @param indexNames
     * @return
     * @throws IOException
     */
    public SearchResponse search(String field,String key, String... indexNames) throws IOException {
        SearchRequest request = new SearchRequest(indexNames);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(new MatchQueryBuilder(field,key));
        request.source(builder);
        return restHighLevelClient.search(request,RequestOptions.DEFAULT);
    }

    /**
     * term 精确匹配
     * @param field
     * @param key
     * @param page
     * @param size
     * @param indexNames
     * @return
     * @throws IOException
     */
    public SearchResponse termSearch(String field,String key,int page,int size,String... indexNames) throws IOException {
        SearchRequest request = new SearchRequest(indexNames);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.termsQuery(field,key))
                .from(page)
                .size(size);
        request.source(builder);
        return restHighLevelClient.search(request,RequestOptions.DEFAULT);
    }

    /**
     * 搜索
     * @param field
     * @param key
     * @param rangeField
     * @param from
     * @param to
     * @param termField
     * @param termVal
     * @param indexNames
     * @return
     * @throws IOException
     */
    public SearchResponse search(String field, String key, String rangeField,
                                 String from, String to, String termField,
                                 String termVal, String... indexNames) throws IOException {
        SearchRequest request = new SearchRequest(indexNames);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(new MatchQueryBuilder(field,key))
                .must(new RangeQueryBuilder(rangeField).from(from).to(to))
                .must(new TermQueryBuilder(termField,termVal));
        builder.query(boolQueryBuilder);
        request.source(builder);
        log.info("[搜索语句为1:{}]",request.source().toString());
        log.info("[搜索语句为2:{}]",builder.toString());
        return restHighLevelClient.search(request, RequestOptions.DEFAULT);
    }

    /**
     *  批量导入
     * @param indexName
     * @param isAutoId 使用自动id 还是使用传入对象的id
     * @param source
     * @return
     * @throws IOException
     */
    public BulkResponse importAll(String indexName, boolean isAutoId, String source) throws IOException {
        if (source.length() == 0) {
            //todo 抛出异常 导入数据为空
        }
        BulkRequest request = new BulkRequest();
        JsonNode jsonNode = objectMapper.readTree(source);

        if (jsonNode.isArray()) {
            for (JsonNode node : jsonNode) {
                if (isAutoId) {
                    request.add(new IndexRequest(indexName).source(node.toString(), XContentType.JSON));
                } else {
                    request.add(new IndexRequest(indexName)
                    .id(node.get("id").asText())
                    .source(node.toString(), XContentType.JSON));
                }
            }
        }

        return restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
    }
}
