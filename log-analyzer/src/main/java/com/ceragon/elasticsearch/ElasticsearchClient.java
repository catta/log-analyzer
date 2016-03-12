package com.ceragon.elasticsearch;

import java.io.IOException;
import java.net.InetAddress;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.client.transport.TransportClient.Builder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

public class ElasticsearchClient
{
    
    private static final String TYPE  = "external";
    private static final String INDEX = "customer";

    public static void main(String[] args) throws Exception
    {
        ElasticsearchClient ceragonClient = new ElasticsearchClient();
        
        // ceragonClient.nodeClient();
        
        Client client = ceragonClient.transportClient();
        
        String id = ceragonClient.addNewDocument(client);
        
        ceragonClient.getDocument(client, id);
        
        int millis = 5000;
        System.out.println("sleep " + millis);
        Thread.sleep(millis);

        ceragonClient.deleteDocument(client, id);

        client.close();
        
    }
    
    private void deleteDocument(Client client, String id)
    {
        DeleteResponse response = client.prepareDelete(INDEX, TYPE, id).get();
        if (response != null)
        {
            System.out.println(">> delete response " + response.getShardInfo());
        }
    }

    private void getDocument(Client client, String id)
    {
        GetResponse response = client.prepareGet(INDEX, TYPE, id).get();
        
        if (response != null && response.isExists())
        {
            System.out.println(">> get document " + response);
        }
    }

    private String addNewDocument(Client client) throws IOException
    {
        IndexRequestBuilder index = client.prepareIndex(INDEX, TYPE);
        XContentBuilder jsonBuilder = XContentFactory.jsonBuilder();
        jsonBuilder.startObject().field("name", "Catalin");
        index.setSource(jsonBuilder.endObject());
        
        IndexResponse indexResponse = index.get();
        // indexResponse.getId();
        // indexResponse.getIndex();
        // indexResponse.getShardInfo();
        // indexResponse.getType();
        // indexResponse.getVersion();
        System.out.println("add document indexResponse " + indexResponse);
        
        return indexResponse.getId();
    }

    private Client transportClient() throws IOException
    {
        Builder builder = TransportClient.builder();
        
        builder.settings(Settings.settingsBuilder().put("cluster.name", "elasticsearch"));
        InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300);
        System.out.println("transportAddress " + transportAddress);
        
        TransportClient client = builder.build().addTransportAddress(transportAddress);
        
        System.out.println("transport client " + client);
        
        MultiGetResponse multiGetResponse = client.prepareMultiGet().add(INDEX, TYPE, "1").get();
        
        for (MultiGetItemResponse itemResponse : multiGetResponse)
        {
            GetResponse response = itemResponse.getResponse();
            if (response.isExists())
            {
                String source = response.getSourceAsString();
                System.out.println("source " + source);
            }
        }

        return client;

    }

    private void nodeClient()
    {
        NodeBuilder nodeBuilder = NodeBuilder.nodeBuilder();
        nodeBuilder.settings(Settings.settingsBuilder().put("http.enabled", false)).client(true);// .local(true);
        Node node = nodeBuilder/*.clusterName("elasticseach")*/.node();
        
        Client client = node.client();
        
        AdminClient adminClient = client.admin();
        
        IndicesAdminClient indices = adminClient.indices();
        
        node.close();
    }
    
}
