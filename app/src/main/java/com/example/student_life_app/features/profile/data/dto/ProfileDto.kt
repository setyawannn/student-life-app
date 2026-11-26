package com.example.student_life_app.features.profile.data.dto

import com.example.student_life_app.features.profile.model.UserProfile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    @SerialName("id") val id: String,
    @SerialName("full_name") val fullName: String,
    @SerialName("nim") val nim: String = "",
    @SerialName("avatar_url") val avatarUrl: String? = null
) {
    fun toDomain(): UserProfile = UserProfile(
        id = id,
        fullName = fullName,
        nim = nim,
        avatarUrl = avatarUrl
    )
}
