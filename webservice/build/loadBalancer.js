const config = require('./config.json');
const servers = config.servers;
let myMap = new Map();
let count = 0;

module.exports = {
    getDomain: function(userId){
        if(!myMap.has(userId)){
            myMap.set(userId, servers[count]);
            count = (count + 1) % servers.length;
        }
        return myMap.get(userId)
    }
}