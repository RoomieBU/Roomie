const express = require('express');
const path = require('path');
const app = express();
const port = 8000;

app.use(express.static(path.join(__dirname, 'build')));

app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'login.html'));
});

app.listen(port, '0.0.0.0', () => {
    console.log(`Server is running at http://localhost:${port}`);
});
