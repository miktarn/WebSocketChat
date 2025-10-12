'use strict';

export function createUser(username) {
    return axios.post(`http://localhost:8080/user?name=${username}`);
}

export function createChatRoom(name, userNames) {
    return axios.post('http://localhost:8080/chat', { name, userNames });
}

export function connectUserToChat(chatName, userName) {
    return axios.post('http://localhost:8080/chat/user', { chatName, userName });
}


export function fetchRoomMessages(room) {
    return axios.get(`http://localhost:8080/message?room=${room}`);
}