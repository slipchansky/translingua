package com.transling.api.beans;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import org.elasticsearch.index.query.BaseQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.indices.TypeMissingException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transling.api.exceptions.ThereNoIndexException;
import com.transling.api.exceptions.UnknownSpaceException;
import com.transling.es.Defaults;

public class TranslingImpl {
	private static final String FIELD_VOCABULARY_ID = "vocabularyId";
	private static final String FIELD_USER_NAME = "userName";
	private static final String FIELD_MODIFIED = "modified";
	private static final String FIELD_CREATED = "created";
	private static final String FIELD_TRANSLATIONS = "translations";
	private final static Logger LOGGER = LoggerFactory.getLogger(TranslingImpl.class);
	
	private Client client;
	
	
	public TranslingImpl(Client client) {
		super();
		this.client = client;
	}

	private Date parseDate(String s) {
		SimpleDateFormat sdf = new SimpleDateFormat (Defaults.DATE_FORMAT);
		try {
			return sdf.parse(s);
		} catch (ParseException e) {
			LOGGER.debug("Can't parse to date: "+s, e);
		}
		return null;
	}
	
	private SearchRequestBuilder prepareSearch(String space, String vocabularyId, String lang, String word) {
		SearchRequestBuilder search = client.prepareSearch (Defaults.VOCABULARY_INDEX);
		if (!StringUtils.isEmpty(space)) {
			search.setTypes(space);
		}
		
		BaseQueryBuilder tq = QueryBuilders.termQuery(FIELD_TRANSLATIONS+'.'+lang, word);
		if (!StringUtils.isEmpty(vocabularyId)) {
			TermFilterBuilder tf = FilterBuilders.termFilter(FIELD_VOCABULARY_ID, vocabularyId);
			tq = QueryBuilders.filteredQuery(tq, tf);
		}
		search.setQuery(tq);
		return search;
	}
	
	public List<Bunch> findBunches (String space, String vocabularyId, String lang, String word, int n) throws ThereNoIndexException {
		SearchRequestBuilder search = prepareSearch(space, vocabularyId, lang, word);
		
		search.setFrom(0).setSize(n);
		SearchResponse resp = null;
		try {
		    resp = search.execute().actionGet();
		} catch (IndexMissingException e) {
			throw new ThereNoIndexException ();
		}
		
		SearchHits foundHits = resp.getHits();
		SearchHit[] hits = foundHits.getHits();
		List<Bunch> result = new ArrayList<Bunch> (hits.length);
		
		
		for (SearchHit hit : hits) {
			Map<String, Object> source = hit.getSource();
			Map<String, List<String>> translations = (Map<String, List<String>>) source.get(FIELD_TRANSLATIONS);
			Date created = parseDate((String)source.get (FIELD_CREATED));
			Date modified = parseDate ((String)source.get (FIELD_MODIFIED));
			String vocabulary = (String)source.get (FIELD_VOCABULARY_ID);
			String userName = (String)source.get (FIELD_USER_NAME);
			Bunch item = new Bunch (hit.getId(), hit.getType());
			item.setUserName(userName);
			item.setVocabularyId (vocabulary);
			item.setCreated(created);
			item.setModified(modified);
			item.setTranslations(new Translations (translations));
			result.add(item);
		}
		return result;
	}


	public Bunch findBunch (String space, String vacabularyId, String lang, String word) throws ThereNoIndexException {
		List<Bunch> found = findBunches(space, vacabularyId, lang, word, 1);
		if (found.size()==0) return null;
		return found.get (0);
	}
	


	private void createIndex(String space) throws IOException {
		if (StringUtils.isEmpty(space)) {
			throw new UnknownSpaceException ();
		} 
		CreateIndexRequestBuilder createIndexRequest = client.admin().indices().prepareCreate(Defaults.VOCABULARY_INDEX);
		createIndexRequest.addMapping("_default_", getMapping (space));
		try {
		createIndexRequest.execute().actionGet();
		} catch (Throwable e) {
			e.printStackTrace();
			int k=0;
			
			k++;
		}
		
		
	}
	
	
	private XContentBuilder getMapping(String space) throws IOException {
		XContentBuilder mapping = XContentFactory.jsonBuilder()
		.startObject()
		   .startObject("_default_")
		      .startObject("properties")
		             .startObject(FIELD_CREATED).field("type", "date").field("format", Defaults.DATE_FORMAT).endObject()
		             .startObject(FIELD_MODIFIED).field("type", "date").field("format", Defaults.DATE_FORMAT).endObject();
		             
		             mapping = addLanguagesMapping (mapping);
		             
		             mapping.endObject()
		      .endObject()
		 .endObject();
		 return mapping;            
	}


