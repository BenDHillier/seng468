const request = require('request');
const config = require('../config.json');
const loadBalancer = require('../loadBalancer.js');


module.exports = (app) => {
    app.get('/sell/create/', (req, res) => {
        let host = config.url + loadBalancer.getDomain(req.query.userId) + config.port;
        request({uri: host + "sell/create/", qs: req.query, method: "POST", json: true }, (err, res2, body) => {
            if (err) {
                res.status(500).send(err);
            } else {
                res.status(res2.statusCode).send(body);
            }
        });
    });

    app.get('/sell/commit/', (req, res) => {
        let host = config.url + loadBalancer.getDomain(req.query.userId) + config.port;
        request({uri: host + "sell/commit/", qs: req.query, method: "POST", json: true }, (err, res2, body) => {
            if (err) {
                res.status(500).send(err);
            } else {
                res.status(res2.statusCode).send(body);
            }
        });
    });

    app.get('/sell/cancel/', (req, res) => {
        let host = config.url + loadBalancer.getDomain(req.query.userId) + config.port;
        request({uri: host + "sell/cancel/", qs: req.query, method: "POST", json: true }, (err, res2, body) => {
            if (err) {
                res.status(500).send(err);
            } else {
                res.status(res2.statusCode).send(body);
            }
        });
    });
}