package cn.alvinkwok.spiderman;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Selectable;

import javax.management.JMException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description
 *
 * @author alvinkwok
 * @since 2024/5/14
 */
public class GuoKeYunPageProcessor implements PageProcessor {

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
        List<Selectable> postItemList = page.getHtml().xpath("//div[@class='technology-list']//list-content//table[@class='el-table__body']//div[@class='cell']").nodes();
        for (Selectable postItem : postItemList) {
            // 获取title
            String title = postItem.xpath("a[@class='link']/text()").get();
            // 获取url
            String url = postItem.xpath("a[@class='link']").links().get();

            URLItem item = new URLItem(title, url);
            items.add(item);
        }


        page.putField("items", items);
    }

    @Override
    public Site getSite() {
        return Site.me()
                .setRetryTimes(3)
                .setSleepTime(1000);
    }


    public static void main(String[] args) {
        HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
        httpClientDownloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("10.60.45.176", 7890, null, null)));
        Spider githubSpider = Spider.create(new GuoKeYunPageProcessor());
        Request request = new Request();
        request.setMethod("GET");
        request.setUrl("https://www.guokeyun.com/news/technology.html");

        githubSpider
                .setDownloader(httpClientDownloader)
                .addUrl("https://www.guokeyun.com/news/technology.html")
//                .addRequest(request)
                .thread(5).run();
    }
}
