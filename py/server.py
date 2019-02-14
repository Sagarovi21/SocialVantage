#!/usr/bin/python3
from flask import Flask, request, jsonify
from flask_restful import Resource, Api
from sqlalchemy import create_engine
from json import dumps
from soup import Reader
import scrapy
import time
import logging
from scrapy.crawler import CrawlerProcess

app = Flask(__name__)
api = Api(app)

urls=[]

class Comments(scrapy.Spider):
    name = "ScraperWithLimit"

    def __init__(self, domain=None, commonUrls = [],*args, **kwargs):
        commonUrls = set(commonUrls)
        self.urls =commonUrls;

    def start_requests(self):
        for url in self.urls:
            yield scrapy.Request(url=url, callback=self.parse)

    def parse(self, response):
        for sel in response.xpath("//div[contains(@class, 'user_cmnt_text')]/text()"):
            val = sel.extract()
            print(val)



class Employees(Resource):
    def get(self):
        return {'employees': 'Meeraj Kanaparthi'} # Fetches first column that is Employee ID


api.add_resource(Employees, '/employees') # Route_1


if __name__ == '__main__':
    start = time.time()
    """app.run(port='5002')"""
    urls = Reader.readFile('https://gadgets.ndtv.com/mobiles/smartphones');
    logging.getLogger('scrapy').setLevel(logging.WARNING)
    logging.getLogger('scrapy').propagate = False
    process = CrawlerProcess({
        'USER_AGENT': 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)'
    })

    process.crawl(Comments, domain=["https://gadgets.ndtv.com/mobiles/smartphones"],commonUrls=urls)
    process.start()
    end = time.time()
    print(end - start)
    print("#############################")
