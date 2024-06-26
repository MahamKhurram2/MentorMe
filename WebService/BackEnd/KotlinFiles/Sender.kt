package com.mahamkhurram.i210681

class Sender {
    private var data: Data? = null
    private var to: String? = null

    constructor() {}
    constructor(data: Data?, to: String?) {
        this.data = data
        this.to = to
    }

    fun getData(): Data? {
        return data
    }

    fun setData(data: Data?) {
        this.data = data
    }

    fun getTo(): String? {
        return to
    }

    fun setTo(to: String?) {
        this.to = to
    }
}