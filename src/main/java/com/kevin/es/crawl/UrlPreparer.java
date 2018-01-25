package com.kevin.es.crawl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class UrlPreparer {

    private UrlTool urlTool;

    public UrlPreparer(){
        urlTool = new UrlTool();
        urlTool.connect();
    }

    public void close(){
        urlTool.close();
    }

    private UrlTool getUrlTool() {
        return urlTool;
    }

    /**
     * 获取所有地址
     * @return
     */
    public List<String> getAddresses(){
        List<String> addresses = new ArrayList<String>(100);
        Document doc = Jsoup.parse(urlTool.doGet(UrlTool.INIT_URL));
        Elements liClass = doc.getElementsByClass("lpcjg_w blue");
        for(Element element : liClass){
            Elements links = element.select("a[href]");
            for(Element el : links){
                String link =  el.attr("href");
                String[] _args  = link.split("/");
                addresses.add(_args[2]);
            }
        }
        return addresses;
    }
    /**
     * 解析抓取的网页
     * @return
     */
    public List<HtmlExtracter> getExtracterFromAddresses() {
        List<String> addresses = getAddresses();
        List<HtmlExtracter> extracters = new ArrayList<HtmlExtracter>(addresses.size());

        for(String address : addresses){
            int page = 1;
            Document doc = Jsoup.parse(urlTool.doGet(String.format(UrlTool.URL_GET, address)));
            Elements wpEls = doc.getElementsByClass("work_page");
            Elements pLs = wpEls.get(0).select("a[href]");
            if(pLs.size() > 1){
                Element pL = pLs.get(1);
                String link =  pL.attr("href");
                int index  = link.lastIndexOf("current=");
                page = Integer.parseInt(link.substring(index).replaceAll("current=", "").trim());
            }



            HtmlExtracter extracter = new HtmlExtracter();
            extracter.setPage(page);
            extracter.setAddress(address);
            extracters.add(extracter);
        }

        return extracters;

    }

    /**
     * 地区信息提取器
     */
    public static class HtmlExtracter{
        HtmlExtracter(){

        }
        private Integer page;

        private String address;

        public void setPage(Integer page){
            this.page = page;
        }

        public Integer getPage() {
            return page;
        }

        public void setAddress(String address){
            this.address = address;
        }
        public String getAddress(){
            return this.address;
        }
    }

    public static void main(String[] s){
        UrlPreparer preparer = new UrlPreparer();
        String url = preparer.getUrlTool().doGet(UrlTool.INIT_URL);
        System.out.println(url);
        System.out.println(preparer.getUrlTool().extracterZH(url));
        System.out.println(preparer.getExtracterFromAddresses());
        List<HtmlExtracter> extracters = preparer.getExtracterFromAddresses();
        preparer.getUrlTool().close();
        System.out.println("*******************************************");
    }

}
