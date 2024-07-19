import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import java.net.URI;


public class WebCrawler {
    private final int IMAGE_STARTING_AMOUNT_LIMIT = 100;
    private final int THREAD_LIMIT = 20;
    private int imageSoftLimit = 100;
    private CopyOnWriteArraySet<String> visitedUrls = new CopyOnWriteArraySet<>();
    private CopyOnWriteArraySet<String> imageUrls = new CopyOnWriteArraySet<>();
    private CopyOnWriteArraySet<String> awaitUrls = new CopyOnWriteArraySet<>(); // sites that didn't get to be crawl to limit the # of threads created
    private CopyOnWriteArrayList<Thread> threads = new CopyOnWriteArrayList<>();
    private String domain;

    public String[] response(String message) {
        String[] res = new String[3 + imageUrls.size()];
        res[0] = Integer.toString(visitedUrls.size());
        res[1] = Integer.toString(imageUrls.size());
        res[2] = message;
        int index = 3;
        for (String url: imageUrls) {
            res[index++] = url;
        }
        return res;
    }

    public String[] crawl(String url) {
        domain = DomainExtractor.extractDomain(url);
        visitedUrls.clear();
        imageUrls.clear();
        awaitUrls.clear();
        threads.clear();
        imageSoftLimit = IMAGE_STARTING_AMOUNT_LIMIT;

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        crawlUrl(url);
        System.out.println("Visited Site Count: " + visitedUrls.size());
        System.out.println("Image Count: " + imageUrls.size());
        return (!imageUrls.isEmpty()) ? response("Done") : response("No Image or Website to Crawl");
    }

    public String[] crawlUpdate() {
        int previousLength = imageUrls.size();
        String[] urls = new String[awaitUrls.size()];
        int index = 0;
        for (String url: awaitUrls) {
            urls[index++] = url;
        }
        awaitUrls.clear();
        imageSoftLimit += imageUrls.size();
        for (String url: urls) {
            if (imageUrls.size() < imageSoftLimit) crawlUrl(url);
            else awaitUrls.add(url);
        }
        System.out.println("Visited Site Count: " + visitedUrls.size());
        System.out.println("Image Count: " + imageUrls.size());
        return (previousLength != imageUrls.size()) ? response("Done") : response("Nothing More to Crawl");
    }

    private void crawlUrl(String url) {
        if (!visitedUrls.contains(url)) {
            visitedUrls.add(url);
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(500); // pause for .2 seconds
                    Document document = Jsoup.connect(url).get();
                    Elements links = document.select("a[href]");
                    for (Element link : links) {
                        String nextUrl = link.absUrl("href");
                        if (isValidUrl(nextUrl)) {
                            if (threads.size() <= THREAD_LIMIT && imageUrls.size() <= imageSoftLimit) crawlUrl(nextUrl);
                            else awaitUrls.add(nextUrl);
                        }
                    }
                    Elements images = document.select("img[src]");
                    for (Element image : images) {
                        String imageUrl = image.absUrl("src");
                            synchronized (imageUrls) {
                                imageUrls.add(imageUrl);
                            }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            threads.add(thread);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            threads.remove(thread);
        }
    }

    private boolean isValidUrl(String url) {
//        System.out.println(url);
//        System.out.println(this.domain);
        // Check if URL is within the same domain and not already visited
        return this.domain.equals(DomainExtractor.extractDomain(url)) && !visitedUrls.contains(url);
    }

    public static void main(String[] args) {
        WebCrawler crawler = new WebCrawler();
        String[] imageUrls = crawler.crawl("https://www.apple.com");
        System.out.println("Found images:");
        for (String imageUrl : imageUrls) {
            System.out.println(imageUrl);
        }
    }
}