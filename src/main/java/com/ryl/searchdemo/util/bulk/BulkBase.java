package com.ryl.searchdemo.util.bulk;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

/**
 * @author: ryl
 * @description:
 * @date: 2020-08-04 11:59:52
 */
@Slf4j
@Accessors(chain = true)
public abstract class BulkBase {

    /**
     * 客户端
     */
    private RestHighLevelClient client;

    /**
     * 索引名
     */
    @Setter
    protected String index;

    /**
     * 批量请求
     */
    protected BulkRequest bulkRequest;

    public BulkBase(RestHighLevelClient client) {
        this.client = client;
    }

    /**
     * 批量同步执行请求
     * @return
     * @throws IOException
     */
    public BulkResponse bulk() throws IOException {
        buildBulkRequest();
        return client.bulk(bulkRequest, RequestOptions.DEFAULT);
    }

    /**
     * 构建bulkRequest {@link BulkBase#bulkRequest}
     *
     * @Param
     * @Return void
     * @author ryl
     * @Date 2020-08-04 15:09:14
     */
    protected abstract void buildBulkRequest();


}
