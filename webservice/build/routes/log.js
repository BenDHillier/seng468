const request = require('request');
const config = require('../config.json');
const loadBalancer = require('../loadBalancer.js');

module.exports = (app) => {
    app.get('/dumplog/all/', (req, res) => {
        let host = config.url + loadBalancer.getDomain(req.query.userId) + config.port;
        request({uri: host + "dumplog/all/", qs: req.query, method: "POST", json: true }, (err, res2, body) => {
            if (err) {
                res.status(500).send(err);
            } else {
                res.status(res2.statusCode).send(body);
            }
        });
    });

    app.get('/dumplog/user/', (req, res) => {
        request({uri: host + "dumplog/user/", qs: req.query, method: "POST", json: true }, (err, res2, body) => {
            if (err) {
                res.status(500).send(err);
            } else {
                res.status(res2.statusCode).send(body);
            }
        });
    });
}