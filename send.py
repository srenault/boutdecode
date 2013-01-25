#!/usr/bin/python

import sys, os, re, json, urllib2

hostname="localhost:9000"
uri="/commit"

print "#####################"
print "### Piece Of Code ###"
print "#####################"
print

if len(sys.argv) != 2:
    print "Send a git diff to the server. "
    print sys.argv[0] + " <hash to send>"
    print "For example: " + sys.argv[0] + " HEAD, will send the last commit."
    sys.exit()

hash=sys.argv[1]

logs=os.popen('git log --color -p --date=short --format=medium ' + hash + "~1.." + hash).read()
print logs

#parser="commit(.+)$Author:(.+)$Date:(.+)$"
parser="commit (.+)\nAuthor: (.+)\nDate: (.+)\n((?:\n.+)+)\n\n((\n*.+)+)"
m = re.search(parser, logs, re.MULTILINE)
data = json.dumps( {"hash":m.group(1),"author":m.group(2),"date":m.group(3).strip(),"message":m.group(4).strip(),"diff":m.group(5)} )
#print m.group(5)

print data

print "Sending"
req = urllib2.Request("http://"+hostname+uri, data, {'Content-Type': 'application/json'})
f = urllib2.urlopen(req)
print f.read()
print f.getcode()
print "End"