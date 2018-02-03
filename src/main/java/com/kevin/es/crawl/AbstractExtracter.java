package com.kevin.es.crawl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.kevin.es.domain.BankData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
                            String oriHtml = urlTool.doGet(bankData.getOriUrl());
                            bankData.setHtmlStr(UrlTool.extracterZH(oriHtml));

                            //解析具体内容为格式化数据
                            Document formatDoc = Jsoup.parse(oriHtml);
                            Elements formatClasses = formatDoc.getElementsByClass("MsoNormal");
                            Map<String, Integer> nameToIndex = Maps.newHashMap();
                            for(int i = 0; i < formatClasses.size(); i++){
                                if("个人姓名".equals(formatClasses.get(i).select("span").text().trim())){
                                    nameToIndex.put("个人姓名", ++i);
                                }
                                if("名称".equals(formatClasses.get(i).select("span").text().trim())){
                                    nameToIndex.put("名称", ++i);
                                }
                                if(formatClasses.get(i).select("span").text().trim().contains("法定代表人（主要负责人）姓名")){
                                    nameToIndex.put("法定代表人（主要负责人）姓名", ++i);
                                }
                                if(formatClasses.get(i).select("span").text().trim().contains("主要违法违规事实（案由）")){
                                    nameToIndex.put("主要违法违规事实（案由）", ++i);
                                }
                                if("行政处罚依据".equals(formatClasses.get(i).select("span").text().trim())){
                                    nameToIndex.put("行政处罚依据", ++i);
                                }
                                if("行政处罚决定".equals(formatClasses.get(i).select("span").text().trim())){
                                    nameToIndex.put("行政处罚决定", ++i);
                                }
                                if("作出处罚决定的机关名称".equals(formatClasses.get(i).select("span").text().trim())){
                                    nameToIndex.put("作出处罚决定的机关名称", ++i);
                                }


//                                System.out.println(">>>>>>>>>>>>>>>>>>>>>> i = " + i);
//                                System.out.println(">>>>>>>>>>>>>>>>>>>>>> conent = " + formatClasses.get(i));
//                                System.out.println(">>>>>>>>>>>>>>>>>>>>>> nameToIndex = " + nameToIndex);
//                                System.out.println(">>>>>>>>>>>>>>>>>>>>>> span = " +formatClasses.get(i).select("span").text().trim());
                            }
                            //当事人
                            if(nameToIndex.get("个人姓名") != null){
                                bankData.setPartyPerson(formatClasses.get(nameToIndex.get("个人姓名")).select("span").text().trim());
                            }

                            //金融机构名称
                            if(nameToIndex.get("名称") != null){
                                bankData.setBankName(formatClasses.get(nameToIndex.get("名称")).select("span").text().trim());
                            }

                            //法人名称
                            if(nameToIndex.get("法定代表人（主要负责人）姓名") != null){
                                bankData.setHolderName(formatClasses.get(nameToIndex.get("法定代表人（主要负责人）姓名")).select("span").html().trim());
                            }

                            //主要违法违规事实（案由）
                            if(nameToIndex.get("主要违法违规事实（案由）") != null){
                                bankData.setMainCase(formatClasses.get(nameToIndex.get("主要违法违规事实（案由）")).select("span").text().trim());
                            }

                            //行政处罚依据
                            if(nameToIndex.get("行政处罚依据") != null){
                                bankData.setAccording(formatClasses.get(nameToIndex.get("行政处罚依据")).select("span").text().trim());
                            }

                            //行政处罚决定
                            if(nameToIndex.get("行政处罚决定") != null){
                                bankData.setDecision(formatClasses.get(nameToIndex.get("行政处罚决定")).select("span").text().trim());
                            }
                            //作出处罚决定的机关名称
                            if(nameToIndex.get("作出处罚决定的机关名称") != null){
                                bankData.setOrgName(formatClasses.get(nameToIndex.get("作出处罚决定的机关名称")).select("span").text().trim());
                            }

                            System.out.println(bankData);

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
