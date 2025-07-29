package com.adilstudio.project.onevault.core.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImageUtil {
    
    /**
     * Save an image from URI to app's internal storage
     * @param context Application context
     * @param imageUri URI of the image to save
     * @return Path of the saved image file, or null if failed
     */
    fun saveImageToInternalStorage(context: Context, imageUri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            inputStream?.let { stream ->
                // Create unique filename
                val fileName = "bill_image_${UUID.randomUUID()}.jpg"
                val imagesDir = File(context.filesDir, "images")
                
                // Create images directory if it doesn't exist
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs()
                }
                
                val imageFile = File(imagesDir, fileName)
                val outputStream = FileOutputStream(imageFile)
                
                // Copy image data
                stream.copyTo(outputStream)
                
                // Close streams
                stream.close()
                outputStream.close()
                
                // Return the file path
                imageFile.absolutePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Delete an image file from internal storage
     * @param imagePath Path of the image to delete
     * @return true if deletion was successful, false otherwise
     */
    fun deleteImageFromInternalStorage(imagePath: String?): Boolean {
        return try {
            if (imagePath.isNullOrEmpty()) return true
            
            val file = File(imagePath)
            if (file.exists()) {
                file.delete()
            } else {
                true // File doesn't exist, consider it deleted
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Check if an image file exists
     * @param imagePath Path of the image to check
     * @return true if file exists, false otherwise
     */
    fun imageExists(imagePath: String?): Boolean {
        return try {
            if (imagePath.isNullOrEmpty()) return false
            File(imagePath).exists()
        } catch (e: Exception) {
            false
        }
    }
}
