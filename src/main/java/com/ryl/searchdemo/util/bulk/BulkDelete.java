package com.ryl.searchdemo.util.bulk;

import lombok.Data;
import lombok.experimental.Accessors;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.List;

/**
 * @author: ryl
 * @description:
 * @date: 2020-08-04 12:01:10
 */
@Data
@Accessors(chain = true)
public class BulkDelete extends BulkBase {

    private List<String> ids;

    public BulkDelete(RestHighLevelClient client) {
        super(client);
    }

    @Override
    public void buildBulkRequest() {

        super.bulkRequest = new BulkRequest();
        ids.forEach(id -> {
            bulkRequest.add(new DeleteRequest(index).id(id));
        });
    }
}
