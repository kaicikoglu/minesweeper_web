let clickCount = 0;
let timer;
const socket = new WebSocket("ws://localhost:9000/socket");

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

function handleJSONData(jsonData) {
    const gridContainer = document.getElementById('minesweeper-grid');
    gridContainer.innerHTML = '';
    const rows = jsonData.field.sizeRow;
    const cols = jsonData.field.sizeCol;

    for (let i = 0; i < rows; i++) {
        const row = document.createElement('div');
        row.classList.add("cell-container");
        for (let j = 0; j < cols; j++) {
            const cell = jsonData.field.cells.find(c => c.row === i && c.col === j);

            // Create a table cell (td) for each cell in the row
            const cellDiv = document.createElement("div");
            cellDiv.classList.add("cell");

            if (cell) {
                const first = cell.cell.first;
                const second = cell.cell.second;

                if (first === "■") {
                    const button = document.createElement('button');
                    button.classList.add('button');
                    cellDiv.addEventListener('click', () => {
                        clickCount++;
                        if (clickCount === 1) {
                            timer = setTimeout(async function () {
                                if (second === "✴") {
                                    reveal(i, j);
                                    await sleep(200);
                                    sendBombClick(i, j, rows);
                                } else {
                                    reveal(i, j);
                                }
                                clickCount = 0
                            }, 300);
                        } else if (clickCount === 2) {
                            clearTimeout(timer);
                            flag(i, j);
                            clickCount = 0;
                        }
                    });
                    cellDiv.appendChild(button);
                } else if (first === "⚑") {
                    const flagDiv = document.createElement('div');
                    flagDiv.classList.add('flagged');
                    flagDiv.textContent = "⚑";
                    flagDiv.id = `${i}-${j}`;
                    flagDiv.addEventListener('click', () => flag(i, j));
                    cellDiv.appendChild(flagDiv);
                } else if (first === "✴") {
                    const bombDiv = document.createElement('div');
                    bombDiv.classList.add('cell-type', 'bomb');
                    bombDiv.textContent = '\uD83D\uDCA3';
                    bombDiv.id = `${i}-${j}`;
                    cellDiv.appendChild(bombDiv);
                } else if (first === "□") {
                    const hiddenDiv = document.createElement('div');
                    hiddenDiv.classList.add('cell-type', 'empty-cell');
                    hiddenDiv.id = `${i}-${j}`;
                    cellDiv.appendChild(hiddenDiv);
                } else if (first === "1") {
                    const numberDiv = document.createElement('div');
                    numberDiv.classList.add('cell-type', `number-${first}`);
                    numberDiv.textContent = first;
                    numberDiv.id = `${i}-${j}`;
                    cellDiv.appendChild(numberDiv);
                } else if (first === "2") {
                    const numberDiv = document.createElement('div');
                    numberDiv.classList.add('cell-type', `number-${first}`);
                    numberDiv.textContent = first;
                    numberDiv.id = `${i}-${j}`;
                    cellDiv.appendChild(numberDiv);
                } else if (first === "3") {
                    const numberDiv = document.createElement('div');
                    numberDiv.classList.add('cell-type', `number-${first}`);
                    numberDiv.textContent = first;
                    numberDiv.id = `${i}-${j}`;
                    cellDiv.appendChild(numberDiv);
                } else if (first === "4") {
                    const numberDiv = document.createElement('div');
                    numberDiv.classList.add('cell-type', `number-${first}`);
                    numberDiv.textContent = first;
                    numberDiv.id = `${i}-${j}`;
                    cellDiv.appendChild(numberDiv);
                } else if (first === "5") {
                    const numberDiv = document.createElement('div');
                    numberDiv.classList.add('cell-type', `number-${first}`);
                    numberDiv.textContent = first;
                    numberDiv.id = `${i}-${j}`;
                    cellDiv.appendChild(numberDiv);
                } else if (first === "6") {
                    const numberDiv = document.createElement('div');
                    numberDiv.classList.add('cell-type', `number-${first}`);
                    numberDiv.textContent = first;
                    numberDiv.id = `${i}-${j}`;
                    cellDiv.appendChild(numberDiv);
                } else if (first === "7") {
                    const numberDiv = document.createElement('div');
                    numberDiv.classList.add('cell-type', `number-${first}`);
                    numberDiv.textContent = first;
                    numberDiv.id = `${i}-${j}`;
                    cellDiv.appendChild(numberDiv);
                } else if (first === "8") {
                    const numberDiv = document.createElement('div');
                    numberDiv.classList.add('cell-type', `number-${first}`);
                    numberDiv.textContent = first;
                    numberDiv.id = `${i}-${j}`;
                    cellDiv.appendChild(numberDiv);
                }
            } else {
                const emptyDiv = document.createElement('div');
                emptyDiv.classList.add('cell', 'empty');
                cellDiv.appendChild(emptyDiv);
            }
            row.appendChild(cellDiv);
        }
        gridContainer.appendChild(row);
    }
}

function connectWebSockets() {
    socket.onopen = () => {
        console.log('WebSocket connection opened.');
    };

    socket.onmessage = (event) => {
        const gameData = JSON.parse(event.data);

        if (gameData.event === "bombClick") {
            handleBombClick(gameData.row, gameData.col, gameData.size)
        } else {
            handleJSONData(gameData);
            getFlags();
        }
    };

    socket.onclose = () => {
        console.log('WebSocket connection closed.');
    };

    socket.onerror = function (error) {
        console.log('Error in Websocket Occured: ' + error);
    };
}

function updateFlags(flags) {
    $('#flagsLeft').text("Flags Left: " + flags);
}

$(document).ready(function () {
    initialField();
    getFlags();
    connectWebSockets();
});


