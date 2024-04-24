const express = require('express');
const axios = require('axios');
const path = require('path');

const app = express();
const port = 3000;

app.use(express.json());

var cube = {
    frontFace: [[0, 1, 0], [1, 1, 1], [0, 1, 0]],
    backFace: [[1, 0, 2], [1, 0, 0], [1, 0, 2]],
    leftFace: [[4, 3, 3], [3, 2, 3], [3, 3, 5]],
    rightFace: [[1, 1, 1], [1, 3, 1], [1, 1, 1]],
    upFace: [[2, 2, 2], [2, 4, 2], [2, 2, 2]],
    downFace: [[4, 4, 4], [4, 5, 4], [4, 4, 4]]
};



// Define the route to fetch data from the Spring Boot backend
app.get('/fetch-data', async (req, res) => {
    try {
        // Ensuring withCredentials is used properly for backend requests
        const response = await axios.get('http://localhost:8080/cube/fetch-data', { withCredentials: true });
        res.send(response.data);
    } catch (error) {
        console.error('Error fetching data from Spring Boot:', error);
        res.status(500).send('Failed to fetch data');
    }
});

// Define the route to send commands to the Spring Boot backend
app.post('/send-command/:commandType', async (req, res) => {
    const commandType = req.params.commandType;
    const pointer = parseInt(req.body.index);
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

// Serve the HTML file for the GUI
app.get('/', (req, res) => {
    const filePath = path.resolve(__dirname, 'cubeGUI.html');
    res.sendFile(filePath, function (err) {
        if (err) {
            console.log('Error occurred while trying to send file:', err);
            res.status(500).send('Error occurred while trying to send file.');
        }
    });
});

// Function to send commands to the Spring Boot server
async function sendCommandToSpringBoot(move, pointer) {
    try {
        const url = `http://localhost:8080/cube/command/`;
        const params = new URLSearchParams({move, pointer}).toString();
        
        const response = await axios.post(url, params, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            withCredentials: true
        });
        console.log(response.data);
        return response.data;
    } catch (error) {
        console.error('Error sending command to Spring Boot:', error);
        throw error;
    }
}

// Start the server
app.listen(port, () => {
    console.log(`Server running on http://localhost:${port}`);
});
