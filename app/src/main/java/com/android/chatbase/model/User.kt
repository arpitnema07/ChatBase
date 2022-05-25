package com.android.chatbase.model

data class User(
    var id: String,
    var username: String,
     var imageUrl: String,
    var status: String
) {
    constructor() : this("","","","")
    fun equals(user: User):Boolean{
        return id== user.id && username == user.username && imageUrl == user.imageUrl && status == user.status
    }
}