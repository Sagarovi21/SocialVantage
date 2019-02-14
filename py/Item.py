import scrapy


class Item(scrapy.Item):
    name = scrapy.Field()
    link = scrapy.Field()
    img = scrapy.Field()
