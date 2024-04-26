document.getElementById('shuffleSlider').addEventListener('input', function () {
    const difficultyLevel = document.getElementById('difficultyLevel');
    switch (this.value) {
    case '1':
        difficultyLevel.textContent = 'Easy';
        difficultyLevel.style.color = 'green';
        break;
    case '2':
        difficultyLevel.textContent = 'Medium';
        difficultyLevel.style.color = 'yellow';
        break;
    case '3':
        difficultyLevel.textContent = 'Hard';
        difficultyLevel.style.color = 'red';
        break;
    }
    });
    
    // Initialize text and color based on the default slider value
    document.getElementById('shuffleSlider').dispatchEvent(new Event('input'));
    /*
    * Handles user input to call commands and update pointer
    */
    document.addEventListener('keydown', function (event) {
        switch (event.key) {
            case 'w':
                if (pointer.horizontal) {
                    if (pointer.x === 0) {
                        fetchCommand('HU');
                    } else {
                        pointer.y = (pointer.y + 2) % 3;
                    }
                } else {
                    if (pointer.x === 1) {
                        pointer.y = (pointer.y + 2) % 3;
                    } else {
                        fetchCommand('U');
                    }
                }
                break;
    
            case 's':
                if (pointer.horizontal) {
                    if (pointer.x === 0) {
                        fetchCommand('HD');
                    } else {
                        pointer.y = (pointer.y + 1) % 3;
                    }
                } else {
                    if (pointer.x === 1) {
                        pointer.y = (pointer.y + 1) % 3;
                    } else {
                        fetchCommand('D');
                    }
                }
                break;
            case 'a':
                if (pointer.x === 0) {
                    pointer.y = (pointer.y - 1 + 3) % 3;
    
                } else {
                    fetchCommand('L');
                }
                break;
            case 'd':
                if (pointer.x === 0) {
                    pointer.y = (pointer.y + 1) % 3;
                } else {
                    fetchCommand('R');
                }
                break;
            case ' ':
                pointer.x = pointer.x === 1 ? 0 : 1;
                break;
            case 'h':
                pointer.horizontal = !pointer.horizontal;
                break;
            case 'ArrowLeft':
            case 'ArrowRight':
                const angle = event.key === 'ArrowLeft' ? Math.PI / 36 : -Math.PI / 36;
                camera.position.applyAxisAngle(new THREE.Vector3(0, 1, 0), angle);
                break;
            case 'ArrowUp':
            case 'ArrowDown':
                const tilt = event.key === 'ArrowUp' ? Math.PI / 18 : -Math.PI / 18;
                camera.position.applyAxisAngle(new THREE.Vector3(1, 0, 0), tilt);
                
                break;
        }
        camera.lookAt(scene.position);
        highlightSlice();
    });
    function togglePopup() {
    var popup = document.getElementById('popup');
    if (popup.style.display === 'none' || popup.style.display === '') {
    popup.style.display = 'block';
    } else {
    popup.style.display = 'none';
    }
    }