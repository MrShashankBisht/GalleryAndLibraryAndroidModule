package com.iab.galleryandlibrary.utils

enum class MediaType {
    IMAGE, VIDEO
}

public class Size(var x: Float, var y: Float){
    fun getStringXY(): String{
        return "$x X $y"
    }
}
