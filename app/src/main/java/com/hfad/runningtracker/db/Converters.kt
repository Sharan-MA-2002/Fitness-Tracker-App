package com.hfad.runningtracker.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.io.ByteArrayOutputStream

class Converters {

    @TypeConverter
    fun toBitMap(byteArray: ByteArray):Bitmap{
        return BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
    }

    @TypeConverter
    fun fromBitMap(bmp:Bitmap):ByteArray{
        val outputStream=ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG,100,outputStream)
        return  outputStream.toByteArray()
    }
}