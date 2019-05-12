package dev.cedrickring.auth.model.response

data class UserCreateResponse(val username: String,
                              val email: String,
                              val created: Boolean)
