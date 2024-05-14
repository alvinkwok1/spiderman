package cn.alvinkwok.spiderman;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Selectable;

import javax.management.JMException;
import java.util.ArrayList;
import java.util.List;

public class BlogAlvinkwokPageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    private static class URLItem {
        private String title;
        private String url;

        public URLItem(String title, String url) {
            this.title = title;
            this.url = url;
        }

        @Override
        public String toString() {
            return "title=" + title + ",url=" + url;
        }
    }

    @Override
    public void process(Page page) {
//        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
//        page.putField("name", page.getHtml().xpath("//h1[@class='public']/strong/a/text()").toString());
//        if (page.getResultItems().get("name") == null) {
        //skip this page
//            page.setSkip(true);
//        }
//        page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
        List<URLItem> items = new ArrayList<>();
        // 返回所有的页面节点
        List<Selectable> postItemList = page.getHtml().xpath("//div[@class='post-item']").nodes();
        for (Selectable postItem : postItemList) {
            // 获取title
            String title = postItem.xpath("a[@class='post-title']/text()").get();
            // 获取url
            String url = postItem.xpath("a[@class='post-title']").links().get();

            URLItem item = new URLItem(title, url);
            items.add(item);
        }
        page.putField("items", items);

        // 抽取下一页
        page.addTargetRequest(page.getHtml().xpath("//div[@class='pager-next']/a").links().get());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws JMException {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("10.60.45.176", 7890, null, null)));
        Spider githubSpider = Spider.create(new BlogAlvinkwokPageProcessor());
        githubSpider.setDownloader(httpClientDownloader)
                .addUrl("https://blog.alvinkwok.cn").thread(5).run();

        SpiderMonitor.instance().register(githubSpider);
        githubSpider.start();
    }
}