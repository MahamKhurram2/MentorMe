package com.mahamkhurram.i210681

data class chatt(val sender: String, val receiver: String, val message: String ,
                 var audioUrl: String? = null,
                 var imageUrl: String? = null,
                 var key: String = "") {
    // Secondary constructor if needed
    constructor() : this("", "", "")
}

