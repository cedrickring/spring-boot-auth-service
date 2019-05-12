package dev.cedrickring.auth.model

import java.util.*

data class User(var id: UUID?,
                val fullName: String,
                val username: String,
                val emailAddress: String,
                val password: String,
                val created: Long?,
                val passwordLastModified: Long?)
