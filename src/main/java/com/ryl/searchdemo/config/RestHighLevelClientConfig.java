package com.ryl.searchdemo.config;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Node;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.Assert;

/**
 * @author: ryl
 * @description:
 * @date: 2020-05-14 13:40:50
 */
@Configuration
@PropertySource("classpath:es-config.properties")
public class RestHighLevelClientConfig {

    @Value("${es.host}")
    private String hosts;
    @Value("${es.port}")
    private int port;
    @Value("${es.scheme}")
    private String scheme;
    @Value("${es.token}")
    private String token;
    @Value("${es.charset}")
    private String charSet;
    @Value("${es.client.connectTimeOut}")
    private int connectTimeOut;
    @Value("${es.client.socketTimeout}")
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
