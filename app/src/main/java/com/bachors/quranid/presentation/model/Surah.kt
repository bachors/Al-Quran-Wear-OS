package com.bachors.quranid.presentation.model

data class Surah(
    val nomor: String,
    val nama: String,
    val asma: String,
    val type: String,
    val urut: String,
    val rukuk: String,
    val arti: String,
    val keterangan: String,
    val audio: String,
    val ayat: List<Ayah>
)