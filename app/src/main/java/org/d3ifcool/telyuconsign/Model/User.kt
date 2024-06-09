package org.d3ifcool.telyuconsign.Model

class User {
    var id: String? = null
    var username: String? = null
    var fullname: String? = null
    var email: String? = null
    var image: String? = null

    constructor() {}
    constructor(id: String?, username: String?, fullname: String?, email: String?, image: String?) {
        this.id = id
        this.username = username
        this.fullname = fullname
        this.email = email
        this.image = image

    }
}