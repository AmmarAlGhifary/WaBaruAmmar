package com.blogspot.yourfavoritekaisar.wabaruammar.listener

interface ChatClickListener {
    fun onChatClicked(name: String?, otherUserId: String?, chatsImageUrl: String?,
                      chatsName: String?)
}