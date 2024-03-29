import scrapy
from soup import Reader
from scrapy.crawler import CrawlerProcess
import threading
import time
import multiprocessing
import logging

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
        try:


            for sel in response.xpath("//*[not(*)]/@id"):
                id = sel.extract()
                if len(id) > 0 and id.lower().find("comment") >= 0 :
                    print(id)
                if len(id) > 0 and id.lower().find("title") >= 0:
                    print (id)
                    titleId = id;
                    path ='//*[@id="'+id+'"]'
                    print(path)
                    text = response.xpath(path+"/text()").extract()
                    title =''.join(text)
                    print('#2')
                    print(title.replace(' ','').replace('\n',''))
        except Exception as error:
            print('An error occured.')
            print(error)
        end = time.time()
        print(end - start)

        print("#############################")

    def process(urls):
        process = CrawlerProcess({
            'USER_AGENT': 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)'
        })
        process.crawl(Comments, domain=["https://gadgets.ndtv.com/mobiles/smartphones"],commonUrls=urls)
        process.start()

if __name__ == '__main__':
    logging.getLogger('scrapy').setLevel(logging.WARNING)
    logging.getLogger('scrapy').propagate = False
    process = CrawlerProcess({
        'USER_AGENT': 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)'
    })

    process.crawl(Comments, domain=["https://www.amazon.in/gp/product/B07J2394DS/ref=s9_acsd_simh_hd_bw_b1yBwdz_c_x_w?pf_rd_m=A1K21FY43GMZF8&pf_rd_s=merchandised-search-11&pf_rd_r=D1ETTWGXA46FYD2AHTR8&pf_rd_t=101&pf_rd_p=764d4603-56b1-52ff-9323-2047ecb59e4e&pf_rd_i=1805560031"])
    process.start()
