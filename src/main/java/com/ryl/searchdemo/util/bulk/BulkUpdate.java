package com.ryl.searchdemo.util.bulk;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.experimental.Accessors;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * @author: ryl
 * @description:
 * @date: 2020-08-04 12:00:52
 */
@Data
@Accessors(chain = true)
public class BulkUpdate<T> extends BulkBase {

    /**
     * 指定docId的map,key:docId,value:source
     */
    private Map<String, T> docIdSourceMap;

    @Override
    public void buildBulkRequest() {

        if (!CollectionUtils.isEmpty(docIdSourceMap)) {
            super.bulkRequest = new BulkRequest();
            bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.NONE);
            docIdSourceMap.forEach((k,v) -> {
                if (!ObjectUtils.isEmpty(v)) {
                    bulkRequest.add(new UpdateRequest().index(index).id(k).doc(JSON.toJSONString(v),XContentType.JSON));
                }
            });
        }
    }
}
