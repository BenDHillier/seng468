const request = require('request');
const config = require('../config.json');
const loadBalancer = require('../loadBalancer.js');

module.exports = (app) => {
    app.get('/display', (req, res) => {
        let host = config.url + loadBalancer.getDomain(req.query.userId) + config.port;
        request({uri: host + "display/", qs: req.query, method: "GET", json: true }, (err, res2, body) => {
            if (err) {
                res.status(500).send(err)
            } else {
                res.status(res2.statusCode).send(body);
            }
        });
    });
}