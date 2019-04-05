const config = require('./config.json');

module.exports = {
    convertNumber: function(number){
        if (/^\d+(\.\d\d)?$/.test(number)) {
            number = number.replace("\.", "");
            return number
        }else{
            return -1;
        }
    }
}