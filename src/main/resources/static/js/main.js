'use strict';

import { state, setVisible, initStompClient, addToInvitedUsersCache} from "./state.js";
import {drawMessage, drawRoomButton, redrawChat, updateInvitedUsersHeader} from './ui.js';
import * as api from './api.js';

const addChatPage = document.querySelector('#add-chat-page');
const createChatPage = document.querySelector('#create-chat-page');
const connectToChatPage = document.querySelector('#enter-chat-page');
const chatPage = document.querySelector('#chat-page');
const createChatForm = document.querySelector('#createChatForm');
const messageForm = document.querySelector('#messageForm');
const enterChatForm = document.querySelector('#enterChatForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');
const createRoomInput = document.querySelector('#createRoomInput');
const enterChatInput = document.querySelector('#enterChatInput');
const addChatButton = document.querySelector('#addChatButton')
const inviteUserInput = document.querySelector("#inviteUserInput");

document.getElementById("inviteUserButton").addEventListener('click', inviteUser)
document.getElementById("showEnterChatPageButton").addEventListener('click', showEnterChatPage)
document.getElementById("showEnterChatPageButton").addEventListener('click', showEnterChatPage)
document.getElementById("showLoginPageButton").addEventListener('click', showLoginPage)
document.getElementById("showLoginPageButton2").addEventListener('click', showLoginPage)
document.getElementById("showSighInPageButton").addEventListener('click', showSighInPage)
document.getElementById("showSighInPageButton2").addEventListener('click', showSighInPage)
document.getElementById("showCreateChatPageButton").addEventListener('click', showCreateChatPage)
createChatForm.addEventListener('submit', createChatRoom, true)
enterChatForm.addEventListener('submit', enterChatRoom, true)
messageForm.addEventListener('submit', sendMessage, true)
document.getElementById("addChatButton").addEventListener('click', () => setVisible(addChatPage))


initStompClient()

export function setUpUser(userData) {
    state.stompClient.subscribe('/invites/' + state.username, onInviteReceived);
    setVisible(addChatPage)
    addChatButton.classList.remove('hidden');
    userData.chatNames.forEach(drawRoomButton)
}

function onInviteReceived(payload) {
    const inviteRoom = payload.body

    if (state.roomMessages.has(inviteRoom)) {
        console.log("Invalid invite: user " + state.username + " already in " + inviteRoom)
    } else {
        console.log("Processing invite to " + inviteRoom)
        connectToChat(inviteRoom)
        drawRoomButton(inviteRoom)
    }
}

function createChatRoom(event) {
    state.room = createRoomInput.value.trim();
    console.info("Creating room: " + state.room + " " + state.username)

    if(state.username != null && state.room != null) {
        api.createChatRoom(state.room, state.invitedUsersCache, state.username)
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

    state.stompClient.subscribe('/topic/' + room, onMessageReceived);

    api.postMessage(room, state.username, api.MESSAGE_TYPE_JOIN)
        .then(() => renderRoomMessages(room))
        .catch(error => console.error(error))
}

function renderRoomMessages(room) {
    connectingElement.classList.add('hidden');
    api.fetchRoomMessages(room)
        .then(response => {
            console.log("HTTP GET response:", response.data);
            state.roomMessages.set(room, response.data);
            redrawChat()
        })
        .catch(error => console.error(error))
}

function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if(messageContent && state.stompClient) {
        const chatMessage = {
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
    const message = JSON.parse(payload.body);

    if (message.room === state.room) {
        drawMessage(message)
        messageArea.scrollTop = messageArea.scrollHeight;
    }

    state.roomMessages.get(message.room).push(message);
}

function showEnterChatPage() {
    enterChatInput.value = '';
    setVisible(connectToChatPage)
}

function showCreateChatPage() {
    createRoomInput.value = '';
    state.invitedUsersCache = []
    updateInvitedUsersHeader()
    setVisible(createChatPage)
}

function inviteUser() {
    const userName = inviteUserInput.value.trim();
    api.userExists(userName)
        .then(response => addToInvitedUsersCache(userName, response.data))
        .catch(error => console.error(error))
}

function showLoginPage() {
    document.querySelector('#loginForm').reset();
    const loginPage = document.querySelector("#login-page");
    setVisible(loginPage)
}

function showSighInPage() {
    document.querySelector('#sighInForm').reset();
    const sighInPage = document.querySelector("#sighIn-page");
    setVisible(sighInPage)
}
