from sqlalchemy import *
from sqlalchemy.dialects import registry
from sqlalchemy_teradata.dialect import TeradataDialect
from sqlalchemy.ext.declarative import declarative_base, DeferredReflection
from sqlalchemy import Table, Column, Integer, String, MetaData, ForeignKey
from sqlalchemy.orm import scoped_session, sessionmaker
from sqlalchemy import create_engine
from teradata import tdodbc
import teradata

class Tops:

     def setUp(self):

        udaExec = teradata.UdaExec ()
        with udaExec.connect("${dataSourceName}") as session:
            for row in session.execute("SELECT * FROM ${table}"):
                print(row)

        udaExec = teradata.UdaExec (appName="mkbasedb", version="1.0",logConsole=False)
        session = udaExec.connect(method="odbc", system="sdt20168.labs.teradata.com",username="cim", password="cim")
        count = 0
        data = []
        data.append(('e','3',4.3,5.0))
        data.append(('e2','23',4.3,5.0))
        session.executemany("""INSERT INTO mkbasedb.reviews (ObjectName, Review, Rating, TotalRating)
               VALUES (?, ?, ?, ?)""",
            (data),
            batch=True)
        session.commit()

if __name__ == '__main__':
    tops = Tops()
    tops.setUp()
