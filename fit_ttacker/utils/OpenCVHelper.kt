package com.example.fit_ttacker.utils

import android.content.Context
import android.graphics.Bitmap
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

object OpenCVHelper {

    fun initialize(context: Context): Boolean {
        return try {
            OpenCVLoader.initDebug()
        } catch (e: Exception) {
            false
        }
    }

    // Filtro de Escala de Grises
    fun applyGrayscaleFilter(context: Context, bitmap: Bitmap): Bitmap {
        return try {
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_GRAY2RGBA)
            val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(mat, result)
            mat.release()
            result
        } catch (e: Exception) {
            bitmap
        }
    }

    // Filtro Sepia
    fun applySepiaFilter(context: Context, bitmap: Bitmap): Bitmap {
        return try {
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)

            val kernel = Mat(4, 4, CvType.CV_32F)
            kernel.put(0, 0,
                0.272, 0.534, 0.131, 0.0,
                0.349, 0.686, 0.168, 0.0,
                0.393, 0.769, 0.189, 0.0,
                0.0, 0.0, 0.0, 1.0
            )

            Core.transform(mat, mat, kernel)
            val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(mat, result)
            mat.release()
            kernel.release()
            result
        } catch (e: Exception) {
            bitmap
        }
    }

    // Filtro de Desenfoque
    fun applyBlurFilter(context: Context, bitmap: Bitmap): Bitmap {
        return try {
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)
            Imgproc.GaussianBlur(mat, mat, Size(15.0, 15.0), 0.0)
            val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(mat, result)
            mat.release()
            result
        } catch (e: Exception) {
            bitmap
        }
    }

    // Filtro de Nitidez
    fun applySharpenFilter(context: Context, bitmap: Bitmap): Bitmap {
        return try {
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)

            val kernel = Mat(3, 3, CvType.CV_32F)
            kernel.put(0, 0,
                0.0, -1.0, 0.0,
                -1.0, 5.0, -1.0,
                0.0, -1.0, 0.0
            )

            Imgproc.filter2D(mat, mat, -1, kernel)
            val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(mat, result)
            mat.release()
            kernel.release()
            result
        } catch (e: Exception) {
            bitmap
        }
    }

    // Filtro Invertir
    fun applyInvertFilter(context: Context, bitmap: Bitmap): Bitmap {
        return try {
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)
            Core.bitwise_not(mat, mat)
            val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(mat, result)
            mat.release()
            result
        } catch (e: Exception) {
            bitmap
        }
    }

    // Filtro de Brillo+
    fun applyBrightnessFilter(context: Context, bitmap: Bitmap): Bitmap {
        return try {
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)
            mat.convertTo(mat, -1, 1.0, 50.0)
            val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(mat, result)
            mat.release()
            result
        } catch (e: Exception) {
            bitmap
        }
    }

    // Filtro de Contraste+
    fun applyContrastFilter(context: Context, bitmap: Bitmap): Bitmap {
        return try {
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)
            mat.convertTo(mat, -1, 1.5, 0.0)
            val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(mat, result)
            mat.release()
            result
        } catch (e: Exception) {
            bitmap
        }
    }

    // Filtro Vintage
    fun applyVintageFilter(context: Context, bitmap: Bitmap): Bitmap {
        return try {
            val mat = Mat()
            Utils.bitmapToMat(bitmap, mat)

            val kernel = Mat(4, 4, CvType.CV_32F)
            kernel.put(0, 0,
                0.393, 0.769, 0.189, 0.0,
                0.349, 0.686, 0.168, 0.0,
                0.272, 0.534, 0.131, 0.0,
                0.0, 0.0, 0.0, 1.0
            )

            Core.transform(mat, mat, kernel)
            mat.convertTo(mat, -1, 0.9, 15.0)

            val result = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
            Utils.matToBitmap(mat, result)
            mat.release()
            kernel.release()
            result
        } catch (e: Exception) {
            bitmap
        }
    }
}