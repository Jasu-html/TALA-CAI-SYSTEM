package com.capstone.tala.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val avatarSpecJson: String = "",
    val section: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
