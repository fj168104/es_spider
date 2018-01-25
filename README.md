# es_spider

elasticsearch httpClient jsoup 使用的使用， 使用前需要安装elasticsearch-5.5.1，请参考：http://www.ruanyifeng.com/blog/2017/08/elasticsearch.html

com.kevin.es.crawl.Spider 是可执行的爬虫类，抓取并分析网络数据。

java -jar com.kevin.es.crawl.Spider

com.kevin.es.crawl.EmbedServer 搜索服务，直接执行：

java -jar com.kevin.es.crawl.EmbedServer

启动后可以使用http://localhost:9080?sQuery=keyword 来搜索
