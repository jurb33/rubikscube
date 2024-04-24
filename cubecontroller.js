
const express = require('express');
const axios = require('axios');
axios.defaults.baseURL = 'http://localhost:8080';
const path = require('path');

const app = express();
const port = 3000;

app.use(express.json());

/*
* defines route for Fetching data from the backend at baseURL and sends the user cube as a JSON
*/
app.get('/fetch-data', async (req, res) => {
    try {
        // Ensuring withCredentials is used properly for backend requests
        const response = await axios.get('/cube/fetch-data', { withCredentials: true });
        res.send(response.data);
    } catch (error) {
        console.error('Error fetching data from Spring Boot:', error);
        res.status(500).send('Failed to fetch data');
    }
});

/*
* Defines rout for sending commands to userId instance of cube
*/
app.post('/send-command/:commandType', async (req, res) => {
    const commandType = req.params.commandType;
    const pointer = parseInt(req.body.index);
    //Check valid input
    if (!(["U", "D", "L", "R", "HU", "HD"].includes(commandType)) || pointer == null || pointer < 0 || pointer > 2) {
        res.status(400).send("Invalid command or index");
        return;
    }
    try {
        const result = await sendCommandToSpringBoot(commandType, pointer);
        res.json(result);
    } catch (error) {
        console.error('Error sending command to Spring Boot:', error);
        res.status(500).send("Failed to send command to Spring Boot");
    }
});

/*
* sends command to springboot backend
* parses search params as such
* move='MOVE'&pointer=index

*/
async function sendCommandToSpringBoot(move, pointer) {
    try {
        const url = `/cube/command/`; 
        const params = new URLSearchParams({move, pointer}).toString();
        
        const response = await axios.post(url, params, { 
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            withCredentials: true
        });
        return response.data;
    } catch (error) {
        console.error('Error sending command to Spring Boot:', error);
        throw error;
    }
}

/*
* serves the html GUI in the index of the server
*/
app.get('/', (req, res) => {
    const filePath = path.resolve(__dirname, 'cubeGUI.html');
    res.sendFile(filePath, function (err) {
        if (err) {
            console.log('Error occurred while trying to send file:', err);
            res.status(500).send('Error occurred while trying to send file.');
        }
    });
});

/*
* starts this server
*/
app.listen(port, () => {
    console.log(`Server running on http://localhost:${port}`);
});
