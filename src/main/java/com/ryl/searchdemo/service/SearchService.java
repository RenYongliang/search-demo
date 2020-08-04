package com.ryl.searchdemo.service;

import com.ryl.searchdemo.model.Item;
import org.elasticsearch.index.query.QueryBuilder;

import java.io.IOException;
import java.util.List;

/**
 * @author: ryl
 * @description:
 * @date: 2020-05-27 09:52:29
 */
public interface SearchService {

    /**
     * 搜索词自动补全
     * @param prefix
     * @return
     */
    List<String> suggest(String prefix);

    List<Item> search(String index, QueryBuilder queryBuilder) throws IOException;
}
