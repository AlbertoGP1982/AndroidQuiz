package com.example.quiz

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Quiz (
    val identificador: Int,
    val pregunta: String,
    val respuesta1:String,
    val respuesta2:String,
    val respuesta3:String,
    val respuesta4:String,
    val respuestaCorrecta: String
) : Parcelable

