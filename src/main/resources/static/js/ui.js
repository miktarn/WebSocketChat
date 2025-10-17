'use strict';

import {getAvatarColor} from "./utils.js";
import {setVisible, state} from "./state.js";
import {connectToChat} from "./main.js";

var messageArea = document.querySelector('#messageArea');
var roomListPage = document.querySelector('#room-list-page');
var chatPage = document.querySelector('#chat-page');


export function drawMessage(message) {
    const messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        const avatarElement = document.createElement('i');
        const avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        const usernameElement = document.createElement('span');
        const usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    const textElement = document.createElement('p');
    const messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
}

export function drawRoomButton(roomName) {
    if (document.querySelector(`[data-room="${roomName}"]`)) {
        return;
    }

    const button = document.createElement('button');
    button.textContent = roomName;
    button.classList.add('room-button');

    button.addEventListener('click', function () {
        switchRoom(roomName);
    });
    roomListPage.appendChild(button);
}


function switchRoom(newRoom) {
    state.room = newRoom;
    if (!state.roomMessages.has(newRoom)) {
        console.log("Room is "+ newRoom + " and room messages is " + state.roomMessages)
        connectToChat(newRoom)
    } else {
        redrawChat();
    }
    setVisible(chatPage)
}

export function redrawChat() {
    document.querySelector('#roomTitle').textContent = state.room

    messageArea.innerHTML = '';

    const messages = state.roomMessages.get(state.room) || [];

    messages.forEach(message => drawMessage(message));

    messageArea.scrollTop = messageArea.scrollHeight;
}

export function updateInvitedUsersHeader() {
    const invitedUsersHeader = document.querySelector('#invitedUserList');
    invitedUsersHeader.textContent = state.invitedUsersCache.join(", ")
}
