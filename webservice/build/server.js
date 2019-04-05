const express = require('express');
const app = express();

require('./routes')(app);

app.get('/', (req, res) => {
    res.send('LANDING_PAGE');
});

// Listen to the App Engine-specified port, or 8181 otherwise
const PORT = process.env.PORT || 8181;
app.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}...`);
});

