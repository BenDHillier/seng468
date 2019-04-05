const request = require('request');
const config = require('../config.json');
const loadBalancer = require('../loadBalancer.js');
const moneyConv = require('../convertMoney.js');


module.exports = (app) => {
    app.get('/buyTrigger/amount/', (req, res) => {
        let host = config.url + loadBalancer.getDomain(req.query.userId) + config.port;
        let convertedMoney = moneyConv.convertNumber(req.query.amount);
        if (convertedMoney < 0) {
            res.status(500).send("invalid money amount");
            return
        }else{
            req.query.amount = convertedMoney;
        }
        request({uri: host + "buyTrigger/amount/", qs: req.query, method: "POST", json: true }, (err, res2, body) => {
            if (err) {
                res.status(500).send(err);
            } else {
                res.status(res2.statusCode).send(body);
            }
        });
    });

    app.get('/buyTrigger/trigger/', (req, res) => {
        let host = config.url + loadBalancer.getDomain(req.query.userId) + config.port;
        let convertedMoney = moneyConv.convertNumber(req.query.amount);
        if (convertedMoney < 0) {
            res.status(500).send("invalid money amount");
            return
        }else{
            req.query.amount = convertedMoney;
        }
        request({uri: host + "buyTrigger/trigger/", qs: req.query, method: "POST", json: true }, (err, res2, body) => {
            if (err) {
                res.status(500).send(err);
            } else {
                res.status(res2.statusCode).send(body);
            }
        });
    });

    app.get('/buyTrigger/cancel/', (req, res) => {
        let host = config.url + loadBalancer.getDomain(req.query.userId) + config.port;
        request({uri: host + "buyTrigger/cancel/", qs: req.query, method: "POST", json: true }, (err, res2, body) => {
            if (err) {
                res.status(500).send(err);
            } else {
                res.status(res2.statusCode).send(body);
            }
        });
    });
}