package com.example.st5

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException

open class MultipartRequest(
    method: Int,
    url: String,
    private val params: MutableMap<String, DataPart>,
    private val listener: Response.Listener<NetworkResponse>,
    errorListener: Response.ErrorListener
) : Request<NetworkResponse>(method, url, errorListener) {

    private val boundary: String = "volleyMultipartBoundary"
    private val twoHyphens: String = "--"
    private val lineEnd: String = "\r\n"

    override fun getHeaders(): MutableMap<String, String> {
        return HashMap()
    }

    override fun getBodyContentType(): String {
        return "multipart/form-data;boundary=$boundary"
    }

    override fun getBody(): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val dataOutputStream = DataOutputStream(byteArrayOutputStream)

        try {
            // Añadir los parámetros multipart a la solicitud
            for ((key, value) in params) {
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"$key\"; filename=\"${value.fileName}\"$lineEnd")
                dataOutputStream.writeBytes("Content-Type: ${value.contentType}$lineEnd")
                dataOutputStream.writeBytes(lineEnd)
                dataOutputStream.write(value.data)
                dataOutputStream.writeBytes(lineEnd)
            }

            // Finalizar la solicitud multipart
            dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)

            return byteArrayOutputStream.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                byteArrayOutputStream.close()
                dataOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return ByteArray(0)
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        return try {
            Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            Response.error(VolleyError(e))
        }
    }

    override fun deliverResponse(response: NetworkResponse) {
        listener.onResponse(response)
    }
}