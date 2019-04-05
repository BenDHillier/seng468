const request = require('request');
const config = require('../config.json');
const loadBalancer = require('../loadBalancer.js');


module.exports = (app) => {
    app.get('/sellTrigger/amount/', (req, res) => {
        let host = config.url + loadBalancer.getDomain(req.query.userId) + config.port;
        request({uri: host + "sellTrigger/amount/", qs: req.query, method: "POST", json: true }, (err, res2, body) => {
            if (err) {
                res.status(500).send(err);
            } else {
                res.status(res2.statusCode).send(body);
            }
        });
    });

    app.get('/sellTrigger/trigger/', (req, res) => {
        let host = config.url + loadBalancer.getDomain(req.query.userId) + config.port;
        request({uri: host + "sellTrigger/trigger/", qs: req.query, method: "POST", json: true }, (err, res2, body) => {
            if (err) {
                res.status(500).send(err);
            } else {
                res.status(res2.statusCode).send(body);
            }
        });
    });

    app.get('/sellTrigger/cancel/', (req, res) => {
        let host = config.url + loadBalancer.getDomain(req.query.userId) + config.port;
        request({uri: host + "sellTrigger/cancel/", qs: req.query, method: "POST", json: true }, (err, res2, body) => {
            if (err) {
                res.status(500).send(err);
            } else {
                res.status(res2.statusCode).send(body);
            }
        });
    });
}