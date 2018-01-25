package com.kevin.es.crawl;

import java.util.List;

public class Spider {
    private LinkExtracter extracter;
    private UrlPreparer preparer;
    private static Spider INSTANCE = new Spider();
    public static Spider getInstance(){
        return INSTANCE;
    }
    private Spider(){

    }

    public void init(){
        extracter = new ESExtracter();
        preparer = new UrlPreparer();
    }

    public int process() throws Exception {
        extracter.load();
        List<UrlPreparer.HtmlExtracter> htmlExtractors = preparer.getExtracterFromAddresses();
        int downLoadNum = extracter.download(htmlExtractors);
        return downLoadNum;
    }

    public void shutDown(){
        preparer.close();
    }
    public static void main(String[] args) throws Exception {
        Spider.INSTANCE.init();
        Spider.INSTANCE.process();
        Spider.INSTANCE.shutDown();
    }
}
