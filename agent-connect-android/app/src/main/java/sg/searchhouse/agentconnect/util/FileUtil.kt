package sg.searchhouse.agentconnect.util

import android.content.Context
import androidx.annotation.WorkerThread
import java.io.File
import java.io.FileWriter
import java.io.IOException

object FileUtil {
    @Throws(IOException::class)
    @WorkerThread
    fun writeTextToFile(
        context: Context,
        text: String,
        fileName: String,
        directoryName: String? = null
    ): String {
        val directory = context.getExternalFilesDir(directoryName)
        val file = File(directory, fileName)
        val writer = FileWriter(file)
        writer.write(text)
        writer.close()
        return file.path
    }

    fun isFileExist(context: Context, fileName: String, directory: String? = null): Boolean {
        val file = File(context.getExternalFilesDir(directory), fileName)
        return file.exists()
    }
}