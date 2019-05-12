package dev.cedrickring.auth.model

data class LoginUser(val username: String,
                     val password: String,
                     val stayLoggedIn: Boolean? = false)
