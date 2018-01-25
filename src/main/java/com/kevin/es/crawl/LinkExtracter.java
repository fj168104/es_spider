package com.kevin.es.crawl;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface LinkExtracter {

    /**
     * 更具地址URL返回抓取网页信息到具体的位置
     * @return 下载条数
     * @throws RuntimeException
     */
    public int download(List<UrlPreparer.HtmlExtracter> extracters) throws RuntimeException, ExecutionException, InterruptedException;

    /**
     * 加载信息存放载体等
     * @return
     * @throws Exception
     */
    public int load() throws Exception;

}
