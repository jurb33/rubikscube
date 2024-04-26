
const express = require('express');
const axios = require('axios');
const cookieParser = require('cookie-parser');
const { v4: uuidv4 } = require('uuid');
const path = require('path');

axios.defaults.baseURL =  'http://localhost:3000';
const app = express();
const port = process.env.PORT || 8080;

app.use(express.json());
app.use(express.static(__dirname));
app.use(cookieParser());

/*
* defines route for Fetching data from the backend at baseURL and sends the user cube as a JSON
*/
app.get('/fetch-data', async (req, res) => {
    try {
        var userId = req.cookies['user_id'];
        if (!userId) {
            userId = uuidv4();
            res.cookie('user_id', userId, { maxAge: 24*60*60*7*1000, httpOnly: true });
        }
        // Ensuring withCredentials is used properly for backend requests
        const response = await axios.get(`/cube/fetch-data?user_id=` + userId);
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
    const user_id = req.cookies['user_id'];
    const commandType = req.params.commandType;
    const pointer = parseInt(req.body.index);
    //Check valid input
    if (!(["U", "D", "L", "R", "HU", "HD", "shuffle1", "shuffle2", "shuffle3", "clear"].includes(commandType)) || pointer == null || pointer < 0 || pointer > 2) {
        res.status(400).send("Invalid command or index");
        return;
    }
    try {
        const result = await sendCommandToSpringBoot(user_id, commandType, pointer);
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
async function sendCommandToSpringBoot(user_id, move, pointer) {
    try {
        const url = `/cube/command/`; 
        const params = new URLSearchParams({user_id, move, pointer}).toString();
       
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
app.get('/cube/isSolved', async (req, res) => {
    try {
        const user_id = req.cookies['user_id'];
        const response = await axios.get(`/cube/isSolved?user_id=` + user_id);
        res.send(response.data);
    } catch (error) {
        console.error("Error getting solved state,", + error);
    }
});

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
