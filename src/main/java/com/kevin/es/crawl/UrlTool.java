package com.kevin.es.crawl;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public final class UrlTool {
    public static String URL_GET  = "http://www.cbrc.gov.cn/zhuanti/xzcf/getPcjgXZCFDocListDividePage/%s.html";
    public static String INIT_URL = "http://www.cbrc.gov.cn/zhuanti/xzcf/getPcjgXZCFDocListDividePage/beijing.html";
    public static String CURRENT_URL = "http://www.cbrc.gov.cn/zhuanti/xzcf/getPcjgXZCFDocListDividePage/%s.html?current=%d";
    public static String BASE_URL = "http://www.cbrc.gov.cn";

    private CloseableHttpClient client;

    public static String extracterZH(String s){
        String str = s;
        String reg = "[^\u4e00-\u9fa5]";
        str = str.replaceAll(reg, "");

        return filter(str);
    }

    static String filter(String src){
        String to = src;
        List<String> keywords = new ArrayList<String>();
        keywords.add("宋体");
        keywords.add("黑体");
        keywords.add("楷体");
        keywords.add("仿宋");
        keywords.add("普通表格");
        keywords.add("当前位置");
        keywords.add("首页");
        keywords.add("政务公开");
        keywords.add("发布时间");
        keywords.add("页脚");
        keywords.add("页眉");
        keywords.add("页码");
        keywords.add("年月日日期版权所有中国银行业监督管理委员会备号访问量次当前页访问量次");

        for(String key : keywords){
            to = to.replace(key, "");
        }
        return to;
    }

    public int connect(){

        client = HttpClients.createDefault();
        return 1;
    }

    public String doGet(String uriAPI) {

        String resStr = "";
        //创建一个httpclient对象


        try {
            //创建URIBuilder
            URIBuilder uri = new URIBuilder(uriAPI);
            //设置参数
//        uri.addParameter("id", "10001");

            //创建httpGet对象
            HttpGet hg = new HttpGet(uri.build());
            hg.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
            //设置请求的报文头部的编码
            hg.setHeader(
                    new BasicHeader("Content-Type", "charset=utf-8"));

            //设置期望服务端返回的编码
//            hg.setHeader(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8"));

            //请求服务
            CloseableHttpResponse response = null;
            response = client.execute(hg);
            //获取响应码
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {

                //获取返回实例entity
                HttpEntity entity = response.getEntity();

                //通过EntityUtils的一个工具方法获取返回内容
                resStr = EntityUtils.toString(entity, "utf-8");

                //输出
//                System.out.println("请求成功,请求返回内容为: " + resStr);
                System.out.println("请求成功,请求返回内容为: " + uriAPI);
            } else {

                //输出
                System.out.println("请求失败,错误码为: " + statusCode);
            }

            //关闭response和client
            response.close();


        }catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resStr;
    }

    public void close(){
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