	private XContentBuilder addLanguagesMapping(XContentBuilder mapping) throws IOException {
		return mapping
				 .startObject("translations.en").field("type", "string").field("index", "not_analyzed").endObject()
	             .startObject("translations.ru").field("type", "string").field("index", "not_analyzed").endObject()
	             .startObject("translations.ua").field("type", "string").field("index", "not_analyzed").endObject()
	             .startObject("translations.de").field("type", "string").field("index", "not_analyzed").endObject()
	             .startObject("translations.be").field("type", "string").field("index", "not_analyzed").endObject()
	             .startObject("translations.fr").field("type", "string").field("index", "not_analyzed").endObject()
	             .startObject("translations.it").field("type", "string").field("index", "not_analyzed").endObject()
	             .startObject("translations.sp").field("type", "string").field("index", "not_analyzed").endObject();
	}

	private void updateBunch(Bunch bunch) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat (Defaults.DATE_FORMAT);
		XContentBuilder data = XContentFactory.jsonBuilder()
			    .startObject()
			        .field(FIELD_CREATED,  sdf.format(bunch.getCreated()))
			        .field(FIELD_MODIFIED, sdf.format(bunch.getModified()))
			        .field(FIELD_USER_NAME, bunch.getUserName()) 
			        .field(FIELD_VOCABULARY_ID, bunch.getVocabularyId()) 
			        .field(FIELD_TRANSLATIONS, bunch.getTranslations ())
			    .endObject();
		
		
		
		IndexRequestBuilder indexRequest = client.prepareIndex().setIndex(Defaults.VOCABULARY_INDEX).setType(bunch.getUserName());
		if (!StringUtils.isEmpty(bunch.getId())) {
			indexRequest.setId(bunch.getId());
		}
		indexRequest.setSource(data);
		try {
		   IndexResponse response = indexRequest.execute().actionGet();
		   bunch.setId(response.getId());
		} catch (Exception e) {
			LOGGER.debug("Bunch update error", e);
		}
	}
	

	public void addTranslations (String space, String vocabularyId, String langFrom, String wordFrom, String langTo, Words wordsTo) throws IOException  {
		Bunch bunch = null;
		try {
			bunch = findBunch (space, vocabularyId, langFrom, wordFrom);
		} catch (ThereNoIndexException e) {
			createIndex (space);
		}
		
		if (bunch==null) {
			if (StringUtils.isEmpty(space)) {
				throw new UnknownSpaceException ();
			}
			
			if (StringUtils.isEmpty(vocabularyId)) {
				throw new UnknownSpaceException ();
			}
			
			bunch = new Bunch ();
			bunch.setUserName(space);
			bunch.setVocabularyId(vocabularyId);
		}
		bunch.getTranslations().put(langFrom, new Words (wordFrom));
		bunch.getTranslations().put(langTo,   new Words (wordsTo));
		bunch.setModified(new Date ());
		updateBunch (bunch);
	}
	

	public void addTranslation (String space, String vocabularyId, String langFrom, String wordFrom, String langTo, String wordTo) throws IOException {
		addTranslations(space, vocabularyId, langFrom, wordFrom, langTo, new Words (wordTo));
	}
	
	public void commit () {
		client.admin().indices().prepareFlush(Defaults.VOCABULARY_INDEX).execute().actionGet();
	}

	public void removeUser(String space) {
		if (StringUtils.isEmpty(space)) {
			throw new UnknownSpaceException();
		}
		try {
		 client.admin().indices().prepareDeleteMapping(Defaults.VOCABULARY_INDEX).setType(space).execute().actionGet();
		 commit ();
		} 
		catch (IndexMissingException e) {
			LOGGER.debug("There is no index: "+Defaults.VOCABULARY_INDEX);
		} catch (TypeMissingException t) {
			LOGGER.debug("There is no user: "+space);
		}
	}
	
	public WordTranslation translate (String space, String vocabularyId, String langFrom, String wordFrom, String langTo) {
		
		SearchRequestBuilder search = prepareSearch(space, vocabularyId, langFrom, wordFrom);
		TermsBuilder agg = AggregationBuilders.terms(FIELD_TRANSLATIONS).field(FIELD_TRANSLATIONS+'.'+langTo);
		search.setSize(0);
		search.addAggregation(agg);
		SearchResponse response = search.execute().actionGet();
		StringTerms aggregation = response.getAggregations().get(FIELD_TRANSLATIONS);
		Collection<Bucket> buckets = aggregation.getBuckets();
		
		WordTranslation wordTranslation = new WordTranslation ();
		wordTranslation.setLangFrom(langFrom);
		wordTranslation.setLangTo(langTo);
		wordTranslation.setUserName(space);
		wordTranslation.setVocabularyId(vocabularyId);
		wordTranslation.setWord(wordFrom);
		
		for (Bucket b : buckets) {
			String word = b.getKey();
			long count = b.getDocCount();
			wordTranslation.getTranslations().add(new WordFacet (word, count));
		}
		return wordTranslation;
	}

}
