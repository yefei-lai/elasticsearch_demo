package com.example.es_demo;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.search.SimpleQueryStringQueryParser;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
public class DocumentsTests {

    @Resource
    private RestHighLevelClient client;


    /**
     * 创建索引
     * @throws IOException
     */
    @Test
    public void createIndex() throws IOException {
        //创建索引Request
        CreateIndexRequest request = new CreateIndexRequest("music");
        //设置索引分片和备份
        request.settings(Settings.builder()
            .put("index.number_of_shards", 3)
            .put("index.number_of_replicas", 2));
        //设置mapping映射
        request.mapping(
                "{\n" +
                        "  \"properties\":\n" +
                        "  {\n" +
                        "    \"mid\": {\n" +
                        "      \"type\": \"long\"\n" +
                        "    },\n" +
                        "    \"name\":{\n" +
                        "      \"type\": \"text\"\n" +
                        "    },\n" +
                        "    \"type\":{\n" +
                        "      \"type\": \"keyword\"\n" +
                        "    },\n" +
                        "    \"singer\":{\n" +
                        "      \"type\": \"text\",\n" +
                        "      \"fields\":{\n" +
                        "        \"keyword\":{\n" +
                        "          \"type\": \"keyword\"\n" +
                        "        }\n" +
                        "      }\n" +
                        "    },\n" +
                        "    \"country\":{\n" +
                        "      \"type\": \"keyword\"\n" +
                        "    },\n" +
                        "    \"date\": {\n" +
                        "      \"type\": \"date\"\n" +
                        "    }\n" +
                        "  }\n" +
                        "}",
                        XContentType.JSON);
        //设置别名
        request.alias(new Alias("music_alias"));
        //发送请求
        //同步
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        boolean acknowledged = response.isAcknowledged();
        boolean shardsAcknowledged = response.isShardsAcknowledged();
        System.out.println("acknowledged:"+acknowledged);
        System.out.println("shardsAcknowledged:"+shardsAcknowledged);
    }

    /**
     * 删除索引
     * @throws IOException
     */
    @Test
    public void deleteIndex() throws IOException{
        //创建删除索引（指向索引：music）
        DeleteIndexRequest request = new DeleteIndexRequest("music");
        //发送请求
        //同步
        AcknowledgedResponse deleteIndexResponse = client.indices().delete(request,RequestOptions.DEFAULT);
        System.out.println(deleteIndexResponse.isAcknowledged());
    }

    /**
     * 单个插入数据
     * @throws IOException
     */
    @Test
    public void indexIndex() throws IOException{
        //创建请求
        IndexRequest request = new IndexRequest("music");
        //设置type 对应 json：post music/_doc/1{...}
        request.type("_doc");
        request.id("4");
        //方式一：直接写json
//        String jsonString =
//                "{\n" +
//                "  \"mid\": 2,\n" +
//                "  \"name\" : \"Sunflower\",\n" +
//                "  \"type\" : \"rap\",\n" +
//                "  \"singer\" : \"Post Malone\",\n" +
//                "  \"country\" : \"America\",\n" +
//                "  \"date\" : \"2018\"\n" +
//                "}";
//        request.source(jsonString, XContentType.JSON);
        //方式二：以map对象来表示文档信息
//        Map<String, Object> jsonMap = new HashMap<>();
//        jsonMap.put("mid", 2);
//        jsonMap.put("name", "Better Now");
//        jsonMap.put("type", "rap");
//        jsonMap.put("singer", "Post Malone");
//        jsonMap.put("country", "America");
//        jsonMap.put("date", "2018");
//        request.source(jsonMap);
        //方式三：用XContentBuilder来构建文档
//        XContentBuilder builder = XContentFactory.jsonBuilder();
//        builder.startObject();
//        {
//            builder.field("mid", 3);
//            builder.field("name", "Remember");
//            builder.field("type", "pop");
//            builder.field("singer","KATIE");
//            builder.field("country","Korea");
//            builder.field("date","2018");
//        }
//        builder.endObject();
//        request.source(builder);
        //方式四：直接用key-value设置
        request.source("mid", 4,
                        "name", "Pray",
                        "type","pop",
                        "singer","Sam Smith",
                        "country","England",
                        "date","2017");
        //发送请求
        //同步
        IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 删除索引数据(根据_id)
     * @throws IOException
     */
    @Test
    public void deleteDocumentIndex() throws IOException{
        //创建删除请求
        DeleteRequest request = new DeleteRequest("music", "_doc", "2");
        //发送请求
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
    }

    /**
     * 通过id获取单个数据信息
     * @throws IOException
     */
    @Test
    public void getIndex() throws IOException{
        GetRequest getRequest = new GetRequest("music", "_doc", "4");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        if (getResponse.isExists()){
            String sourceAsString = getResponse.getSourceAsString();
            Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
            System.out.println(sourceAsString);
            System.out.println(sourceAsMap);
        }
    }

    /**
     * 根据id更新数据
     * @throws IOException
     */
    @Test
    public void UpdateIndex() throws IOException {
        UpdateRequest request = new UpdateRequest("music", "_doc", "4");
        //通过script更新
        Map<String, Object> parameters = Collections.singletonMap("date", "2017");
        Script inline = new Script(ScriptType.INLINE, "painless", "ctx._source.date = params.date", parameters);
        request.script(inline);
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
    }

    /**
     * 批量操作
     * @throws IOException
     */
    @Test
    public void BulkIndex() throws IOException{
        BulkRequest request = new BulkRequest();
        //批量插入
        request.add(new IndexRequest("music", "_doc", "5")
                .source(XContentType.JSON,
                        "mid", "5",
                        "name", "Attention",
                        "type", "pop",
                        "singer", "Charlie Puth",
                        "country", "America",
                        "date", "2017"));
        request.add(new IndexRequest("music", "_doc", "6")
                .source(XContentType.JSON,
                        "mid", "6",
                        "name", "ghostin",
                        "type", "pop",
                        "singer", "Ariana Grande",
                        "country", "America",
                        "date", "2019"));
        BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
        for (BulkItemResponse bulkItemResponse : response){
            DocWriteResponse writeResponse = bulkItemResponse.getResponse();
            if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.INDEX
                || bulkItemResponse.getOpType() == DeleteRequest.OpType.CREATE){
                IndexResponse indexResponse = (IndexResponse) writeResponse;
                //TODO 批量新增成功的处理
            }else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.UPDATE){
                UpdateResponse updateResponse = (UpdateResponse) writeResponse;
                //TODO 批量修改成功的处理
            }else if (bulkItemResponse.getOpType() == DocWriteRequest.OpType.DELETE){
                //TODO 批量删除成功的处理
            }
        }

    }
}
