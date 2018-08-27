package com.datafiniti.importer;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.HashMap;

public class ESClient {
    private static boolean connected;
    private static RestHighLevelClient client;

    public static synchronized void connect() {
        if (connected) {
            return;
        }

        client = new RestHighLevelClient(
            RestClient.builder(
                new HttpHost("elasticsearch", 9200, "http")
            )
        );

        connected = true;
    }

    public static void insert(String record) throws IOException {
        checkConnected();

        IndexRequest indexRequest = new IndexRequest("records", "all")
            .source(record, XContentType.JSON);

        client.index(indexRequest);
    }

    public static void setup() throws IOException {
        checkConnected();
        GetIndexRequest request = new GetIndexRequest()
            .indices("records");

        boolean exists = client
            .indices()
            .exists(request, RequestOptions.DEFAULT);

        if (exists) {
            System.out.println("deleting records index");

            DeleteIndexRequest deleteRequest = new DeleteIndexRequest()
                .indices("records");

            client.indices().delete(deleteRequest);
        }

        System.out.println("creating records index");

        Settings settings = Settings
            .builder()
            .put("index.number_of_shards", 1)
            .put("index.number_of_replicas", 0)
            .build();

        CreateIndexRequest createRequest = new CreateIndexRequest("records")
            .settings(settings)
            .mapping("all", new HashMap());

        client.indices().create(createRequest);

        System.out.println("records index created");
    }

    private static void checkConnected() {
        if (!connected) {
            throw new RuntimeException("client not connected. Call connect first.");
        }
    }

    private static synchronized void disconnect() throws IOException {
        if (!connected) {
            return;
        }

        client.close();
        connected = false;
    }
}
