#!/usr/bin/python3
from flask import Flask, request, jsonify
from flask_restful import Resource, Api
from json import dumps
from soup import Reader
import scrapy
import time
import logging
from scrapy.crawler import CrawlerProcess
from scrapy.http import Request
import re
import teradata
import asyncio
from multiprocessing import Pool

class Comments(scrapy.Spider):
    name = "ScraperWithLimit"
    def __init__(self, domain=None, commonUrls = [],task_id=None,data=None, *args, **kwargs):
        self.commonUrls = set(commonUrls)
        self.urls =commonUrls
        self.task_id = task_id
        self.data = data
        

    def start_requests(self):
       
        for url in self.urls:
           
            try:
                yield scrapy.Request(url=url, callback=self.parse_phone_details)  
            except Exception as error:
                print('An error occured during page opening')
                print(error)

    def parse_phone_details(self, response):
        
        try:
            phone_name = response.xpath('//span[contains(@id,"productTitle")]/text()').extract_first().strip()
            product_details = response.xpath('//div[contains(@id,"prodDetails")]')
            technial_details = product_details.xpath('.//div[contains(@class,"pdTab")]')[0]
           
            feature_names = technial_details.xpath('.//td[contains(@class,"label")]/text()').extract()
            feature_values = technial_details.xpath('.//td[contains(@class,"value")]/text()').extract()
            features_data = dict(zip(feature_names,feature_values))
            print (features_data)

            additional_info = product_details.xpath('.//div[contains(@class,"pdTab")]')[1]
            is_user_reviews_exist = not additional_info.xpath('.//tr[contains(@class,"customer_reviews")]')\
                                    .xpath('.//a/text()')\
                                    .extract_first().strip() == "Be the first to review this item"
            if(is_user_reviews_exist):
                reviews_url = additional_info.xpath('.//tr[contains(@class,"customer_reviews")]')\
                    .xpath('.//a[contains(@class,"link")]/@href')\
                    .extract_first()
                yield  Request(reviews_url,callback=self.parse_phone_reviews,meta={'phone_name':phone_name, 'product_details' : product_details,'technial_details':'test','feature_names' : feature_names,'feature_values':feature_values  })
        except Exception as error:
            print('An error occured parse_phone_details')
            print(error)

    def parse_phone_reviews(self,response):
        product_details = response.meta['product_details']
        if len (product_details) > 230 :
            product_details = product_details[:230]
        technial_details = response.meta['technial_details']
        if len (technial_details) > 230 :
            technial_details = technial_details[:230]
        feature_names = response.meta['feature_names']
        if len (feature_names) > 230 :
            feature_names = feature_names[:230]
        feature_values = response.meta['feature_values']
        if len (feature_values) > 230 :
            feature_values = feature_values[:230]
        phone_name = response.meta['phone_name']
        reviews = response.xpath('//div[contains(@id,"customer_review")]')
        count = 0
        for review in reviews:
            review_text = ' '.join(review.xpath('.//span[contains(@data-hook,"review-body")]/text()').extract())
            if len (phone_name) > 120:
                phone_name = phone_name[:120]
            if len(review_text) > 900:
                review_text = review_text[:900]
            review_ratings = review.xpath('.//a[contains(@title,"out")]/@title').extract()
            m = re.match(r'(.*) out of (.*) stars', review_ratings[0])
            rating = float(m.group(1))
            total_rating =  float(m.group(2))
            
            data = (self.task_id,phone_name, review_text, rating,total_rating, product_details,technial_details,feature_names,  feature_values )
            print( "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&")
            print(data )
            if( count > 10):
                break
            count += 1
        next_page_url = response.xpath('//li[contains(@class,"a-last")]/a/@href').extract_first()
        absolute_next_page_url = response.urljoin(next_page_url)
        yield  Request(absolute_next_page_url,callback=self.parse_phone_reviews,meta={'phone_name':phone_name, 'product_details' : product_details,'technial_details':'test','feature_names' : feature_names,'feature_values':feature_values  })


def process(task_id, search_string):
    start = time.time()

    """app.run(port='5002')"""
    urls = Reader.readFile('https://www.amazon.in/s/ref=nb_sb_noss?field-keywords='+search_string)

    logging.getLogger('scrapy').setLevel(logging.WARNING)
    logging.getLogger('scrapy').propagate = False
    process = CrawlerProcess({
        'USER_AGENT': 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)'
    })
    process.crawl(Comments, domain=["https://gadgets.ndtv.com/mobiles/smartphones"],commonUrls=urls, task_id=task_id)
    process.start()
    
    
    end = time.time()
    print(end - start)
    print("#############################")


if __name__ == '__main__':
        process(124,'smartphone')