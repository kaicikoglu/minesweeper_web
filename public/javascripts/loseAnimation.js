function handleBombClick(row, col, gridSize) {
    propagateShockwave(row, col, gridSize);
}

function propagateShockwave(row, col, gridSize) {
    const directions = [
        {x: -1, y: -1}, {x: -1, y: 0}, {x: -1, y: 1},
        {x: 0, y: -1}, /* Center cell */ {x: 0, y: 1},
        {x: 1, y: -1}, {x: 1, y: 0}, {x: 1, y: 1}
    ];

    const visited = new Set();
    visited.add(`${row}-${col}`);
    const queue = [{row, col, delay: 50}];

    while (queue.length > 0) {
        const {row: currRow, col: currCol, delay} = queue.shift();

        directions.forEach(direction => {
            const nextRow = currRow + direction.x;
            const nextCol = currCol + direction.y;
            const cellId = `${nextRow}-${nextCol}`;

            if (!visited.has(cellId) && isValidCell(nextRow, nextCol, gridSize)) {
                visited.add(cellId);
                const cell = $(`#${cellId}`);
                applyShockwaveEffect(cell, delay);
                queue.push({row: nextRow, col: nextCol, delay: delay + 50});
            }
        });
    }

    setTimeout(() => {
        showLostScreen();
    }, 2000);
}

function isValidCell(row, col, gridSize) {
    return row >= 0 && row < gridSize && col >= 0 && col < gridSize;
}

function applyShockwaveEffect(cell, delay) {
    setTimeout(() => {
        cell.addClass('shockwave'); // Apply the shockwave class to trigger the animation
        setTimeout(() => {
            cell.removeClass('shockwave'); // Remove the shockwave class after the animation duration
        }, 1000); // Adjust duration as needed to match the animation duration in your CSS
    }, delay);
}

function showLostScreen() {
    $('#game').hide();
    $('#lostScreen').show();
}