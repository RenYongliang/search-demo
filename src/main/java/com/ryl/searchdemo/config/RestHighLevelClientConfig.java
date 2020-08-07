package com.ryl.searchdemo.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * @author: ryl
 * @description:
 * @date: 2020-05-14 13:40:50
 */
@Configuration
@ConfigurationProperties(prefix = "es")
@Slf4j
@Data
public class RestHighLevelClientConfig {

    private String hosts;

    private int port;

    private String scheme;

    private String token;

    private String charSet;

    private int connectTimeOut;

    private int socketTimeout;



    @Bean
    public RestClientBuilder restClientBuilder() {

        RestClientBuilder restClientBuilder = RestClient.builder(getHttpHostArr());
        Header[] defaultHeaders = new Header[]{
                new BasicHeader("Accept", "*/*"),
                new BasicHeader("Charset", charSet),
                //设置token 是为了安全 网关可以验证token来决定是否发起请求 我们这里只做象征性配置
                new BasicHeader("E_TOKEN", token)
        };
        restClientBuilder.setDefaultHeaders(defaultHeaders);
        restClientBuilder.setFailureListener(new RestClient.FailureListener() {
            @Override
            public void onFailure(Node node) {
                System.out.println("监听某个es节点失败");
            }
        });
        restClientBuilder.setRequestConfigCallback(builder ->
                builder.setConnectTimeout(connectTimeOut).setSocketTimeout(socketTimeout));
        return restClientBuilder;
    }

    @Bean
    public RestHighLevelClient restHighLevelClient(RestClientBuilder restClientBuilder) {
        return new RestHighLevelClient(restClientBuilder);
    }


    @Bean
    BulkProcessor builderBulkProcessor(RestHighLevelClient restHighLevelClient) {
        BulkProcessor.Listener listener = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long l, BulkRequest bulkRequest) {
                //在每次执行BulkRequest之前调用，该方法允许知道要在BulkRequest中执行的操作的数量
                log.debug("Executing bulk [{}] with {} requests", l, bulkRequest.numberOfActions());
            }

            @Override
            public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {
                //在每次执行BulkRequest之后调用，此方法允许知道BulkResponse是否包含错误
                if (bulkResponse.hasFailures()) {
                    log.warn("Bulk [{}] executed with failures", l);
                    for (BulkItemResponse bulkItemResponse : bulkResponse.getItems()) {
                        if (bulkItemResponse.isFailed()) {
                            log.warn("bulkItemResponse.getFailure():{}", bulkItemResponse.getFailure().getMessage());
                        }
                    }
                } else {
                    log.debug("Bulk [{}] completed in {} milliseconds", l, bulkResponse.getTook().getMillis());
                }
            }

            @Override
            public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {
                //当BulkRequest失败时调用此方法
                log.error("Failed to execute bulk", throwable);
            }
        };

        BulkProcessor.Builder builder = BulkProcessor.builder(
                (request, bulkListener) ->
                        restHighLevelClient.bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
                listener);

        //根据当前添加的操作数设置刷新新批量请求的时间(默认为1000，使用-1禁用它)
        //builder.setBulkActions(this.bulkActions);
        //根据当前添加的操作大小设置刷新新批量请求的时间(默认值为5MB，使用-1禁用它)
        //builder.setBulkSize(this.bulkSize);
        //设置允许执行的并发请求数(默认为1，使用0只允许执行单个请求)
        //builder.setConcurrentRequests(this.concurrentRequests);
        //设置刷新间隔，如果间隔过去，刷新所有BulkRequest挂起(默认为NotSet)
        //builder.setFlushInterval(this.flushInterval);
        //设置一个常量后退策略，最初等待1秒，最多重试3次。有关更多选项，请参见BackoffPolicy.noBackoff()、BackoffPolicy.constantBackoff()和BackoffPolicy.指数退避()。
        //builder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1L), 3));
        return builder.build();
    }






    /**
     * hosts字符串转成HttpHost数组
     * @return
     */
    private HttpHost[] getHttpHostArr() {
        Assert.hasLength(hosts,"host must not be null or empty!");

        String[] hostArr = hosts.split(",");
        HttpHost[] httpHostArr = new HttpHost[hostArr.length];
        for (int i=0; i < hostArr.length; i++) {
            //分割ip和端口
            String[] arr = hostArr[i].split(":");
            httpHostArr[i] = new HttpHost(arr[0],Integer.parseInt(arr[1]),"http");
        }
        return httpHostArr;
    }
}
