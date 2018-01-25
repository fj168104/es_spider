package com.kevin.es.crawl;


import com.kevin.es.domain.BankData;

public class DefaultExtracter extends AbstractExtracter {


    protected void save(BankData data) {
        if(data!=null){
            System.out.println(data);
            System.out.println(data.getHtmlStr());
        }
    }

    public int load() throws Exception {
        return 0;
    }
}
