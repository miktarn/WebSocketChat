'use strict';

import {updateInvitedUsersHeader} from "./ui.js";

export const state = {
    username: null,
    room: null,
    roomMessages: new Map(),
    visibleContainer: document.querySelector('#auth-option-page'),
    invitedUsersCache: [],
    stompClient :null,
};

export function setVisible(domContainer) {
    state.visibleContainer.classList.add('hidden');
    domContainer.classList.remove('hidden');
    state.visibleContainer = domContainer;
}

export function initStompClient() {
    const socket = new SockJS('/ws');
    state.stompClient = Stomp.over(socket);
    state.stompClient.connect({}, function (frame) {
        console.log("Connected to " + frame)
    }, onError);
}

function onError(error) {
    const connectingElement = document.querySelector('.connecting');
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

export function addToInvitedUsersCache(userName, exists) {
    if (!exists) {
        console.log("User " + userName + " not exist!")
    } else if (state.invitedUsersCache.includes(userName) && state.username === userName) {
        console.log("User " + userName + " already invited!")
    } else {
        console.log("User " + userName + " founded")
        state.invitedUsersCache.push(userName);
        updateInvitedUsersHeader()
        inviteUserInput.value = '';
    }
}