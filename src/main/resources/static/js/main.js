'use strict';

import { state, setVisible} from "./state.js";
import {drawMessage, drawRoomButton, redrawChat} from './ui.js';
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
let connectToRoomInput = document.querySelector('#connectToRoomInput');
var addChatButton = document.querySelector('#addChatButton')

var stompClient = null;

function login(event) {
    event.preventDefault()
    state.username = document.querySelector('#name').value.trim();
    console.info("Hi "+ state.username)

    api.createUser(state.username)
        .then(response => response.data.chatNames.forEach(drawRoomButton))

    setVisible(addChatPage)
    addChatButton.classList.remove('hidden');
}

function createChatRoom(event) {
    state.room = createRoomInput.value.trim();
    console.info("Creating room: " + state.room + " " + state.username)

    if(state.username != null && state.room != null) {
        api.createChatRoom(state.room, [state.username])
            .then(() => connectToChat(state.room))
            .catch(function (error) {
                console.log(error);
            })
    }
    event.preventDefault();
}

function enterChatRoom(event) {
    state.room = connectToRoomInput.value.trim();
    console.info("Connecting to room: " + state.room + " " + state.username)

    if(state.username != null && state.room != null) {
        api.connectUserToChat(state.room, state.username)
            .then(() => connectToChat(state.room))
            .catch(function (error) {
                console.log(error);
            })
    }
    event.preventDefault();
}

function connectToChat(room) {
    setVisible(chatPage)

    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    state.roomMessages.set(room, []);
    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/' + state.room, onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
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
    drawRoomButton(state.room);
}

function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: state.username,
            content: messageInput.value,
            room: state.room,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
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

function addChat(event) {
    event.preventDefault();
    roomInput.value = '';
    setVisible(addChatPage)
}

usernameForm.addEventListener('submit', login, true)
document.getElementById("showConnectToChatPageButton")
    .addEventListener('click', () => setVisible(connectToChatPage))
document.getElementById("showCreateChatPageButton")
    .addEventListener('click', () => setVisible(createChatPage))
createChatForm.addEventListener('submit', createChatRoom, true)
enterChatForm.addEventListener('submit', enterChatRoom, true)
messageForm.addEventListener('submit', sendMessage, true)
document.getElementById("addChatButton").addEventListener('click', addChat)