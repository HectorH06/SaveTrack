package com.example.st4

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import java.sql.PreparedStatement
import java.sql.SQLException
import com.example.st4.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var connectSql: ConexionSQL
    lateinit var binding: ActivityLoginBinding
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val buttonSinCuenta = findViewById<Button>(R.id.button_sinCuenta)
        buttonSinCuenta.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        connectSql = ConexionSQL()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonIniSes.setOnClickListener {
            agregarUsuario()
            binding.editTextTextPersonName.text.clear()
            binding.editTextTextPassword.text.clear()
        }


    }
    fun agregarUsuario(){
        try {
            val usuario: PreparedStatement =  connectSql.dbConn()?.prepareStatement("INSERT INTO Login_ST values (?,?)")!!
            usuario.setString(1, binding.editTextTextPersonName.text.toString())
            usuario.setString(2, binding.editTextTextPassword.text.toString())
            usuario.executeUpdate()
            Toast.makeText(this, "INSERTADO CORRECTAMENTE", Toast.LENGTH_SHORT).show()
        } catch (ex: SQLException){
            Toast.makeText(this, "ERROR AL INSERTAR", Toast.LENGTH_SHORT).show()
        }
    }
}