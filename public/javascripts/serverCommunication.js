function flag(x, y) {
    const message = {
        command: "flag",
        x: x,
        y: y
    };

    socket.send(JSON.stringify(message));
    getFlags();
}


function reveal(x, y) {
    const message = {
        command: "reveal",
        x: x,
        y: y
    };

    socket.send(JSON.stringify(message));
}


function initialField() {
    fetch('/json')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(jsonData => {
            handleJSONData(jsonData);
        })
        .catch(error => {
            console.error('There was a problem fetching the data:', error);
        });
}

function sendBombClick(row, col, size) {
    const message = {
        command: "bomb",
        size: size,
        x: row,
        y: col
    };

    socket.send(JSON.stringify(message));
}

function getFlags() {
    $.ajax({
        url: '/game/flagsLeft',
        method: 'GET',
        success: function (response) {
            const flagsLeftValue = response.flags_left;
            updateFlags(flagsLeftValue);
        },
        error: function (xhr, status, error) {
            console.error('Error fetching flags left:', error);
        }
    });
}
