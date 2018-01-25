package com.kevin.es.crawl;


import com.kevin.es.domain.BankData;
import com.kevin.es.es.EsOperater;
import org.jsoup.helper.StringUtil;

public class ESExtracter extends AbstractExtracter {

    private EsOperater esOperater;

    @Override
    protected void save(BankData data) {
        if(data!=null){
            esOperater.insert(data);
        }
    }

    public int load() throws Exception {
        esOperater = new EsOperater();
        esOperater.open();
        return 1;
    }
}
