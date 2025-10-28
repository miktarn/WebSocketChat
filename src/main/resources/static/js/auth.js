'use strict';

import {state} from "./state.js";
import * as api from "./api.js";
import {setUpUser} from "./main.js";

const loginForm = document.querySelector('#loginForm');
loginForm.addEventListener('submit', login, true)
const sighInForm = document.querySelector('#sighInForm');
sighInForm.addEventListener('submit', sighIn, true)

function login(event) {
    event.preventDefault()
    state.username = document.querySelector('#loginUsername').value.trim();
    const password = document.querySelector('#loginPassword').value.trim();
    console.info("Login attempt "+ state.username)

    api.login(state.username, password)
        .then(response => setUpUser(response.data))
        .catch(error => console.error(error))
}

function sighIn(event) {
    event.preventDefault()
    state.username = document.querySelector('#sighInUsername').value.trim();
    const password = document.querySelector('#sighInPassword').value.trim();
    console.info("SighIn attempt "+ state.username)

    api.sighIn(state.username, password)
        .then(response => setUpUser(response.data))
        .catch(error => console.error(error))
}
