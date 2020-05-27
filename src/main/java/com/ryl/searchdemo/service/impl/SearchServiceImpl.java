package com.ryl.searchdemo.service.impl;

import com.ryl.searchdemo.service.SearchService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * @author: ryl
 * @description:
 * @date: 2020-05-27 09:52:58
 */
@Service
public class SearchServiceImpl implements SearchService {

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

        suggestBuilder.addSuggestion("prefixWordsSuggest",prefixWordSuggest)
                .addSuggestion("fullPinyinSuggest",fullPinyinSuggest)
                .addSuggestion("prefixPinyinSuggest",prefixPinyinSuggest);

        System.out.println("语句打印================>");
        System.out.println(suggestBuilder.toString());

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.suggest(suggestBuilder);
        searchRequest.source(sourceBuilder);

        SearchResponse response = null;
        try{
            response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {

        }

        List<String> suggestList = new ArrayList<>();
        if (response != null) {
            LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
            List<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> entries1;
            List<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> entries2;
            List<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> entries3;

            //汉字前缀匹配
            entries1 = response.getSuggest().getSuggestion("prefixWordsSuggest").getEntries();
            entries1.forEach(e -> e.getOptions().forEach(o -> linkedHashSet.add(o.getText().toString())));
            if (linkedHashSet.size() < 10) {
                //汉字前缀匹配数量不足补充全拼匹配
                entries2 = response.getSuggest().getSuggestion("fullPinyinSuggest").getEntries();
                entries2.forEach(e -> e.getOptions().forEach(o -> {
                    //满10个不再补充
                    if (linkedHashSet.size() < 10) {
                        linkedHashSet.add(o.getText().toString());
                    }
                }));
                if (linkedHashSet.size() < 10) {
                    //全拼匹配数量不足补充首拼匹配
                    entries3 = response.getSuggest().getSuggestion("prefixPinyinSuggest").getEntries();
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
}
