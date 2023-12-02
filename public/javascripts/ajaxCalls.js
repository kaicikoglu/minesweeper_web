function flag(x, y) {
    fetch(`/game/setFlag/${x}/${y}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(jsonData => {
            handleJSONData(jsonData);
            getFlags();
        })
        .catch(error => {
            console.error('There was a problem fetching the data:', error);
        });
}


function reveal(x, y) {
    fetch(`/game/revealValue/${x}/${y}`)
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
