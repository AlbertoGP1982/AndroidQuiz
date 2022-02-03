package com.example.quiz

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.quiz.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var respuesta : String = "http://c80f-81-0-49-64.ngrok.io/responder/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val listaBotones=listOf(binding.bResp1,binding.bResp2,binding.bResp3,binding.bResp4)
        binding.bResponder.isClickable = false
        hacerLlamadaPregunta()
        binding.bResp1.setOnClickListener {
            configurar("/1")
            cambiarColores(binding.bResp1,listaBotones)
        }
        binding.bResp2.setOnClickListener {
            configurar("/2")
            cambiarColores(binding.bResp2,listaBotones)
        }
        binding.bResp3.setOnClickListener {
            configurar("/3")
            cambiarColores(binding.bResp3,listaBotones)
        }
        binding.bResp4.setOnClickListener {
            configurar("/4")
            cambiarColores(binding.bResp4,listaBotones)
        }
    }

    private fun cambiarColores(boton: Button, listaBotones: List<Button>) {
        listaBotones.forEach{
            it.setBackgroundColor(Color.BLACK)
        }
        boton.setBackgroundColor(Color.CYAN)
    }

    private fun configurar(numPregunta : String) {
        binding.bResponder.isClickable = true
        respuesta += numPregunta
        hacerLlamadaRespuesta(respuesta)
        CoroutineScope(Dispatchers.Main).launch{
            delay(2000)
            hacerLlamadaPregunta()
        }

    }

    fun hacerLlamadaPregunta(){
        mostrarCargando()
        val client = OkHttpClient()
        val request = Request.Builder()
        request.url("http://c80f-81-0-49-64.ngrok.io/solicitarPregunta")
        val call = client.newCall(request.build())

        call.enqueue( object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Log.d("Alberto","Error en el onFailure")
                println(e.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@MainActivity, "Algo ha ido mal", Toast.LENGTH_SHORT).show()
                }

            }
            override fun onResponse(call: Call, response: Response) {
                println(response.toString())
                response.body?.let { responseBody ->
                    val body = responseBody.string()
                    println(body)
                    val gson = Gson()
                    val quiz = gson.fromJson(body, Quiz::class.java)
                    mostrarPreguntasYrespuestas(quiz)
                    respuesta = "http://c80f-81-0-49-64.ngrok.io/responder/" + quiz.identificador

                    println(respuesta)
                }
            }
        })
    }

    fun resetColores(listaBotones: List<Button>){
        listaBotones.forEach{
            it.setBackgroundColor(Color.BLACK)
        }
    }

    fun hacerLlamadaRespuesta(respuesta : String){
        val client = OkHttpClient()
        val request = Request.Builder()
        request.url(respuesta)
        val call = client.newCall(request.build())

        call.enqueue( object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("Alberto","Error en el onFailure")
                println(e.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(this@MainActivity, "Algo ha ido mal", Toast.LENGTH_SHORT).show()
                }

            }
            override fun onResponse(call: Call, response: Response) {
                println(response.toString())
                response.body?.let { responseBody ->

                    val body = responseBody.string()
                    println(body)
                    CoroutineScope(Dispatchers.Main).launch {
                        if(body == "Respuesta correcta") {
                            val snackbar =Snackbar.make(binding.root, "RESPUESTA CORRECTA", Snackbar.LENGTH_LONG)
                            snackbar.view.setBackgroundColor(Color.GREEN)
                            snackbar.show()
                        }
                        else if(body == "Respuesta incorrecta") {
                            val snackbar =Snackbar.make(binding.root, "RESPUESTA INCORRECTA", Snackbar.LENGTH_LONG)
                            snackbar.view.setBackgroundColor(Color.RED)
                            snackbar.show()
                        }
                    }
                }
            }
        })
    }

    fun mostrarPreguntasYrespuestas(quiz : Quiz){
        CoroutineScope(Dispatchers.Main).launch {

            binding.tvPregunta.text=quiz.pregunta
            binding.bResp1.text=quiz.respuesta1
            binding.bResp2.text=quiz.respuesta2
            binding.bResp3.text=quiz.respuesta3
            binding.bResp4.text=quiz.respuesta4
        }
    }

    fun mostrarCargando(){
        CoroutineScope(Dispatchers.Main).launch {
            binding.linear.visibility = View.INVISIBLE
            binding.pbDownloading.visibility = View.VISIBLE
            delay(2000)
            binding.pbDownloading.visibility = View.GONE
            binding.linear.visibility = View.VISIBLE
        }
    }
}