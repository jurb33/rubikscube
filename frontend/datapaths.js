/*
* Makes a request to server to fetch cube info
*/
async function fetchCube() {
    const data =  fetch('/fetch-data')
.then(response => {
    if (!response.ok) {
        throw new Error('Data response was not ok! Status: ' + response.status);
    }
    return response.json();
})
return data;
}
/*
* Fetches command from the springboot server at the given endpoint:
* /send-command/${command}
*/
function fetchCommand(command) {
    fetch(`/send-command/${command}`, {
         method: 'POST',
         headers: {
      'Content-Type': 'application/json'
         },
         body: JSON.stringify({index: pointer.y })
     })
.then(response => {
if (!response.ok) {
 throw new Error('Data response was not ok!');
}
updateCubeGraphics(response.json());
//TODO call update graphics with the new solved parameter
});       
}