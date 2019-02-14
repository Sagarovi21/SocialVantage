import scrapy
from soup import Reader
from scrapy.crawler import CrawlerProcess
import threading
import time
import multiprocessing

class Comments(scrapy.Spider):
    name = "ScraperWithLimit"

    def __init__(self, domain=None, commonUrls = [],*args, **kwargs):
        self.urls = domain
        commonUrls = set(commonUrls)
        self.commonUrls =commonUrls;

    def start_requests(self):
        for url in self.urls:
            yield scrapy.Request(url=url, callback=self.parse)

    def parse(self, response):
        start = time.time()
        for sel in response.xpath(".//div[contains(@class, 'user_cmnt_text')]"):
            print(val)
        end = time.time()
        print(end - start)
        print("#############################")

    def process(urls):
        process = CrawlerProcess({
            'USER_AGENT': 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)'
        })
        process.crawl(Comments, domain=["https://gadgets.ndtv.com/mobiles/smartphones"],commonUrls=urls)
        process.start()
