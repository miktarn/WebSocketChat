'use strict';

const baseUrl = "http://localhost:8080"

export function createUser(userName) {
    return axios.post(baseUrl + `/user?name=${userName}`);
}

export function userExists(userName) {
    return axios.get(baseUrl + `/user/exists?name=${userName}`);
}

export function createChatRoom(roomName, invitedUsersNames, creatorName) {
    return axios.post(baseUrl + '/chat', { roomName, invitedUsersNames, creatorName });
}

export function connectUserToChat(chatName, userName) {
    return axios.post(baseUrl + '/chat/user', { chatName, userName });
}

export function fetchRoomMessages(room) {
    return axios.get(baseUrl + `/message?room=${room}`);
}
