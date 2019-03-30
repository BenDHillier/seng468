import httplib
import threading
import thread
import urllib
import urllib2
import sys
import time
import random
import socket

# TODO: SET_BUY_AMOUNT, CANCEL_SET_BUY, SET_BUY_TRIGGER, SET_SELL_AMOUNT, CANCEL_SET_SELL, SET_SELL_TRIGGER
# Will do the above functions once triggers are in place

# TODO: DISPLAY_SUMMARY
# Will do DISPLAY_SUMMARY once logging is in place

class WorkloadGenerator:
    def __init__(self, ip, port, paramsList):
        self._args_ip = ip
        self._args_port = port
        self.paramsList = paramsList
        try:
            self._httpconnection = httplib.HTTPConnection(self._args_ip, self._args_port)
        except httplib.HTTPException:
            print("Error. Could not create HTTPConnection")
            sys.exit()
        except TypeError as e:
            print e.message
            sys.exit()

    def run(self):
        for (command,params) in self.paramsList:
            print "{}:{}".format(command,params)
            if len(params[-1]) > 4 and params[-1][-4:] == '000':
                print params[-1]
            try:
                self.handleCommand(command,params)
            except Exception as e:
                print "{},{} failed due to exception {}, {}".format(command,params,e, repr(e))

    def handleCommand(self,cmd,params):
        if( cmd == "ADD"):
            self.addRequest(params)
        elif( cmd == "BUY"):
            self.buyRequest(params)
        elif( cmd == "COMMIT_BUY"):
            self.commitBuyRequest(params)
        elif( cmd == "CANCEL_BUY"):
            self.cancelBuyRequest(params)
        elif( cmd == "QUOTE"):
            self.quoteRequest(params)
        elif( cmd == "SELL"):
            self.sellRequest(params)
        elif( cmd == "COMMIT_SELL"):
            self.commitSellRequest(params)
        elif( cmd == "CANCEL_SELL"):
            self.cancelSellRequest(params)
        elif( cmd == "SET_BUY_AMOUNT"):
            self.setBuyAmountRequest(params)
        elif( cmd == "SET_BUY_TRIGGER"):
            self.setBuyTriggerRequest(params)
        elif( cmd == "CANCEL_SET_BUY"):
            self.cancelSetBuyRequest(params)
        elif( cmd == "SET_SELL_AMOUNT"):
            self.setSellAmountRequest(params)
        elif( cmd == "SET_SELL_TRIGGER"):
            self.setSellTriggerRequest(params)
        elif( cmd == "CANCEL_SET_SELL"):
            self.cancelSetSellRequest(params)
        elif( cmd == "DUMPLOG"):
            self.dumplogRequest(params)
        elif( cmd == "DISPLAY_SUMMARY"):
            self.displaySummaryRequest(params)
        else:
            print("command was " + cmd)

    #POST
    def setSellTriggerRequest(self,params):
        put_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'amount':int(float(params[2])* 100), 'transactionNum':params[3]})
        headers = {"Content-type": "application/x-www-form-urlencoded"}
        self._httpconnection.request('POST', '/sellTrigger/trigger', put_params, headers)
        response = self._httpconnection.getresponse()
        data = response.read()
        #print("{} {}".format(data,response.status))

    #POST
    def setSellAmountRequest(self,params):
        put_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'amount':int(float(params[2]) * 100), 'transactionNum':params[3]})
        headers = {"Content-type": "application/x-www-form-urlencoded"}
        self._httpconnection.request('POST', '/sellTrigger/amount', put_params, headers)
        response = self._httpconnection.getresponse()
        data = response.read()
        #print("{} {}".format(data,response.status))

    #POST
    def cancelSetSellRequest(self,params):
        put_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1], 'transactionNum':params[2]})
        headers = {"Content-type": "application/x-www-form-urlencoded"}
        self._httpconnection.request('POST', '/sellTrigger/cancel', put_params, headers)
        response = self._httpconnection.getresponse()
        data = response.read()
    #POST
    def setBuyTriggerRequest(self,params):
        put_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'amount':int(float(params[2])* 100), 'transactionNum':params[3]})
        headers = {"Content-type": "application/x-www-form-urlencoded"}
        self._httpconnection.request('POST', '/buyTrigger/trigger', put_params, headers)
        response = self._httpconnection.getresponse()
        data = response.read()
        #print("{} {}".format(data,response.status))

    #POST
    def setBuyAmountRequest(self,params):
        put_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'amount':int(float(params[2]) * 100), 'transactionNum':params[3]})
        headers = {"Content-type": "application/x-www-form-urlencoded"}
        self._httpconnection.request('POST', '/buyTrigger/amount', put_params, headers)
        response = self._httpconnection.getresponse()
        data = response.read()
        #print("{} {}".format(data,response.status))

    #POST
    def cancelSetBuyRequest(self,params):
        put_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1], 'transactionNum':params[2]})
        headers = {"Content-type": "application/x-www-form-urlencoded"}
        self._httpconnection.request('POST', '/buyTrigger/cancel', put_params, headers)
        response = self._httpconnection.getresponse()
        data = response.read()
        #print("{} {}".format(data,response.status))

    #PUT
    def addRequest(self,params):
        # TODO: Eventually will have to send unformatted string of float val
        # for now will have to send as a value formatted to integer for testing purposes
        put_params = urllib.urlencode({'userId':params[0],'amount':int(float(params[1]) * 100), 'transactionNum':params[2]})
        headers = {"Content-type": "application/x-www-form-urlencoded"}
        self._httpconnection.request('PUT', '/add', put_params, headers)
        response = self._httpconnection.getresponse()
        data = response.read()
        #print("{} {}".format(data,response.status))

    #GET
    def quoteRequest(self,params):
        get_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'transactionNum':params[2]})
        get_request = urllib2.urlopen('http://{}:{}/quote?'.format(self._args_ip,self._args_port) + get_params)
        response = get_request.read()
        #print(response)

    #POST
    def buyRequest(self,params):
        post_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'amount':int(float(params[2]) * 100),'transactionNum':params[3]})
        headers = {"Content-type": "application/x-www-form-urlencoded"}
        self._httpconnection.request('POST', '/buy/create', post_params, headers)
        response = self._httpconnection.getresponse()
        data = response.read()
        #print("{} {}".format(data,response.status))

    #POST
    # TODO: confirm this is the /create route in server
    def commitBuyRequest(self,params):
        post_params = urllib.urlencode({'userId':params[0],'transactionNum':params[1]})
        headers = {"Content-type": "application/x-www-form-urlencoded"}
        self._httpconnection.request('POST', '/buy/commit', post_params, headers)
        response = self._httpconnection.getresponse()
        data = response.read()
        #print("{} {}".format(data,response.status))

    #POST
    def cancelBuyRequest(self,params):
        post_params = urllib.urlencode({'userId':params[0],'transactionNum':params[1]})
        headers = {"Content-type": "application/x-www-form-urlencoded"}
        self._httpconnection.request('POST', '/buy/cancel', post_params, headers)
        response = self._httpconnection.getresponse()
        data = response.read()
        #print("{} {}".format(data,response.status))

    #POST
    def sellRequest(self,params):
        post_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'amount':int(float(params[2]) * 100),'transactionNum':params[3]})
        headers = {"Content-type": "application/x-www-form-urlencoded"}
        self._httpconnection.request('POST', '/sell/create', post_params, headers)
        response = self._httpconnection.getresponse()
        data = response.read()
        #print("{} {}".format(data,response.status))

    #POST
    def commitSellRequest(self,params):
        post_params = urllib.urlencode({'userId':params[0],'transactionNum':params[1]})
        headers = {"Content-type": "application/x-www-form-urlencoded"}
        self._httpconnection.request('POST', '/sell/commit', post_params, headers)
        response = self._httpconnection.getresponse()
        data = response.read()
        #print("{} {}".format(data,response.status))

    #POST
    def cancelSellRequest(self,params):
        post_params = urllib.urlencode({'userId':params[0],'transactionNum':params[1]})
        headers = {"Content-type": "application/x-www-form-urlencoded"}
        self._httpconnection.request('POST', '/sell/cancel', post_params, headers)
        response = self._httpconnection.getresponse()
        data = response.read()
        #print("{} {}".format(data,response.status))

    #GET
    def dumplogRequest(self,params):
        get_params = urllib.urlencode({'filename':params[0],'transactionNum':params[1]})
        get_request = urllib2.urlopen('http://{}:{}/dumplog/all?'.format(self._args_ip,self._args_port) + get_params)
        response = get_request.read()
        logfile = open(params[0], "w+")
        logfile.write(response)
        logfile.close
        #print("Log written to {}".format(params[0]))
        #print(response)
    
    #GET
    def displaySummaryRequest(self,params):
        get_params = urllib.urlencode({'userId':params[0],'transactionNum':params[1]})
        get_request = urllib2.urlopen('http://{}:{}/display?'.format(self._args_ip,self._args_port) + get_params)
        response = get_request.read()
        #print(response)

