package com.ryl.searchdemo.service;

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
}
