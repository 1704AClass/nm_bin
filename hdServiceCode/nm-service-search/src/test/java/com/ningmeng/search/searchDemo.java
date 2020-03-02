package com.ningmeng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wangb on 2020/2/26.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class searchDemo {
    @Autowired
    RestHighLevelClient client;
    //添加文档
    @Test
    public void testAddDoc() throws IOException {
        //准备json数据
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("id","666");
        jsonMap.put("name", "spring cloud实战");
        jsonMap.put("description", "本课程主要从四个章节进行讲解： 1.微服务架构入门 2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);
        //索引请求对象
        IndexRequest indexRequest = new IndexRequest("nm_course","doc");
        //指定索引文档内容
        indexRequest.source(jsonMap);
        //索引响应对象
        IndexResponse indexResponse = client.index(indexRequest);
        //获取响应结果
        DocWriteResponse.Result result = indexResponse.getResult();
        System.out.println("---------------------------------------------------"+result);
    }
    //查询文档
    @Test
    public void getDoc() throws IOException {
        GetRequest getRequest = new GetRequest("nm_course", "doc", "3");
        GetResponse getResponse = client.get(getRequest);
        boolean exists = getResponse.isExists();
        Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
        System.out.println("++++++++++++++++++++++++++++++"+sourceAsMap);
    }
    //更新文档
    @Test
    public void updateDoc() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("nm_course", "doc", "8jb2gXABTGFvrKltQNDm");
        Map<String, String> map = new HashMap<>();
        map.put("name", "spring cloud实战6666");
        updateRequest.doc(map);
        UpdateResponse update = client.update(updateRequest);
        RestStatus status = update.status();
        System.out.println(status);
    }
    //根据id删除文档
    @Test
    public void testDelDoc() throws IOException {
        //删除文档id
        String id = "8jb2gXABTGFvrKltQNDm";
        //删除索引请求对象
        DeleteRequest deleteRequest = new DeleteRequest("nm_course","doc",id);
        //响应对象
        DeleteResponse deleteResponse = client.delete(deleteRequest);
        //获取响应结果
        DocWriteResponse.Result result = deleteResponse.getResult();
        System.out.println(result);
    }
    //dsl
    @Test
    public void testSearchAll() throws IOException {
        SearchRequest searchRequest = new SearchRequest("nm_course");
        searchRequest.types("doc");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println(name);
            System.out.println(studymodel);
            System.out.println(description);
        }
    }
    //分页
    @Test
    public void getDslByPage() throws IOException {
        int page=1;
        int size=2;
        SearchRequest searchRequest = new SearchRequest("nm_course");
        searchRequest.types("doc");


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //分页查询，设置起始下标，从0开始
        searchSourceBuilder.from((page-1)*size);
        //每页显示个数
        searchSourceBuilder.size(size);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());


        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("条数:"+totalHits);
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("name:"+name);
            System.out.println("studymodel:"+studymodel);
            System.out.println("description:"+description);
        }
    }
    //Term Query为精确查询，在搜索时会整体匹配关键字，不再将关键字分词
    @Test
    public void getTermDsl() throws IOException {
        SearchRequest searchRequest = new SearchRequest("nm_course");
        searchRequest.types("doc");


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.termQuery("name","spring"));


        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("条数:"+totalHits);
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("name:"+name);
            System.out.println("studymodel:"+studymodel);
            System.out.println("description:"+description);
        }
    }
    //根据id精确匹配  QueryBuilders.termsQuery  这个必须有s没s出不来数据
    @Test
    public void getDslByIds() throws IOException {
        SearchRequest searchRequest = new SearchRequest("nm_course");
        searchRequest.types("doc");

        String[] split = new String[]{"1","2"};
        List<String> idList = Arrays.asList(split);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.termsQuery("_id",idList));


        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("条数:"+totalHits);
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("name:"+name);
            System.out.println("studymodel:"+studymodel);
            System.out.println("description:"+description);
        }
    }
    //match Query  or  and
    @Test
    public void testMatchQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("nm_course");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.matchQuery("description", "spring开发").operator(Operator.AND));


        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("条数:"+totalHits);
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("name:"+name);
            System.out.println("studymodel:"+studymodel);
            System.out.println("description:"+description);
        }
    }
    //multi Query 50%  玩的权重  权重越高的字段  数据包含就会在前面
    @Test
    public void testMultiQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("nm_course");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //可以查询多个字段，根据权重影响得分以及查询的顺序
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("spring css","name", "description").minimumShouldMatch("50%").field("name",10));

        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("条数:"+totalHits);
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("name:"+name);
            System.out.println("studymodel:"+studymodel);
            System.out.println("description:"+description);
        }
    }
    //布尔查询  实现将多个查询组合起来。
    @Test
    public void testBoolean() throws IOException {
        SearchRequest searchRequest = new SearchRequest("nm_course");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder multiMatchQueryBuilder= QueryBuilders.multiMatchQuery("spring css","name", "description").minimumShouldMatch("50%").field("name",10);
        //TermQuery
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");
        //可以查询多个字段，根据权重影响得分以及查询的顺序
        BoolQueryBuilder booleanClauses=QueryBuilders.boolQuery();
        //must==and   should==or   must_not==not
        booleanClauses.should(multiMatchQueryBuilder);
        booleanClauses.should(termQueryBuilder);

        searchSourceBuilder.query(booleanClauses);

        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("条数:"+totalHits);
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("name:"+name);
            System.out.println("studymodel:"+studymodel);
            System.out.println("description:"+description);
        }
    }
    //布尔查询的过滤器+排序
    @Test
    public void testBooleanFilter() throws IOException {
        SearchRequest searchRequest = new SearchRequest("nm_course");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder multiMatchQueryBuilder= QueryBuilders.multiMatchQuery("spring css","name", "description").minimumShouldMatch("50%").field("name",10);
        //可以查询多个字段，根据权重影响得分以及查询的顺序
        BoolQueryBuilder booleanClauses=QueryBuilders.boolQuery();
        //must==and   should==or   must_not==not
        booleanClauses.should(multiMatchQueryBuilder);
        //过虑  .filter相当于拼接条件   分类等
        booleanClauses.filter(QueryBuilders.termQuery("studymodel", "201001"));
        booleanClauses.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        searchSourceBuilder.query(booleanClauses);
        searchSourceBuilder.sort("price", SortOrder.DESC);
        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("条数:"+totalHits);
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            System.out.println("name:"+name);
            System.out.println("studymodel:"+studymodel);
            System.out.println("description:"+description);
        }
    }
    //高亮显示
    @Test
    public void testHighlight() throws IOException {
        SearchRequest searchRequest = new SearchRequest("nm_course");
        searchRequest.types("doc");

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MultiMatchQueryBuilder multiMatchQueryBuilder= QueryBuilders.multiMatchQuery("spring css","name", "description").minimumShouldMatch("50%").field("name",10);
        //可以查询多个字段，根据权重影响得分以及查询的顺序
        BoolQueryBuilder booleanClauses=QueryBuilders.boolQuery();
        //must==and   should==or   must_not==not
        booleanClauses.should(multiMatchQueryBuilder);
        //过虑  .filter相当于拼接条件   分类等
        booleanClauses.filter(QueryBuilders.termQuery("studymodel", "201001"));
        booleanClauses.filter(QueryBuilders.rangeQuery("price").gte(60).lte(100));
        searchSourceBuilder.query(booleanClauses);
        searchSourceBuilder.sort("price", SortOrder.DESC);

        //高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<tag>");//设置前缀
        highlightBuilder.postTags("</tag>");//设置后缀
        // 设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
       // highlightBuilder.fields().add(new HighlightBuilder.Field("description"));
        searchSourceBuilder.highlighter(highlightBuilder);

        //source源字段过虑
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel"}, new String[]{});
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("条数:"+totalHits);
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            String index = hit.getIndex();
            String type = hit.getType();
            String id = hit.getId();
            float score = hit.getScore();
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            //取出高亮字段内容
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(highlightFields!=null){
                HighlightField nameField = highlightFields.get("name");
                if(nameField!=null){
                    Text[] fragments = nameField.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text str : fragments) {
                        stringBuffer.append(str.string());
                    } name = stringBuffer.toString();
                }
            }
            System.out.println("name:"+name);
            System.out.println("studymodel:"+studymodel);
            System.out.println("description:"+description);
        }
    }
}
