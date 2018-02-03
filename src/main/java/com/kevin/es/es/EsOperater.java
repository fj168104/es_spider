package com.kevin.es.es;

import com.kevin.es.domain.BankData;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * DELETE ALL:  curl -X DELETE 'http://localhost:9200/mr_index'
 * GET	  ALL:  curl http://localhost:9200/mr_index/_search
 *
 */
public class EsOperater {

	private TransportClient client;

	public void open(){
		EsUtils.closeClient();
		client = EsUtils.getEsClient();
	}

	public void close(){
		EsUtils.closeClient();
	}

	//增加
	public void insert(BankData bankData){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Id", bankData.getId());
		map.put("name", bankData.getName());
		map.put("oriUrl", bankData.getOriUrl());
		map.put("issueDate", bankData.getIssueDate());
		map.put("htmlStr", bankData.getHtmlStr());
		map.put("partyPerson", bankData.getPartyPerson());
		map.put("bankName", bankData.getBankName());
		map.put("holderName", bankData.getHolderName());
		map.put("mainCase", bankData.getMainCase());
		map.put("according", bankData.getAccording());
		map.put("decision", bankData.getDecision());
		map.put("orgName", bankData.getOrgName());

		IndexResponse indexResponse = client.prepareIndex()
				.setIndex(EsUtils.getIndexName())
				.setType(EsUtils.getTypeName())
				//.setSource("{\"prodId\":1,\"prodName\":\"ipad5\",\"prodDesc\":\"比你想的更强大\",\"catId\":1}")
				.setSource(map)
				.setId(Long.toString(bankData.getId()))
				.execute()
				.actionGet();
		System.out.println("插入成功, isCreated="+indexResponse.getResult().toString());
	}
	

	//查询
	public BankData queryById(String dataId){
		GetResponse getResponse = client.prepareGet()
				.setIndex(EsUtils.getIndexName())
				.setType(EsUtils.getTypeName())
				.setId(dataId)
				.execute()
				.actionGet();
		System.out.println("get="+getResponse.getSourceAsString());
		return convertToBankData(getResponse.getSourceAsMap());
	}
	
	//搜索
	public List<BankData> search(String sStr){
		List<BankData> bankDataList = new ArrayList<BankData>(50);
		if(sStr == null || sStr.equals("")) return bankDataList;
		QueryBuilder query = QueryBuilders.queryStringQuery(sStr);
		SearchResponse searchResponse = client.prepareSearch( EsUtils.getIndexName() )
				.setQuery(query)
				.setFrom(0).setSize(50)
				.execute()
				.actionGet();
		//SearchHits是SearchHit的复数形式，表示这个是一个列表
		SearchHits shs = searchResponse.getHits();
		if(shs == null || shs.getTotalHits() == 0)
			return bankDataList;

		for(SearchHit hit : shs){
			System.out.println( hit.getSource() );
			if(hit.getScore()/shs.getMaxScore() < 0.8f) continue;
			Map<String , Object> map = hit.getSource();
			map.put("score", hit.getScore()/shs.getMaxScore());
			bankDataList.add(convertToBankData(map));
		}
		return bankDataList;
	}

	private BankData convertToBankData(Map<String , Object> map){
		if(map == null) return null;
		BankData bankData = new BankData();
		bankData.setId(Long.parseLong(String.valueOf(map.get("Id"))));
		bankData.setName(String.valueOf(map.get("name")));
		bankData.setOriUrl(String.valueOf(map.get("oriUrl")));
		bankData.setIssueDate(String.valueOf(map.get("issueDate")));
		bankData.setHtmlStr(String.valueOf(map.get("htmlStr")));
		bankData.setScore(map.get("score") == null ? 1.0f:(Float)map.get("score"));
		bankData.setPartyPerson(String.valueOf(map.get("partyPerson")));
		bankData.setBankName(String.valueOf(map.get("bankName")));
		bankData.setHolderName(String.valueOf(map.get("holderName")));
		bankData.setMainCase(String.valueOf(map.get("mainCase")));
		bankData.setAccording(String.valueOf(map.get("according")));
		bankData.setDecision(String.valueOf(map.get("decision")));
		bankData.setOrgName(String.valueOf(map.get("orgName")));
		return bankData;
	}

	public static void main(String[] s){
		EsOperater es = new EsOperater();
		es.open();

//		BankData bankData = new BankData();
//		bankData.setId(123L);
//		bankData.setName("test");
//		bankData.setOriUrl("http://baidu.com");
//		bankData.setIssueDate("2018-01-02");
//		bankData.setHtmlStr("测试");

//		es.insert(bankData);

		List<BankData> bds = es.search("谢京华");
	}
}
