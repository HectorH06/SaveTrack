package com.example.st5.database

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import org.json.JSONObject


class Backup {
    private fun respaldarDatos() {
        val url = "http://savetrack.com.mx/backupput.php"
        val backup = JSONObject()
        backup.put("username", "LUIS")
        backup.put("edad", 30)
        val jsonBody = JSONObject()
        try {
            jsonBody.put("backup", backup)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val requestBody = jsonBody.toString()

        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, url, null,
            Response.Listener {
                // El respaldo fue exitoso
            },
            Response.ErrorListener {
                // Hubo un error al enviar los datos al servidor externo
            }) {
            override fun getBody(): ByteArray {
                return requestBody.toByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }
    }
}