package com.android.chatbase.model

class Chat (
    var sender: String,
    var receiver: String,
    var msg: String,
){
    constructor() : this("","","")
}