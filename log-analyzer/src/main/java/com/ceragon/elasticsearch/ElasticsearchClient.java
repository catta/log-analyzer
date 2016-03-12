package com.ceragon.elasticsearch;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.client.transport.TransportClient.Builder;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

public class ElasticsearchClient
{
    
    public static void main(String[] args)
    {
        ElasticsearchClient ceragonClient = new ElasticsearchClient();
        
        // ceragonClient.nodeClient();
        
        ceragonClient.transportClient();
    }
    
    private void transportClient()
    {
        try
        {
            Builder builder = TransportClient.builder();
            
            builder.settings(Settings.settingsBuilder().put("cluster.name", "elasticsearch"));
            InetSocketTransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300);
            System.out.println("transportAddress " + transportAddress);

            TransportClient client = builder.build().addTransportAddress(transportAddress);

            System.out.println("transport client " + client);
            
            MultiGetResponse multiGetResponse = client.prepareMultiGet().add("customer", "external", "1").get();
            
            for (MultiGetItemResponse itemResponse : multiGetResponse)
            {
                GetResponse response = itemResponse.getResponse();
                if (response.isExists())
                {
                    String source = response.getSourceAsString();
                    System.out.println("source " + source);
                }
            }
            
            client.close();
            
        }
        catch (UnknownHostException e)
        {
            e.printStackTrace();
        }
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
