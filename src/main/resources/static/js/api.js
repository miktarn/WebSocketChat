'use strict';

export function createUser(username) {
    return axios.post(`http://localhost:8080/user?name=${username}`);
}

export function createChatRoom(chatName, creatorName) {
    return axios.post('http://localhost:8080/chat', { name: chatName, creatorName });
}

export function fetchRoomMessages(room) {
    return axios.get(`http://localhost:8080/message?room=${room}`);
}