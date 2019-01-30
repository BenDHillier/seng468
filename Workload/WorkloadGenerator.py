import httplib
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
        for line in file:
            split_line = line.rstrip().split(" ")[1].split(",")
            command = split_line[0]
            params = split_line[1:]
            print("sending: " + command + " with params {}".format(params))
            self.handleCommand(command,params)
            #time.sleep(.250)

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
        else:
            print("command was " + cmd)

    #PUT
    def addRequest(self,params):
        try:
            # TODO: Eventually will have to send unformatted string of float val
            # for now will have to send as a value formatted to integer for testing purposes
            put_params = urllib.urlencode({'userId':params[0],'amount':int(float(params[1]))})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('PUT', '/add', put_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            if( response.status == 200 ):
                print("Response from server: " + data)
            else:
                print(response.status)
        except Exception as e:
            print "ADD,{},{} failed due to exception {}".format(params[0],params[1],e)

    #GET
    def quoteRequest(self,params):
        try:
            get_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1]})
            get_request = urllib2.urlopen('http://{}:{}/quote?'.format(self._args_ip,self._args_port) + get_params)
            response = get_request.read()
            print(response)
        except Exception as e:
            print "QUOTE,{},{} failed due to exception {}".format(params[0],params[1],e)

    #POST
    def buyRequest(self,params):
        try:
            post_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'amount':int(float(params[2]))})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/buy/create', post_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            if( response.status == 200 ):
                print("Response from server: " + data)
            else:
                print(response.status)
        except Exception as e:
            print "BUY,{},{},{} failed due to exception {}".format(params[0],params[1],params[2],e)

    #POST
    # TODO: confirm this is the /create route in server
    def commitBuyRequest(self,params):
        try:
            post_params = urllib.urlencode({'userId':params[0]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/buy/commit', post_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            if( response.status == 200 ):
                print("Response from server: " + data)
            else:
                print(response.status)
        except Exception as e:
            print "COMMIT_BUY,{} failed due to exception {}".format(params[0],e)

    #POST
    def cancelBuyRequest(self,params):
        try:
            post_params = urllib.urlencode({'userId':params[0]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/buy/cancel', post_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            if( response.status == 200 ):
                print("Response from server: " + data)
            else:
                print(response.status)
        except Exception as e:
            print "CANCEL_BUY,{} failed due to exception {}".format(params[0],e)

    #POST
    def sellRequest(self,params):
        try:
            post_params = urllib.urlencode({'userId':params[0],'stockSymbol':params[1],'amount':int(float(params[2]))})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/sell/create', post_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            if( response.status == 200 ):
                print("Response from server: " + data)
            else:
                print(response.status)
        except Exception as e:
            print "SELL,{},{} failed due to exception {}".format(params[0],params[1],e)

    #POST
    def commitSellRequest(self,params):
        try:
            post_params = urllib.urlencode({'userId':params[0]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/sell/commit', post_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            if( response.status == 200 ):
                print("Response from server: " + data)
            else:
                print(response.status)
        except Exception as e:
            print "COMMIT_SELL,{} failed due to exception {}".format(params[0],e)

    #POST
    def cancelSellRequest(self,params):
        try:
            post_params = urllib.urlencode({'userId':params[0]})
            headers = {"Content-type": "application/x-www-form-urlencoded"}
            self._httpconnection.request('POST', '/sell/cancel', post_params, headers)
            response = self._httpconnection.getresponse()
            data = response.read()
            if( response.status == 200 ):
                print("Response from server: " + data)
            else:
                print(response.status)
        except Exception as e:
            print "CANCEL_SELL,{} failed due to exception {}".format(params[0],e)


if __name__ == "__main__":
    client = WorkloadGenerator(sys.argv)
    client.run()
