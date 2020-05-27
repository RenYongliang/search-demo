package com.ryl.searchdemo;

import com.hankcs.hanlp.HanLP;
import com.ryl.searchdemo.service.ItemService;
import com.ryl.searchdemo.service.RestHighLevelClientService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest(classes = SearchDemoApplication.class)
@RunWith(SpringRunner.class)
class SearchDemoApplicationTests {

    @Autowired
    private ItemService itemService;
    @Autowired
    private RestHighLevelClientService restHighLevelClientService;

    @Test
    void contextLoads() {
        String itemJson = itemService.getItemJson();
        System.out.println(itemJson);
    }

    @Test
    public void createIdx() throws IOException {
        String settings = "" +
                "  {\n" +
                "      \"number_of_shards\" : \"2\",\n" +
                "      \"number_of_replicas\" : \"0\"\n" +
                "   }";
        String mappings = "" +
                "{\n" +
                "  \"dynamic\": \"true\"" +
                "  \"properties\": {\n" +
                "    \"itemId\": {\n" +
                "      \"type\": \"keyword\",\n" +
                "      \"ignore_above\": 64\n" +
                "    },\n" +
                "    \"urlId\": {\n" +
                "      \"type\": \"keyword\",\n" +
                "      \"ignore_above\": 64\n" +
                "    },\n" +
                "    \"sellAddress\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\",\n" +
                "      \"fields\": {\n" +
                "        \"keyword\": {\n" +
                "          \"ignore_above\": 256,\n" +
                "          \"type\": \"keyword\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"courierFee\": {\n" +
                "      \"type\": \"text\"\n" +
                "    },\n" +
                "    \"promotions\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\",\n" +
                "      \"fields\": {\n" +
                "        \"keyword\": {\n" +
                "          \"ignore_above\": 256,\n" +
                "          \"type\": \"keyword\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"originalPrice\": {\n" +
                "      \"type\": \"keyword\",\n" +
                "      \"ignore_above\": 64\n" +
                "    },\n" +
                "    \"startTime\": {\n" +
                "      \"type\": \"date\",\n" +
                "      \"format\": \"yyyy-MM-dd HH:mm:ss\"\n" +
                "    },\n" +
                "    \"endTime\": {\n" +
                "      \"type\": \"date\",\n" +
                "      \"format\": \"yyyy-MM-dd HH:mm:ss\"\n" +
                "    },\n" +
                "    \"title\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\",\n" +
                "      \"fields\": {\n" +
                "        \"keyword\": {\n" +
                "          \"ignore_above\": 256,\n" +
                "          \"type\": \"keyword\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"serviceGuarantee\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\",\n" +
                "      \"fields\": {\n" +
                "        \"keyword\": {\n" +
                "          \"ignore_above\": 256,\n" +
                "          \"type\": \"keyword\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"venue\": {\n" +
                "      \"type\": \"text\",\n" +
                "      \"analyzer\": \"ik_max_word\",\n" +
                "      \"search_analyzer\": \"ik_smart\",\n" +
                "      \"fields\": {\n" +
                "        \"keyword\": {\n" +
                "          \"ignore_above\": 256,\n" +
                "          \"type\": \"keyword\"\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    \"currentPrice\": {\n" +
                "      \"type\": \"keyword\",\n" +
                "      \"ignore_above\": 64\n" +
                "    }\n" +
                "  }\n" +
                "}";
        restHighLevelClientService.createIndex("idx_item",settings ,mappings);
    }

    @Test
    public void createSuggestIdx() throws IOException {
        String settings = "" +
                "{\n" +
                "    \"analysis\": {\n" +
                "      \"tokenizer\":{\n" +
                "        \"full_pinyin\":{\n" +
                "          \"type\":\"pinyin\",\n" +
                "          \"keep_first_letter\" : false,\n" +
                "          \"keep_separate_first_letter\" : false,\n" +
                "          \"keep_full_pinyin\" : true,\n" +
                "          \"keep_joined_full_pinyin\" : true,\n" +
                "          \"keep_original\" : true,\n" +
                "          \"lowercase\" : true,\n" +
                "          \"remove_duplicated_term\" : true\n" +
                "        },\n" +
                "        \"prefix_pinyin\":{\n" +
                "          \"type\":\"pinyin\",\n" +
                "          \"keep_first_letter\":true,\n" +
                "          \"keep_separate_first_letter\" : true,\n" +
                "          \"keep_full_pinyin\" : false,\n" +
                "          \"keep_joined_full_pinyin\" : false,\n" +
                "          \"keep_original\" : true,\n" +
                "          \"lowercase\" : true,\n" +
                "          \"remove_duplicated_term\" : true\n" +
                "        }\n" +
                "      },\n" +
                "      \"analyzer\": {\n" +
                "        \"pinyin_full\":{\n" +
                "          \"type\":\"custom\",\n" +
                "          \"tokenizer\":\"full_pinyin\"\n" +
                "        },\n" +
                "        \"pinyin_prefix\":{\n" +
                "          \"type\":\"custom\",\n" +
                "          \"tokenizer\":\"prefix_pinyin\"\n" +
                "        },\n" +
                "        \"lowercase_keyword\":{\n" +
                "          \"type\":\"custom\",\n" +
                "          \"tokenizer\":\"keyword\",\n" +
                "          \"filter\":[\"lowercase\"]\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }";
        String mappings = "" +
                "{\n" +
                "    \"dynamic\":true, \n" +
                "    \"properties\": {\n" +
                "      \"title\":{\n" +
                "        \"type\": \"completion\",\n" +
                "        \"analyzer\": \"keyword\",\n" +
                "        \"fields\": {\n" +
                "          \"pinyin_full\":{\n" +
                "            \"type\":\"completion\",\n" +
                "            \"analyzer\":\"pinyin_full\",\n" +
                "            \"search_analyzer\":\"lowercase_keyword\"\n" +
                "          },\n" +
                "          \"pinyin_prefix\":{\n" +
                "            \"type\":\"completion\",\n" +
                "            \"analyzer\":\"pinyin_prefix\",\n" +
                "            \"search_analyzer\":\"lowercase_keyword\"\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }";
        restHighLevelClientService.createIndex("my_token",settings ,mappings);
    }

    @Test
    public void importAll() throws IOException {
        restHighLevelClientService.importAll("idx_item",true,itemService.getItemJson());
    }

    @Test
    public void testHanLP() {
        System.out.println(HanLP.segment("你好,欢迎使用HanLP !"));
    }
}
