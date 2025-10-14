'use strict';

export const state = {
    username: null,
    room: null,
    roomMessages: new Map(),
    visibleContainer: document.querySelector('#username-page'),
    invitedUsers: [],
    stompClient :null,
};

export function setVisible(domContainer) {
    state.visibleContainer.classList.add('hidden');
    domContainer.classList.remove('hidden');
    state.visibleContainer = domContainer;
}

export function initStompClient() {
    var socket = new SockJS('/ws');
    state.stompClient = Stomp.over(socket);
    state.stompClient.connect({}, function (frame) {
        console.log("Connected to " + frame)
    }, onError);
}

function onError(error) {
    var connectingElement = document.querySelector('.connecting');
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}