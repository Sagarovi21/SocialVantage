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
from scrapy.http import Request
import re
import teradata
from multiprocessing import Pool

app = Flask(__name__)
api = Api(app)

urls=[]
data = []
class Comments(scrapy.Spider):
    name = "ScraperWithLimit"
    def __init__(self, domain=None, commonUrls = [],task_id=None, *args, **kwargs):
        commonUrls = set(commonUrls)
        self.urls =commonUrls;
        self.task_id = task_id

    def start_requests(self):
        count = 0;
        for url in self.urls:
            count += 1
            try:
                session.execute("""UPDATE mkbasedb.TaskStatus SET pages_completed = ?
                                            WHERE task = ? , task_id = ? """,
                                         (str(count), 'webcrawl', self.task_id ))
                session.commit()
                yield scrapy.Request(url=url, callback=self.parse_phone_details, task_id = task_id)
            except Exception as error:
                print('An error occured during page opening')
                print(error)

    def parse_phone_details(self, response,task_id):
        start = time.time()
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
                yield  Request(reviews_url,callback=self.parse_phone_reviews,meta={'phone_name':phone_name}, task_id=task_id)
        except Exception as error:
            print('An error occured parse_phone_details')
            print(error)

        end = time.time()
        print(end - start)
        print("#############################")

    def parse_phone_reviews(self,response,task_id):
        phone_name = response.meta['phone_name']
        reviews = response.xpath('//div[contains(@id,"customer_review")]')
        count = 0
        for review in reviews:
            review_text = ' '.join(review.xpath('.//span[contains(@data-hook,"review-body")]/text()').extract())
            review_text = (review_text[:990] + '..') if len(review_text) > 990 else data
            review_ratings = review.xpath('.//a[contains(@title,"out")]/@title').extract()
            m = re.match(r'(.*) out of (.*) stars', review_ratings[0])
            data.append((task_id,phone_name, review_text, m.group(1), m.group(2)))
            if( count > 1):
                break
            count += 1;
        next_page_url = response.xpath('//li[contains(@class,"a-last")]/a/@href').extract_first()
        absolute_next_page_url = response.urljoin(next_page_url)
        yield Request(absolute_next_page_url,callback=self.parse_phone_reviews,meta={'phone_name':phone_name})


@app.route('/search/<int:task_id>/<string:search_string>', methods=['GET'])
def get_tasks(task_id,search_string):
    pool = Pool(processes=1)
    result = pool.apply_async(process, [task_id,search_string], callback) #
    return jsonify({'task_id': task_id, search_string: search_string, status : 'initiated'})


def process(task_id, search_string):
    start = time.time()

    udaExec = teradata.UdaExec (appName="mkbasedb", version="1.0",logConsole=False)
    session = udaExec.connect(method="odbc", system="sdt20168.labs.teradata.com",username="cim", password="cim")
    """app.run(port='5002')"""
    urls = Reader.readFile('https://www.amazon.in/'+search_string+'/b?ie=UTF8&node=1805560031');
    number_pages = str(len(urls))
    session.execute("""UPDATE mkbasedb.TaskStatus SET pages_found = ?
                                WHERE task = ?  and task_id= ? """,
                             (number_pages, 'webcrawl',task_id))
    session.commit()
    logging.getLogger('scrapy').setLevel(logging.WARNING)
    logging.getLogger('scrapy').propagate = False
    process = CrawlerProcess({
        'USER_AGENT': 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)'
    })

    process.crawl(Comments, domain=["https://gadgets.ndtv.com/mobiles/smartphones"],commonUrls=urls, task_id=task_id)
    process.start()

    session.executemany("""INSERT INTO mkbasedb.reviews (task_id, ObjectName, Review, Rating, TotalRating)
               VALUES (?, ?, ?, ?, ?)""",
            (data),
            batch=True)
    session.commit()

    cmnts = str(len(data))
    print("**********************************************"+cmnts)
    session.execute("""UPDATE mkbasedb.TaskStatus SET comments_found = ?, task_status = ?
                                    WHERE task = ?, task_id = ? """,
                                 (cmnts, 'done', 'webcrawl', task_id))
    session.commit()

    end = time.time()
    print(end - start)
    print("#############################")

if __name__ == '__main__':
        process(101,'Smartphones')
