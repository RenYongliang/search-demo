package com.ryl.searchdemo.service.impl;

import com.alibaba.fastjson.JSON;
import com.ryl.searchdemo.model.Item;
import com.ryl.searchdemo.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author: ryl
 * @description:
 * @date: 2020-05-27 09:52:58
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    private  final String PREFIX_WORDS_SUGGEST = "prefixWordsSuggest";
    private final String FULL_PINYIN_SUGGEST = "fullPinyinSuggest";
    private final String PREFIX_PINYIN_SUGGEST = "prefixPinyinSuggest";

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Override
    public List<String> suggest(String prefix) {

        SearchRequest searchRequest = new SearchRequest("my_token");
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        //汉字前缀匹配
        CompletionSuggestionBuilder prefixWordSuggest = SuggestBuilders.completionSuggestion("title").prefix(prefix).size(10);
        //全拼匹配
        CompletionSuggestionBuilder fullPinyinSuggest = SuggestBuilders.completionSuggestion("title.pinyin_full").prefix(prefix).size(10);
        //拼音前缀匹配
        CompletionSuggestionBuilder prefixPinyinSuggest = SuggestBuilders.completionSuggestion("title.pinyin_prefix").prefix(prefix).size(10);

        suggestBuilder.addSuggestion(PREFIX_WORDS_SUGGEST, prefixWordSuggest)
                .addSuggestion(FULL_PINYIN_SUGGEST, fullPinyinSuggest)
                .addSuggestion(PREFIX_PINYIN_SUGGEST, prefixPinyinSuggest);

        log.info("语句打印================>\r\n{}",suggestBuilder.toString());

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.suggest(suggestBuilder);
        searchRequest.source(sourceBuilder);

        SearchResponse response = null;
        try {
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> suggestList = new ArrayList<>();
        if (response != null) {
            LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
            List<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> entries1;
            List<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> entries2;
            List<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> entries3;

            //汉字前缀匹配
            entries1 = response.getSuggest().getSuggestion(PREFIX_WORDS_SUGGEST).getEntries();
            entries1.forEach(e -> e.getOptions().forEach(o -> linkedHashSet.add(o.getText().toString())));
            if (linkedHashSet.size() < 10) {
                //汉字前缀匹配数量不足补充全拼匹配
                entries2 = response.getSuggest().getSuggestion(FULL_PINYIN_SUGGEST).getEntries();
                entries2.forEach(e -> e.getOptions().forEach(o -> {
                    //满10个不再补充
                    if (linkedHashSet.size() < 10) {
                        linkedHashSet.add(o.getText().toString());
                    }
                }));

                if (linkedHashSet.size() < 10) {
                    //全拼匹配数量不足补充首拼匹配
                    entries3 = response.getSuggest().getSuggestion(PREFIX_PINYIN_SUGGEST).getEntries();
                    entries3.forEach(e -> e.getOptions().forEach(o -> {
                        //满10个不再补充
                        if (linkedHashSet.size() < 10) {
                            linkedHashSet.add(o.getText().toString());
                        }
                    }));
                }
            }

            suggestList = new ArrayList<>(linkedHashSet);
        }
        return suggestList;
    }

    @Override
    public List<Item> search(String index, QueryBuilder queryBuilder) throws IOException {
        List<Item> itemList = new ArrayList<>();
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(queryBuilder);
        request.source(builder);

        log.info(String.format("=======================>搜索语句为:\r\n{}", queryBuilder.toString()));
        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        SearchHit[] hits = search.getHits().getHits();

        log.info("记录数:" + hits.length);
        for (SearchHit hit : hits) {
            Item item = JSON.parseObject(hit.getSourceAsString(),Item.class);
            itemList.add(item);
        }
        return itemList;
    }
}
