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

app = Flask(__name__)
api = Api(app)
udaExec = teradata.UdaExec (appName="mkdb", version="1.0",logConsole=False)
session = udaExec.connect(method="odbc", system="tdap863t1.labs.teradata.com",username="alice", password="alice")
urls=[]
data = []
class Comments(scrapy.Spider):
    name = "ScraperWithLimit"
    loop = asyncio.get_event_loop()
    tasks = []
    
    def __init__(self, domain=None, commonUrls = [],task_id=None,data=None, *args, **kwargs):
        commonUrls = set(commonUrls)
        self.urls =commonUrls
        self.task_id = task_id
        self.data = data
        

    def start_requests(self):
        count = 0
        self.cmnts = 0
        number_pages = str(len(urls))
        session.execute("""UPDATE mkdb.TaskStatus SET pages_found = ?
                                WHERE task = ?  and task_id= ? """,
                             (number_pages, 'webcrawl',self.task_id))
        session.commit()
        for url in self.urls:
            count += 1
            try:
                self.data = ()
                session.execute("""UPDATE mkdb.TaskStatus SET pages_completed = ? , task_status = ?  WHERE task = ? and task_id = ? """,
                                         (str(count), 'inprogress','webcrawl', self.task_id ))
                session.commit()
                yield scrapy.Request(url=url, callback=self.parse_phone_details)
                
            except Exception as error:
                print('An error occured during page opening')
                print(error)
        
        print("**********************************************"+ str(self.cmnts))
        session.execute("""UPDATE mkdb.TaskStatus SET comments_found = ?, task_status = ?
                                WHERE task = ? and task_id = ? """,
                                (self.cmnts, 'done', 'webcrawl', self.task_id))
        session.commit()
        self.loop.run_until_complete(asyncio.wait(self.tasks))  
        self.loop.close()

    def parse_phone_details(self, response):
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
                yield  Request(reviews_url,callback=self.parse_phone_reviews,meta={'phone_name':phone_name, 'product_details' : product_details,'technial_details':'test','feature_names' : feature_names,'feature_values':feature_values  })
        except Exception as error:
            print('An error occured parse_phone_details')
            print(error)

        end = time.time()
        print(end - start)
        print("#############################")

    def parse_phone_reviews(self,response):
        phone_name = response.meta['phone_name']
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
            self.cmnts += 1
            self.tasks.append(asyncio.ensure_future(self.runQuery(data)))
            if( count > 10):
                break
            count += 1
        next_page_url = response.xpath('//li[contains(@class,"a-last")]/a/@href').extract_first()
        absolute_next_page_url = response.urljoin(next_page_url)
        yield  Request(absolute_next_page_url,callback=self.parse_phone_reviews,meta={'phone_name':phone_name, 'product_details' : product_details,'technial_details':'test','feature_names' : feature_names,'feature_values':feature_values  })

    async def runQuery(self,data):
        try:
            session.execute("""INSERT INTO mkdb.reviews (task_id, ObjectName, Review, Rating, TotalRating, product_details, technial_details,feature_names, feature_values )
                        VALUES (?, ?, ?, ?, ?, ? , ? , ? , ?)""",
                        data)
            session.commit()
            print(data)
        except Exception as error:
            print('An error occured run query')
            print(error)
            print(self.urls)

    def taskQuery(self):
        session.execute("""INSERT INTO mkdb.taskstatus (task_id, task, ObjectName, pages_found, pages_completed, comments_found, task_status)
                        VALUES (?, ?, ?, ?, ?,?,?)""",
                        (self.task_id, 'webcrawl', len(self.urls),0,0,0,'initiated'))
        session.commit()



@app.route('/search/<int:task_id>/<string:search_string>', methods=['GET'])
def get_tasks(task_id,search_string):
    pool = Pool(processes=1)
    result = pool.apply_async(process, [task_id,search_string]) 
    return jsonify({'task_id': task_id, 'search_string': search_string, 'status' : 'initiated'})


def process(task_id, search_string):
    start = time.time()

    """app.run(port='5002')"""
    urls = Reader.readFile('https://www.amazon.in/s/ref=nb_sb_noss?field-keywords='+search_string)
    
    logging.getLogger('scrapy').setLevel(logging.WARNING)
    logging.getLogger('scrapy').propagate = False
    process = CrawlerProcess({
        'USER_AGENT': 'Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)'
    })

    session.execute("""INSERT INTO mkdb.taskstatus (task_id, task, ObjectName, pages_found, pages_completed, comments_found, task_status)
                        VALUES (?, ?, ?, ?, ?,?,?)""",
                        (task_id, 'webcrawl', len(urls),0,0,0,'initiated'))
    session.commit()

    process.crawl(Comments, domain=["https://gadgets.ndtv.com/mobiles/smartphones"],commonUrls=urls, task_id=task_id, data = data)
    process.start()
    

    end = time.time()
    print(end - start)
    print("#############################")

if __name__ == '__main__':
        process(777,'smartphone')
