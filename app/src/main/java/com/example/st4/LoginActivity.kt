package com.example.st4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import java.sql.PreparedStatement
import java.sql.SQLException

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonSinCuenta = findViewById<Button>(R.id.button_sinCuenta)
        buttonSinCuenta.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        lateinit var binding: LoginActivity

        // lateinit var connectSql: ConnectSql

        var connectSql = ConexionSQL()

        binding = MainActivity.inflate(layoutInflater)
        setContentView(binding.root)

        binding.button_IniSes.setOnClickListener { onBackPressed() }  //regresar

        binding.setOnClickListener {
            agregarUsuario()
            binding.editTextTextPersonName.text.clear()
            binding.editTextTextPassword.text.clear()
        }

        fun agregarUsuario(){
            try {
                val usuario: PreparedStatement =  connectSql.dbConn()?.prepareStatement("insert into Usuario values (?,?)")!!
                usuario.setString(1, binding.editTextTextPersonName.text.toString())
                usuario.setString(2, binding.editTextTextPassword.text.toString())
                usuario.executeUpdate()
                Toast.makeText(this, "INSERTADO CORRECTAMENTE", Toast.LENGTH_SHORT).show()
            } catch (ex: SQLException){
                Toast.makeText(this, "ERROR AL INSERTAR", Toast.LENGTH_SHORT).show()
            }
        }
    }
}