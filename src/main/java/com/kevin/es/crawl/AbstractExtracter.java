package com.kevin.es.crawl;

import com.kevin.es.domain.BankData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public abstract class AbstractExtracter implements LinkExtracter {

    private volatile boolean isFinish;
    private Integer downLoadNum = 0;

    /**
     * 1、抓取线程
     * 2、保存线程
     */
    private ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 2,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    /**
     * 数据处理队列
     */
    private BlockingQueue<BankData> dataQueue = new LinkedBlockingQueue<BankData>();

    public int download(final List<UrlPreparer.HtmlExtracter> extracters) throws RuntimeException, ExecutionException, InterruptedException {
        //抓取
        pool.submit(new Runnable() {

            public void run() {
                Random random = new Random();
                UrlTool urlTool = new UrlTool();
                urlTool.connect();
                for(UrlPreparer.HtmlExtracter htmlExtracter : extracters) {
                    try {
                        Thread.sleep(random.nextInt(1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    for(int page = 1; page <=htmlExtracter.getPage(); page++){
                        String url = String.format(UrlTool.CURRENT_URL,htmlExtracter.getAddress(), page);
                        Document doc = Jsoup.parse(urlTool.doGet(url));
                        Elements liClasses = doc.getElementsByClass("work_list cc f12c");
                        for(Element liClass : liClasses){
                            BankData bankData= new BankData();
                            Element bookw3Element = liClass.getElementsByClass("bookw3").get(0);
                            Element hrefElement = bookw3Element.select("a[href]").get(0);
                            bankData.setOriUrl(UrlTool.BASE_URL + hrefElement.attr("href"));
                            bankData.setName(hrefElement.attr("title").trim());
                            Element dateElement = liClass.getElementsByClass("work_list_date").get(0);
                            bankData.setIssueDate(dateElement.text());
                            bankData.setId(System.currentTimeMillis());
                            //具体内容
                            bankData.setHtmlStr(UrlTool.extracterZH(urlTool.doGet(bankData.getOriUrl())));
                            try {
                                dataQueue.put(bankData);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }


                }
                urlTool.close();
                isFinish = true;
            }
        });


        //保存
        Future<Integer> future = pool.submit(new Callable<Integer>() {

            public Integer call() {
                try {
                    while (!isFinish || dataQueue.size() >0){
                        save(dataQueue.poll(3, TimeUnit.SECONDS));
                        ++downLoadNum;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return 1;
            }
        });


        //闭锁
        future.get();
        pool.shutdown();
        return downLoadNum;
    }

    protected abstract void save(BankData data);

}
