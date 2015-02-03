package com.transling.es;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.lucene.search.TermQuery;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

public class Test {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Settings settings = ImmutableSettings
				.settingsBuilder()
		        .put("cluster.name", "lingua")
		        .put("http.enabled", false)
		        .put("path.data", "D:/lingua-ws/data/")
		        .put("data", "D:/lingua-ws/data/")
		        .put("node.master", true)
		        .put("node.data", true)
		        .put("discovery.zen.ping.multicast.enabled", false)
		        .build();
		
		int k = 0;
		
		
		Node node = NodeBuilder.nodeBuilder().settings(settings).build();
		node.start();
		Client client = node.client();
		
		CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate("index");
		createIndexRequestBuilder.execute().actionGet();
		
		
		XContentBuilder data = XContentFactory.jsonBuilder()
			    .startObject()
			        .field("user", "kimchy")
			        .field("created",  new Date())
			        .field("modified", new Date())
			        .field("en", new String [] {"mother", "mom"})
			        .field("ru", new String [] {"mother", "mom"})
			    .endObject();
		
		IndexRequestBuilder ix = client.prepareIndex().setIndex("index").setType("type");
		ix.setSource(data).execute().actionGet();
		
		Thread.currentThread().sleep(2000L);;
		
		
		SearchRequestBuilder search = client.prepareSearch ("index");
		TermQueryBuilder tq = QueryBuilders.termQuery("en", "mother");
		search.setQuery(tq);
		SearchResponse resp = search.execute().actionGet();
		
//		CountDownLatch latch = new CountDownLatch(1);
//		latch.await();

		node.stop();
		
		k++;
	}

}
