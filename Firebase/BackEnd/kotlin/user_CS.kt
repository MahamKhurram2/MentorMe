package com.mahamkhurram.i210681

data class user_CS(
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var city: String = "",
    var country: String = "",
    var contact: String = "",
    var profilePicture: String = "" ,
    var  fcmtoken: String = ""// Add profile picture property
) {
    constructor() : this("", "", "", "", "", "", "")
}
