import httplib
import threading
import thread
import urllib
import urllib2
import sys
import time

# TODO: SET_BUY_AMOUNT, CANCEL_SET_BUY, SET_BUY_TRIGGER, SET_SELL_AMOUNT, CANCEL_SET_SELL, SET_SELL_TRIGGER
# Will do the above functions once triggers are in place

# TODO: DISPLAY_SUMMARY
# Will do DISPLAY_SUMMARY once logging is in place

class WorkloadGenerator:
    def __init__(self, *args):
        try:
            if( len(args[0]) < 4 ):
                raise TypeError("Incorrect number of arguments. Try python2 WorkloadGenerator webserver_ip port workloadfile")
            self._args_ip = args[0][1]
            self._args_port = args[0][2]
            self._args_filename = args[0][3]
            self._httpconnection = httplib.HTTPConnection(self._args_ip, self._args_port)
            print( "CLIENT ARGS [ip]: {} [port]: {} [filename]: {}".format(self._args_ip,self._args_port,self._args_filename) )

        except httplib.HTTPException:
            print("Error. Could not create HTTPConnection")
            sys.exit()
        except TypeError as e:
            print e.message
            sys.exit()

    def run(self):
        file = open("./WorkloadFiles/{}".format(self._args_filename), "r")
        paramDict = {}
        for line in file:
            split_line = line.rstrip().split(" ")
            split_line_r = split_line[1].split(",")
            split_line_l = split_line[0]
            transaction_num = split_line_l[1:len(split_line_l)-1]
            command = split_line_r[0]
            params = split_line_r[1:]
            params.append(transaction_num)
            if params[0] not in paramDict and command != "DUMPLOG":
                 paramDict[params[0]] = []
            if command != "DUMPLOG":
		 paramDict[params[0]].append((command,params))
        for key in paramDict:
            t = threading.Thread(target=self.runThread,args=(paramDict[key],))
            t.start()
            time.sleep(.250)

    def runThread(self,paramsList):
        for (command,params) in paramsList:
            print("\n[THREAD {}] sending: ".format(thread.get_ident()) + command + " with params {}".format(params))
            self.handleCommand(command,params)

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
            sef.dumplogRequest(params)
        else:
            print("command was " + cmd)

    #POST
    def setSellTriggerRequest(self,params):
        try:
            put_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'stockCost':int(float(params[2])* 100), 'transactionNum':params[3]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/sellTrigger/trigger', put_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            print("{} {}".format(data,response.status))
        except Exception as e:
            print "ADD,{},{} failed due to exception {}".format(params[0],params[1],e)

    #POST
    def setSellAmountRequest(self,params):
        try:
            put_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'stockAmount':int(float(params[2]) * 100), 'transactionNum':params[3]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/sellTrigger/amount', put_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            print("{} {}".format(data,response.status))
        except Exception as e:
            print "ADD,{},{} failed due to exception {}".format(params[0],params[1],e)

    #POST
    def cancelSetSellRequest(self,params):
        try:
            put_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1], 'transactionNum':params[2]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/sellTrigger/cancel', put_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            print("{} {}".format(data,response.status))
        except Exception as e:
            print "CANCEL SET BUY,{},{} failed due to exception {}".format(params[0],params[1],e)
    #POST
    def setBuyTriggerRequest(self,params):
        try:
            put_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'stockCost':int(float(params[2])* 100), 'transactionNum':params[3]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/buyTrigger/trigger', put_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            print("{} {}".format(data,response.status))
        except Exception as e:
            print "ADD,{},{} failed due to exception {}".format(params[0],params[1],e)

    #POST
    def setBuyAmountRequest(self,params):
        try:
            put_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'stockAmount':int(float(params[2]) * 100), 'transactionNum':params[3]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/buyTrigger/amount', put_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            print("{} {}".format(data,response.status))
        except Exception as e:
            print "ADD,{},{} failed due to exception {}".format(params[0],params[1],e)

    #POST
    def cancelSetBuyRequest(self,params):
        try:
            put_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1], 'transactionNum':params[2]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/buyTrigger/cancel', put_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            print("{} {}".format(data,response.status))
        except Exception as e:
            print "CANCEL SET BUY,{},{} failed due to exception {}".format(params[0],params[1],e)

    #PUT
    def addRequest(self,params):
        try:
            # TODO: Eventually will have to send unformatted string of float val
            # for now will have to send as a value formatted to integer for testing purposes
            put_params = urllib.urlencode({'userId':params[0],'amount':int(float(params[1]) * 100), 'transactionNum':params[2]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('PUT', '/add', put_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            print("{} {}".format(data,response.status))
        except Exception as e:
            print "ADD,{},{} failed due to exception {}".format(params[0],params[1],e)

    #GET
    def quoteRequest(self,params):
        try:
            get_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'transactionNum':params[2]})
            get_request = urllib2.urlopen('http://{}:{}/quote?'.format(self._args_ip,self._args_port) + get_params)
            response = get_request.read()
            print(response)
        except Exception as e:
            print "QUOTE,{},{} failed due to exception {}".format(params[0],params[1],e)

    #POST
    def buyRequest(self,params):
        try:
            post_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'amount':int(float(params[2]) * 100),'transactionNum':params[3]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/buy/create', post_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            print("{} {}".format(data,response.status))
        except Exception as e:
            print "BUY,{},{},{} failed due to exception {}".format(params[0],params[1],params[2],e)

    #POST
    # TODO: confirm this is the /create route in server
    def commitBuyRequest(self,params):
        try:
            post_params = urllib.urlencode({'userId':params[0],'transactionNum':params[1]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/buy/commit', post_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            print("{} {}".format(data,response.status))
        except Exception as e:
            print "COMMIT_BUY,{} failed due to exception {}".format(params[0],e)

    #POST
    def cancelBuyRequest(self,params):
        try:
            post_params = urllib.urlencode({'userId':params[0],'transactionNum':params[1]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/buy/cancel', post_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            print("{} {}".format(data,response.status))
        except Exception as e:
            print "CANCEL_BUY,{} failed due to exception {}".format(params[0],e)

    #POST
    def sellRequest(self,params):
        try:
            post_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'amount':int(float(params[2]) * 100),'transactionNum':params[3]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/sell/create', post_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            print("{} {}".format(data,response.status))
        except Exception as e:
            print "SELL,{},{} failed due to exception {}".format(params[0],params[1],e)

    #POST
    def commitSellRequest(self,params):
        try:
            post_params = urllib.urlencode({'userId':params[0],'transactionNum':params[1]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/sell/commit', post_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            print("{} {}".format(data,response.status))
        except Exception as e:
            print "COMMIT_SELL,{} failed due to exception {}".format(params[0],e)

    #POST
    def cancelSellRequest(self,params):
        try:
            post_params = urllib.urlencode({'userId':params[0],'transactionNum':params[1]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/sell/cancel', post_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            print("{} {}".format(data,response.status))
        except Exception as e:
            print "CANCEL_SELL,{} failed due to exception {}".format(params[0],e)

    #GET
    def dumplogRequest(self,params):
        try:
            get_params = urllib.urlencode({'filename':params[0],'transactionNum':params[1]})
            get_request = urllib2.urlopen('http://{}:{}/dumplog/all?'.format(self._args_ip,self._args_port) + get_params)
            response = get_request.read()
            logfile = open(params[0], "w+")
            logfile.write(response)
            logfile.close
            print("Log written to {}".format(params[0]))
            #print(response)
        except Exception as e:
            print "DUMPLOG,{},{} failed due to exception {}".format(params[0],params[1],e)


if __name__ == "__main__":
    client = WorkloadGenerator(sys.argv)
    client.run()
