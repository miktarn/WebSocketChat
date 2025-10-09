'use strict';

import { state, setVisible} from "./state.js";
import {drawMessage, drawRoomButton, redrawChat} from './ui.js';
import { createUser, createChatRoom, fetchRoomMessages } from './api.js';

var addChatPage = document.querySelector('#add-chat-page');
var chatPage = document.querySelector('#chat-page');

var usernameForm = document.querySelector('#usernameForm');
var addChatForm = document.querySelector('#addChatForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
let roomInput = document.querySelector('#room');
var addChatButton = document.querySelector('#addChatButton')

var stompClient = null;

function login(event) {
    event.preventDefault()
    state.username = document.querySelector('#name').value.trim();
    console.info("Hi "+ state.username)

    createUser(state.username)
        .then(response => response.data.chatNames.forEach(drawRoomButton))

    setVisible(addChatPage)
    addChatButton.classList.remove('hidden');
}

function addChatRoom(event) {
    state.room = roomInput.value.trim();
    console.info("HERE " + state.room + " " + state.username)

    if(state.username != null && state.room != null) {
        createChatRoom(state.room, state.username)
            .then(() => connect(state.room))

        function connect(room) {
            setVisible(chatPage)

            var socket = new SockJS('/ws');
            stompClient = Stomp.over(socket);

            state.roomMessages.set(room, []);
            stompClient.connect({}, onConnected, onError);
        }
    }
    event.preventDefault();
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
    drawRoomButton(state.room);
    fetchRoomMessages(state.room)
        .then(response => {
            console.log("HTTP GET response:", response.data);
            state.roomMessages.set(state.room, response.data);
            redrawChat()
        })
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
addChatForm.addEventListener('submit', addChatRoom, true)
messageForm.addEventListener('submit', sendMessage, true)
document.getElementById("addChatButton").addEventListener('click', addChat)