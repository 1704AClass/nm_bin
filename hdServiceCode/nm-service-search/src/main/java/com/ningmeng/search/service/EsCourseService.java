package com.ningmeng.search.service;

import com.ningmeng.framework.domain.course.CoursePub;
import com.ningmeng.framework.domain.search.CourseSearchParam;
import com.ningmeng.framework.model.response.CommonCode;
import com.ningmeng.framework.model.response.QueryResponseResult;
import com.ningmeng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wangb on 2020/3/1.
 */
@Service
public class EsCourseService {
    @Value("${ningmeng.course.index}")
    private String es_index;
    @Value("${ningmeng.course.type}")
    private String es_type;
    @Value("${ningmeng.course.source_field}")
    private String source_field;
    @Autowired
    RestHighLevelClient restHighLevelClient;
    /**
     * 关键字查询
     * @param page
     * @param size
     * @param courseSearchParam
     * @return
     */
    public QueryResponseResult list(int page, int size, CourseSearchParam courseSearchParam) {
        //分页
        if(page<=0){
            page = 1;
        }
        if(size<=0){
            size = 10;
        }
        //设置索引
        SearchRequest searchRequest=new SearchRequest(es_index);
        //设置类型
        searchRequest.types(es_type);
        //创建查询条件对象
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        //设置分页的起始页码
        searchSourceBuilder.from((page-1)*size);
        //设置分页的显示数
        searchSourceBuilder.size(size);
        //boolean方式查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //source源字段过虑 维护字段根据，隔开
        String[] source_fields = source_field.split(",");
        //维护字段
        searchSourceBuilder.fetchSource(source_fields, new String[]{});
        //关键字
        if(StringUtils.isNotEmpty(courseSearchParam.getKeyword())){
            //匹配关键字  第一个参数是要查询的值，第二个是  要查询的字段
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "teachplan","description");
            //设置匹配占比
            multiMatchQueryBuilder.minimumShouldMatch("70%");
            //提升另个字段的Boost值
            multiMatchQueryBuilder.field("name",10);


            boolQueryBuilder.must(multiMatchQueryBuilder);
        }
        //过虑
        //一级分类
        if(StringUtils.isNotEmpty(courseSearchParam.getMt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }
        //二级分类
        if(StringUtils.isNotEmpty(courseSearchParam.getSt())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }
        //等级查询
        if(StringUtils.isNotEmpty(courseSearchParam.getGrade())){
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }

        //高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class='eslight'>");
        highlightBuilder.postTags("</font>");

        //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        //查询条件绑定高亮查询
        searchSourceBuilder.highlighter(highlightBuilder);

        //布尔查询
        searchSourceBuilder.query(boolQueryBuilder);
        //请求搜索
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        try {
            //执行搜索
            searchResponse = restHighLevelClient.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
            /*LOGGER.error("xuecheng search error..{}",e.getMessage());
            return new QueryResponseResult(CommonCode.SUCCESS,new QueryResult<CoursePub>());*/
        }
        //结果集处理
        SearchHits searchHits = searchResponse.getHits();
        //记录总数
        long totalHits = searchHits.getTotalHits();
        SearchHit[] hits = searchHits.getHits();
        //数据列表
        List<CoursePub> list = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            CoursePub coursePub = new CoursePub();
            //取出source
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //取出名称
            String name = (String) sourceAsMap.get("name");
            //取出高亮字段内容
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if(highlightFields!=null){
                HighlightField nameField = highlightFields.get("name");
                if(nameField!=null){
                    Text[] fragments = nameField.getFragments();
                    StringBuffer stringBuffer = new StringBuffer();
                    for (Text str : fragments) {
                        stringBuffer.append(str.string());
                    }
                    //如果有对应的高亮显示的值 就执行替换
                    name = stringBuffer.toString();
                }
            }
            coursePub.setName(name);
            //图片
            String pic = (String) sourceAsMap.get("pic");
            coursePub.setPic(pic);
            //价格
            Float price = null;
            try {
                if(sourceAsMap.get("price")!=null ){
                    price = Float.parseFloat((String) sourceAsMap.get("price"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } coursePub.setPrice(price);
            Float price_old = null;
            try {
                if(sourceAsMap.get("price_old")!=null ){
                    price_old = Float.parseFloat((String) sourceAsMap.get("price_old"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } coursePub.setPrice_old(price_old);
            list.add(coursePub);
        }
        QueryResult<CoursePub> queryResult = new QueryResult<>();
        queryResult.setList(list);
        queryResult.setTotal(totalHits);
        QueryResponseResult coursePubQueryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return coursePubQueryResponseResult;
    }
}
