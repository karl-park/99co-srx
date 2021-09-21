package sg.searchhouse.agentconnect.dsl

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun File.toMultiBodyPart(): MultipartBody.Part = MultipartBody.Part.createFormData(
    "File", // TODO Custom name?
    name,
    asRequestBody("multipart/form-data".toMediaTypeOrNull())
)