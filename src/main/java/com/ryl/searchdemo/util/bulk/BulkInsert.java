package com.ryl.searchdemo.util.bulk;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.experimental.Accessors;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;

/**
 * @author: ryl
 * @description:
 * @date: 2020-08-04 12:00:05
 */
@Data
@Accessors(chain = true)
public class BulkInsert<T> extends BulkBase {

    /**
     * 指定docId的map,key:docId,value:source
     */
    private Map<String, T> docIdSourceMap;

    /**
     * source列表
     */
    private List<T> sources;

    @Override
    public void buildBulkRequest() {

        super.bulkRequest = new BulkRequest();
        //setRefreshPolicy 请求向ElasticSearch提交了数据，不关心数据是否已经完成刷新，直接结束请求。操作延时短、资源消耗低
        bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.NONE);

        //指定docId创建index
        if (!CollectionUtils.isEmpty(docIdSourceMap)) {
            docIdSourceMap.forEach((k,v) -> {
                if (!ObjectUtils.isEmpty(v)) {
                    bulkRequest.add(new IndexRequest().index(index).id(k).source(JSON.toJSONString(v), XContentType.JSON));
                }
            });
        }

        //自动生成docId
        if (!CollectionUtils.isEmpty(sources)) {
            sources.forEach(source -> {
                if (!ObjectUtils.isEmpty(source)) {
                    bulkRequest.add(new IndexRequest().index(index).source(JSON.toJSONString(source), XContentType.JSON));
                }
            });
        }
    }
}
