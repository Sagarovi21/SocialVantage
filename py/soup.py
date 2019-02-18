from bs4 import BeautifulSoup
import urllib.request

class Reader:
    def readFile(url) :
        commonUrls = [];
        try:
            source = urllib.request.urlopen(url).read()
            soup = BeautifulSoup(source, 'html.parser' )
            mydivs =soup.find_all('a', { "class" : "a-link-normal" })
            for link in mydivs:
                commonUrls.append (link['href'])

        except Exception as e:
            print(url)
            print(e)
        return commonUrls;

    if __name__ == '__main__':
        readFile('https://gadgets.ndtv.com/mobiles/smartphones');
