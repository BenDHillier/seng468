const request = require('request');
const config = require('../config.json');
const loadBalancer = require('../loadBalancer.js');

module.exports = (app) => {
    app.get('/quote', (req, res) => {
        let host = config.url + loadBalancer.getDomain(req.query.userId) + config.port;
        request({uri: host + "quote/", qs: req.query, method: "GET", json: true }, (err, res2, body) => {
            if (err) {
                res.status(500).send(err)
            } else {
                console.log(body)
                res.status(res2.statusCode).send(body);
            }
        });
    });
}