def extractParamDict(file):
    paramDict = {}
    for line in file:
        split_line = line.rstrip().split(" ")
        split_line_r = split_line[1].split(",")
        split_line_l = split_line[0]
        transaction_num = split_line_l[1:len(split_line_l)-1]
        command = split_line_r[0]
        params = split_line_r[1:]
        params.append(transaction_num)
        if command == "DUMPLOG":
            continue
        if params[0] not in paramDict:
            paramDict[params[0]] = []
        paramDict[params[0]].append((command,params))
    return paramDict

def dump(ip,port,filename,transactionNum):
    get_params = urllib.urlencode({'filename':filename,'transactionNum':transactionNum})
    get_request = urllib2.urlopen('http://{}:{}/dumplog/all?'.format(ip,port) + get_params)
    return get_request.read().decode('utf-8')

def runThread(ip, port, paramsList):
    client = WorkloadGenerator(ip, port, paramsList)
    client.run()

def run(args):
    file = open(args[3], "r")
    ip_list = args[1].split(',')
    print ip_list
    port = args[2]
    transactionNum = args[4]
    paramDict = extractParamDict(file)
    print 'paramDicts: {}'.format(len(paramDict))
    i = 0
    for key in paramDict:
        t = threading.Thread(target=runThread,args=(ip_list[i], port, paramDict[key],))
        i = (i+1) % len(ip_list)
        t.start()
    while threading.active_count() > 1:
        time.sleep(5)
    time.sleep(20)
    try:
        filename = './dumpLOG'
        logfile = open(filename, "w+")
        for j in range(len(ip_list)):
            response = dump(ip_list[j],port,filename,transactionNum)
            logfile.write(response)
        logfile.close()
        print("Log written to {}".format(filename))
    except Exception as e:
        print 'DUMPLOG failed {}'.format(e)

if __name__ == "__main__":
    run(sys.argv)
    
