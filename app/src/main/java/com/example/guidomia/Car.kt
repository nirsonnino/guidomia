package com.example.guidomia

data class Car(
    val make: String,
    val model: String,
    val customerPrice: Double,
    val marketPrice: Double,
    val rating: Int,
    val prosList: List<String>,
    val consList: List<String>,
    var isExpanded: Boolean = false
)