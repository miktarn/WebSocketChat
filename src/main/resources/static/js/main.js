'use strict';

var addChatPage = document.querySelector('#add-chat-page');
var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var roomListPage = document.querySelector('#room-list-page');

var usernameForm = document.querySelector('#usernameForm');
var addChatForm = document.querySelector('#addChatForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
let roomInput = document.querySelector('#room');


var stompClient = null;
var username = null;
var room = null;

const roomMessages = new Map();

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function updateChatTitle() {
    document.querySelector('#roomTitle').textContent = room
}

function login(event) {
    event.preventDefault()
    username = document.querySelector('#name').value.trim();
    console.info("Hi "+ username)
    usernamePage.classList.add('hidden');
    addChatPage.classList.remove('hidden');
}

function connect(event) {
    room = roomInput.value.trim();
    console.info("HERE " + room + " " + username)
    if(username != null && room != null) {
        addChatPage.classList.add('hidden');
        chatPage.classList.remove('hidden');
        updateChatTitle();

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        roomMessages.set(room, []);
        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/' + room, onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, room: room, type: 'JOIN'})
    )

    connectingElement.classList.add('hidden');
    createRoomButton(room);
    axios.get("http://localhost:8080/message?room=" + room)
        .then(response => {
            console.log("HTTP GET response:", response.data);
            roomMessages.set(room, response.data);
            redrawChat()
        })
}


function createRoomButton(roomName) {
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



function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            room: room,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    if (message.room === room) {
        draw(message)
        messageArea.scrollTop = messageArea.scrollHeight;
    }

    roomMessages.get(message.room).push(message);
}

function draw(message) {
    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

function addChat(event) {
    event.preventDefault();
    roomInput.value = '';
    messageArea.innerHTML = '';
    addChatPage.classList.remove('hidden');
    chatPage.classList.add('hidden');
}

function switchRoom(newRoom) {
    room = newRoom;
    updateChatTitle();
    redrawChat();
}

function redrawChat() {
    messageArea.innerHTML = '';

    const messages = roomMessages.get(room) || [];

    messages.forEach(message => draw(message));

    messageArea.scrollTop = messageArea.scrollHeight;
}

usernameForm.addEventListener('submit', login, true)
addChatForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)
document.getElementById("addChat").addEventListener('click', addChat)