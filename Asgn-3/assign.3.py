#hello 2 3

import MySQLdb

def QueryOutputs():
   loadPre = "SELECT * FROM ("
   loadMid = ") qali INTO OUTFILE '/tmp/assign.3."
   loadSuf1 = ".csv' FIELDS TERMINATED BY ',' ENCLOSED BY '\"' "
   loadSuf2 = "LINES TERMINATED BY '\\n'"

   conn = MySQLdb.connect (host = "localhost",
                           user = "nvela",
                           passwd = "nuance",
                           db = "computers")
   cursor = conn.cursor ()

   # Get queries and file names from .sql file and output to .csv files
   myDir = raw_input("Enter the absolute path of the .sql file\n")
   fin = open(myDir,'r')
   fileSuf = fin.readline()[1:].strip('\n'); qLine = ''
   curr = fin.readline()
   while curr:
      if curr[0] == '#':
         cursor.execute(loadPre+qLine[:len(qLine)-1]+loadMid+fileSuf+loadSuf1+loadSuf2)
         fileSuf = curr[1:].strip('\n'); qLine = ''
         curr = fin.readline()
      else:
         while curr and (curr[0] != '#'):
            qLine += curr.strip('\n') + ' '
            curr = fin.readline()
   cursor.execute(loadPre+qLine[:len(qLine)-1]+loadMid+fileSuf+loadSuf1+loadSuf2)
   print '\n\n Query outputs are in /tmp directory.'
 
   cursor.close ()
   conn.close ()

   fin.close()