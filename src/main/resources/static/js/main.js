'use strict';

import { state, setVisible, initStompClient} from "./state.js";
import {drawMessage, drawRoomButton, redrawChat, updateInvitedUsersHeader} from './ui.js';
import * as api from './api.js';

var addChatPage = document.querySelector('#add-chat-page');
var createChatPage = document.querySelector('#create-chat-page');
var connectToChatPage = document.querySelector('#enter-chat-page');
var chatPage = document.querySelector('#chat-page');

var usernameForm = document.querySelector('#usernameForm');
var createChatForm = document.querySelector('#createChatForm');
var messageForm = document.querySelector('#messageForm');
var enterChatForm = document.querySelector('#enterChatForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
let createRoomInput = document.querySelector('#createRoomInput');
let enterChatInput = document.querySelector('#enterChatInput');
var addChatButton = document.querySelector('#addChatButton')

initStompClient()

function login(event) {
    event.preventDefault()
    state.username = document.querySelector('#name').value.trim();
    console.info("Hi "+ state.username)

    api.createUser(state.username)
        .then(response => response.data.chatNames.forEach(drawRoomButton))
        .catch(error => console.error(error))

    setVisible(addChatPage)
    addChatButton.classList.remove('hidden');
}

function createChatRoom(event) {
    state.room = createRoomInput.value.trim();
    console.info("Creating room: " + state.room + " " + state.username)

    if(state.username != null && state.room != null) {
        api.createChatRoom(state.room, state.invitedUsers)
            .then(() => {
                connectToChat(state.room);
                drawRoomButton(state.room);
                setVisible(chatPage)
            })
            .catch(error => console.error(error))
    }
    event.preventDefault();
}

function enterChatRoom(event) {
    event.preventDefault();

    state.room = enterChatInput.value.trim();
    console.info("Connecting to room: " + state.room + " " + state.username)

    if(state.username != null && state.room != null) {
        api.connectUserToChat(state.room, state.username)
            .then(() => {
                connectToChat(state.room);
                drawRoomButton(state.room);
                setVisible(chatPage)
            })
            .catch(error => console.error(error))
    }
}

export function connectToChat(room) {
    state.roomMessages.set(room, []);

    state.stompClient.subscribe('/topic/' + state.room, onMessageReceived);

    state.stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: state.username, room: state.room, type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');
    api.fetchRoomMessages(state.room)
        .then(response => {
            console.log("HTTP GET response:", response.data);
            state.roomMessages.set(state.room, response.data);
            redrawChat()
        })
        .catch(error => console.error(error))
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && state.stompClient) {
        var chatMessage = {
            sender: state.username,
            content: messageInput.value,
            room: state.room,
            type: 'CHAT'
        };
        state.stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    if (message.room === state.room) {
        drawMessage(message)
        messageArea.scrollTop = messageArea.scrollHeight;
    }

    state.roomMessages.get(message.room).push(message);
}

usernameForm.addEventListener('submit', login, true)

function showEnterChatPage() {
    enterChatInput.value = '';
    setVisible(connectToChatPage)
}

function showCreateChatPage() {
    createRoomInput.value = '';
    state.invitedUsers = [state.username]
    updateInvitedUsersHeader()
    setVisible(createChatPage)
}

function inviteUser() {
    let inviteUserInput = document.querySelector("#inviteUserInput");
    let userName = inviteUserInput.value.trim();
    api.userExists(userName)
        .then(response => addInvitedName(userName, response.data))
        .catch(error => console.error(error))
}

function addInvitedName(userName, exists) {
    if (!exists) {
        console.log("User " + userName + " not exist!")
    } else if (state.invitedUsers.includes(userName)) {
        console.log("User " + userName + " already invited!")
    } else {
        console.log("User " + userName + " founded")
        state.invitedUsers.push(userName);
        updateInvitedUsersHeader()
        inviteUserInput.value = '';
    }
}

document.getElementById("inviteUserButton").addEventListener('click', inviteUser)
document.getElementById("showEnterChatPageButton").addEventListener('click', showEnterChatPage)
document.getElementById("showCreateChatPageButton").addEventListener('click', showCreateChatPage)
createChatForm.addEventListener('submit', createChatRoom, true)
enterChatForm.addEventListener('submit', enterChatRoom, true)
messageForm.addEventListener('submit', sendMessage, true)
document.getElementById("addChatButton").addEventListener('click', () => setVisible(addChatPage))