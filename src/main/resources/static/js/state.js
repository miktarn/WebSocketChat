'use strict';

export const state = {
    username: null,
    room: null,
    roomMessages: new Map(),
    visibleContainer: document.querySelector('#username-page'),
    invitedUsers: [],
};

export function setVisible(domContainer) {
    state.visibleContainer.classList.add('hidden');
    domContainer.classList.remove('hidden');
    state.visibleContainer = domContainer;
}