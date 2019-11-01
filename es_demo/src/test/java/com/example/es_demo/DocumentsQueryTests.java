package com.example.es_demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.example.es_demo.model.MovieModel;
import org.apache.http.HttpEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.ml.job.results.Bucket;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JsonbTester;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@SpringBootTest
class DocumentsQueryTests {

    @Resource
    private RestHighLevelClient client;

    @Test
    void contextLoads() {
    }


    /**
     * 查询所有
     * @throws Exception
     */
    @Test
    public void searchAll() throws Exception{

        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        System.out.println(searchByQueryBuilder(client, queryBuilder));
    }

    /**
     * 匹配查询
     */
    @Test
    public void matchQuery(){
        try {
            QueryBuilder queryBuilder = QueryBuilders.matchQuery("director", "James-Cameron");
            System.out.println(searchByQueryBuilder(client, queryBuilder));
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 多个匹配查询
     */
    @Test
    public void multiMatchQuery(){
        try {
            //查询 contry type字段都包含ce 的数据
            QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("romance", "counrty", "type").operator(Operator.OR);
            System.out.println(searchByQueryBuilder(client, queryBuilder));
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 字段匹配查询
     */
    @Test
    public void termQuery(){
        try {
            //查询 type 为 "romance"或"action" 的所有数据
            QueryBuilder queryBuilder = QueryBuilders.termsQuery("type", "romance", "action");
            System.out.println(searchByQueryBuilder(client, queryBuilder));
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 布尔查询
     */
    @Test
    public void boolQuery(){
        try {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            QueryBuilder queryBuilder_name = QueryBuilders.termQuery("name", "leon");
//            QueryBuilder queryBuilder_type = QueryBuilders.termQuery("type", "romance");
            //查询name 为 "leon" 或者 type 为 "romance"的数据
//            boolQueryBuilder.should(queryBuilder_name);
//            boolQueryBuilder.should(queryBuilder_type);
            QueryBuilder queryBuilder_type = QueryBuilders.termQuery("type", "action");
            //查询name为"leon"并且type为"action"的数据
//            boolQueryBuilder.must(queryBuilder_name);
//            boolQueryBuilder.must(queryBuilder_type);
            //查询date>1997-01-01的数据
//            RangeQueryBuilder rangeQueryBuilder =QueryBuilders.rangeQuery("date").gt("1997-01-01");
//            boolQueryBuilder.must(rangeQueryBuilder);
            //id范围查询
            int[] ids = {1, 2, 3};
            TermsQueryBuilder termsQueryBuilder = QueryBuilders.termsQuery("_id", ids);
            boolQueryBuilder.must(termsQueryBuilder);
            System.out.println(searchByQueryBuilder(client, boolQueryBuilder));
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * String 搜索
     */
    @Test
    public void stringQuery(){
        try {
            //查询 含有 France的数据
            QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("France");
            System.out.println(searchByQueryBuilder(client, queryBuilder));
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 排序
     * @throws IOException
     */
    @Test
    public void sort() throws IOException{
        SearchRequest searchRequest = new SearchRequest("movie");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(0).size(10);
        //设置一个可选的超时时间，用于可控制搜索允许的时间
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //将数据按date排序
        FieldSortBuilder sortBuilder = SortBuilders.fieldSort("date").order(SortOrder.DESC);
        searchSourceBuilder.sort(sortBuilder);
        //指定返回字段
//        String[] includeFields = {"name", "director"};
//        String[] excludeFields = {"date"};
//        searchSourceBuilder.fetchSource(includeFields, excludeFields);
        //实现字段高亮显示
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        HighlightBuilder.Field highlightName = new HighlightBuilder.Field("name");
//        highlightBuilder.field(highlightName);
//        searchSourceBuilder.highlighter(highlightBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        //获取热点数据
        SearchHits searchHits = searchResponse.getHits();
        SearchHit[] hits = searchHits.getHits();
        //将结果装进对象list中
        List<MovieModel> list = new ArrayList<>();
        for (SearchHit searchHit : hits){
            String sourceAsString  = searchHit.getSourceAsString();
            list.add(JSON.parseObject(sourceAsString, new TypeReference<MovieModel>(){}));
        }
        System.out.println(list);
        client.close();
    }

    /**
     * 聚合查询
     * @throws IOException
     */
    @Test
    public void agg() throws IOException{
//        SearchRequest searchRequest = new SearchRequest("movie");
        SearchRequest searchRequest = new SearchRequest("people");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //按type分组
        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("by_sex").field("sex.keyword");
        searchSourceBuilder.aggregation(aggregationBuilder);

        //求平均值和总和(按name分组)
//        //对应的json串
//        String json_es = "GET people/_search\n" +
//                            "{\n" +
//                            "  \"aggs\": {\n" +
//                            "    \"by_sex\":{\n" +
//                            "      \"terms\": {\n" +
//                            "        \"field\": \"sex.keyword\"\n" +
//                            "      },\n" +
//                            "      \"aggs\": {\n" +
//                            "        \"avg_age\": {\n" +
//                            "          \"avg\": {\n" +
//                            "            \"field\": \"age\"\n" +
//                            "          }\n" +
//                            "        },\n" +
//                            "        \"sum_age\":{\n" +
//                            "          \"sum\": {\n" +
//                            "            \"field\": \"age\"\n" +
//                            "          }\n" +
//                            "        }\n" +
//                            "      }\n" +
//                            "    }\n" +
//                            "  }\n" +
//                            "}";
//        TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("by_sex").field("sex.keyword");
//        AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg("avg_age").field("age");
//        SumAggregationBuilder sumAggregationBuilder = AggregationBuilders.sum("sum_age").field("age");
//        searchSourceBuilder.aggregation(aggregationBuilder.subAggregation(avgAggregationBuilder).subAggregation(sumAggregationBuilder));


        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        Aggregations aggregations = searchResponse.getAggregations();
        //获取按type分组聚合结果
        Terms byTypeAggregation = aggregations.get("by_sex");
        for (Terms.Bucket bucket : byTypeAggregation.getBuckets()){
            System.out.println("key:"+bucket.getKey());
            System.out.println("docCount:"+bucket.getDocCount());
        }


        //获取求平均值和总和聚合结果
//        Aggregations aggregations = searchResponse.getAggregations();
//        Terms byAgeAggregation = aggregations.get("by_sex");
//        for (Terms.Bucket bucket : byAgeAggregation.getBuckets()){
//            System.out.println("key:"+bucket.getKey());
//            System.out.println("docCount:"+bucket.getDocCount());
//            Avg avg = bucket.getAggregations().get("avg_age");
//            System.out.println("avg_age:"+avg.getValue());
//            Sum sum = bucket.getAggregations().get("sum_age");
//            System.out.println("sum:"+sum.getValue());
//        }

        client.close();
    }

    private static List searchByQueryBuilder(RestHighLevelClient client, QueryBuilder queryBuilder) throws IOException {
        //创建movie索引的搜索请求
        SearchRequest searchRequest = new SearchRequest("movie");
        //创建搜索文档内容对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        //设置查询的起始索引位置和数量，以下表示从第1条开始，共返回1000条文档数据
        searchSourceBuilder.from(0).size(1000);
        searchRequest.source(searchSourceBuilder);
        //根据搜索请求返回结果
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        //获取热点数据
        SearchHits searchHits = searchResponse.getHits();
        SearchHit[] hits = searchHits.getHits();
        //将结果装进对象list中
        List<MovieModel> list = new ArrayList<>();
        for (SearchHit searchHit : hits){
            String sourceAsString  = searchHit.getSourceAsString();
            list.add(JSON.parseObject(sourceAsString, new TypeReference<MovieModel>(){}));
        }
        return list;
    }

}
