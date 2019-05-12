package dev.cedrickring.auth.model.request

data class ChangePasswordRequest(val oldPassword: String, val newPassword: String)
