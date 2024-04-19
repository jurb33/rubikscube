const express = require('express');
const axios = require('axios');
const path = require('path');
const cors = require('cors');

const app = express();
const port = 3000;

app.use(express.json());
app.use(cors());
app.get('/fetch-data', async (req, res) => {
    try {
        const response = await axios.get('http://localhost:8080/data');
        res.send(response.data);
    } catch (error) {
        console.error('Error fetching data from Spring Boot:', error);
        res.status(500).send('Failed to fetch data');
    }
});

app.listen(port, () => {
    console.log(`cubecontroller.js server running on http://localhost:${port}`);
});

app.get('/', (req, res) => {
    const filePath = path.resolve(__dirname, 'cubeGUI.html');
    console.log(filePath);
    res.sendFile(filePath, function (err) {
        if (err) {
            console.log(err);
            res.status(500).send('Error occurred while trying to send file.');
        }
    });
});

// Endpoint to receive commands and forward them
app.post('/send-command/:commandType', async (req, res) => {
    const { commandType } = req.params;
    const { index } = req.body; 

    if (!["U", "D", "L", "R", "HU", "HD"].includes(commandType) || index < 0 || index > 2) {
        res.status(400).send("Invalid command or index");
        return;
    }

    try {
        const result = await sendCommandToSpringBoot(commandType, index);
        res.json(result);
    } catch (error) {
        res.status(500).send("Failed to send command to Spring Boot");
    }
});

// Generic function to send command to Spring Boot
async function sendCommandToSpringBoot(command, index) {
    try {
        // Modify this URL to where your Spring Boot server is listening
        const url = `http://localhost:8080/command/${command}/${index}`;
        const response = await axios.post(url);
        return response.data;
    } catch (error) {
        console.error('Error sending command to Spring Boot:', error);
        throw error;  // Throw the error to handle it in the calling function
    }
}





