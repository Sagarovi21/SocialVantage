import scrapy
from scrapy.crawler import CrawlerProcess
import urllib.request
from scrapy.crawler import CrawlerProcess

class Parsing(scrapy.Spider):
    def __init__(self, domain=None, commonUrls = [],*args, **kwargs):
        self.urls = domain
        self.commonUrls =commonUrls;

    def start_requests(self):
        for url in self.urls:
            yield scrapy.Request(url=url, callback=self.parse)

    def parse(self, response):
        for sel in response.xpath(".//div[contains(@class, 'user_cmnt_text')]"):
            val = sel.xpath('//a/@href').extract();
            self.commonUrls.append (val)
            print(val)
