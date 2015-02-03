package com.transling.es;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.node.Node;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchClient;
import com.github.tlrx.elasticsearch.test.annotations.ElasticsearchNode;
import com.github.tlrx.elasticsearch.test.support.junit.runners.ElasticsearchRunner;
import com.transling.api.beans.Bunch;
import com.transling.api.beans.TranslingImpl;
import com.transling.api.exceptions.ThereNoIndexException;
import static com.github.tlrx.elasticsearch.test.EsSetup.*;


@RunWith(ElasticsearchRunner.class)
public class EsTest {
	
	@ElasticsearchNode
	Node node;
	
	@ElasticsearchClient
	Client client;
	

	@Ignore
	@Test
	public void testSeparateWords () throws IOException, InterruptedException, ThereNoIndexException {
		TranslingImpl ti = new TranslingImpl (client);
		ti.removeUser ("stas");
		ti.removeUser ("anton");
		ti.addTranslation("stas", "main", "en", "mother", "ru", "мама");
		ti.addTranslation("stas", "main", "en", "mother myla", "ru", "мама мыла");
		ti.commit ();
		List<Bunch> bs = ti.findBunches("stas", "main", "en", "mother", 2);
		assertEquals (1, bs.size());
	}
	
	@Test
	public void testSeparateVocabularies () throws IOException, InterruptedException, ThereNoIndexException {
		TranslingImpl ti = new TranslingImpl (client);
		ti.removeUser    ("stas");
		ti.removeUser ("anton");
		ti.addTranslation("stas", "main", "en", "mother", "ru", "мама");
		ti.addTranslation("stas", "test", "en", "mother", "ru", "мама");
		ti.commit ();
		List<Bunch> bs = ti.findBunches("stas", "main", "en", "mother", 2);
		assertEquals (1, bs.size());
		bs = ti.findBunches("stas", "test", "en", "mother", 2);
		assertEquals (1, bs.size());
		bs = ti.findBunches("stas", null, "en", "mother", 2);
		
		ti.translate("stas", "main", "en", "mother", "ru");
		assertEquals (2, bs.size());
	}
	
	@Ignore
	@Test
	public void testSeparateUsers () throws IOException, InterruptedException, ThereNoIndexException {
		TranslingImpl ti = new TranslingImpl (client);
		ti.removeUser    ("stas");
		ti.removeUser    ("anton");
		
		ti.addTranslation("stas",  "main", "en", "mother", "ru", "мама");
		ti.addTranslation("anton", "main", "en", "mother", "ru", "мама");
		ti.commit ();
		List<Bunch> bs = ti.findBunches("stas", "main", "en", "mother", 2);
		assertEquals (1, bs.size());
		bs = ti.findBunches("anton", "main", "en", "mother", 2);
		assertEquals (1, bs.size());
		bs = ti.findBunches(null, "main", "en", "mother", 2);
		assertEquals (2, bs.size());
	}
	

}
