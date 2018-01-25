package com.kevin.es.es;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 *
 */
public class EsUtils {
	
	public static final String INDEX_NAME = "mr_index";
	public static final String TYPE_NAME = "mr_type";
	
	private static TransportClient client;

	public static TransportClient getEsClient(){
		Settings settings = Settings.builder()
		        .put("cluster.name", "elasticsearch")//指定集群名称
		        .put("client.transport.sniff", true)//探测集群中机器状态
		        .put("xpack.security.user","elastic:changeme")
		        .build();		
		client = new PreBuiltXPackTransportClient(settings);
		try {
			client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return client;
	}
	
	public static void closeClient(){
		if(client!=null)
			client.close();
	}
	
	public static String getIndexName() {
		return INDEX_NAME;
	}
	public static String getTypeName() {
		return TYPE_NAME;
	}
	
	
	
	
	
}
