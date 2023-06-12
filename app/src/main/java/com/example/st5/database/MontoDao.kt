package com.example.st5.database

import androidx.room.*
import com.example.st5.models.Monto

@Dao
interface MontoDao {
    @Insert
    fun insertMonto(monto: Monto)
    @Update
    fun updateMonto(monto: Monto)
    @Delete
    fun deleteMonto(monto: Monto)

    @Query("SELECT * FROM monto")
    fun getMonto(): List<Monto>
    @Query("SELECT * FROM monto WHERE idmonto = :id")
    fun getM(id: Int): Monto
    @Query("SELECT MAX(idmonto) FROM monto")
    fun getMaxMonto(): Int
    @Query("DELETE FROM monto")
    suspend fun clean()

    // GET ETIQUETAS
    @Query("SELECT * FROM monto WHERE etiqueta = 1")
    fun getAlimentos(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 2")
    fun getHogar(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 3")
    fun getBienestar(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 4")
    fun getNecesidades(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 5")
    fun getHormiga(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 6")
    fun getOcio(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 7")
    fun getObsequios(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 8")
    fun getDeudas(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 9")
    fun getSalarios(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 10")
    fun getIrregulares(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 11")
    fun getBecas(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 12")
    fun getPensiones(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 13")
    fun getManutencion(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 14")
    fun getPasivos(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 15")
    fun getRegalos(): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = 16")
    fun getPrestamos(): List<Monto>

    // GET GASTOS/INGRESOS
    @Query("SELECT * FROM monto WHERE valor >= 0")
    fun getIngresos(): List<Monto>
    @Query("SELECT * FROM monto WHERE valor < 0")
    fun getGastos(): List<Monto>
    @Query("SELECT * FROM monto WHERE valor >= 0 AND etiqueta = :e")
    fun getIngresos(e: Int): List<Monto>
    @Query("SELECT * FROM monto WHERE valor < 0 AND etiqueta = :e")
    fun getGastos(e: Int): List<Monto>

    //GET POR FECHA ESPECÍFICA
    @Query("SELECT * FROM monto WHERE fecha = :fecha")
    fun getMontoXFecha(fecha: String): List<Monto>

    // GET POR FILTROS para listas de monto
    @Query("SELECT * FROM monto ORDER BY concepto ASC")
    fun getMontosAlfabetica(): List<Monto>
    @Query("SELECT * FROM monto ORDER BY fecha ASC")
    fun getMontosFechados(): List<Monto>
    @Query("SELECT * FROM monto ORDER BY valor ASC")
    fun getMontosValuados(): List<Monto>
    @Query("SELECT * FROM monto ORDER BY frecuencia ASC")
    fun getMontosFrecuentes(): List<Monto>
    @Query("SELECT * FROM monto ORDER BY etiqueta ASC")
    fun getMontosEtiquetados(): List<Monto>
    @Query("SELECT * FROM monto ORDER BY interes ASC")
    fun getMontosInteres(): List<Monto>

    // GET POR FILTROS para listas de ingresos
    @Query("SELECT * FROM monto WHERE valor >= 0 ORDER BY concepto ASC")
    fun getIngresosAlfabetica(): List<Monto>
    @Query("SELECT * FROM monto WHERE valor >= 0 ORDER BY fecha ASC")
    fun getIngresosFechados(): List<Monto>
    @Query("SELECT * FROM monto WHERE valor >= 0 ORDER BY valor ASC")
    fun getIngresosValuados(): List<Monto>
    @Query("SELECT * FROM monto WHERE valor >= 0 ORDER BY frecuencia ASC")
    fun getIngresosFrecuentes(): List<Monto>
    @Query("SELECT * FROM monto WHERE valor >= 0 ORDER BY etiqueta ASC")
    fun getIngresosEtiquetados(): List<Monto>
    @Query("SELECT * FROM monto WHERE valor >= 0 ORDER BY interes ASC")
    fun getIngresosInteres(): List<Monto>

    // GET POR FILTROS para listas de gastos
    @Query("SELECT * FROM monto WHERE valor < 0 ORDER BY concepto ASC")
    fun getGastosAlfabetica(): List<Monto>
    @Query("SELECT * FROM monto WHERE valor < 0 ORDER BY fecha ASC")
    fun getGastosFechados(): List<Monto>
    @Query("SELECT * FROM monto WHERE valor < 0 ORDER BY valor ASC")
    fun getGastosValuados(): List<Monto>
    @Query("SELECT * FROM monto WHERE valor < 0 ORDER BY frecuencia ASC")
    fun getGastosFrecuentes(): List<Monto>
    @Query("SELECT * FROM monto WHERE valor < 0 ORDER BY etiqueta ASC")
    fun getGastosEtiquetados(): List<Monto>
    @Query("SELECT * FROM monto WHERE valor < 0 ORDER BY interes ASC")
    fun getGastosInteres(): List<Monto>

    // GET POR FILTROS para listas de ingresos (con etiqueta)
    @Query("SELECT * FROM monto WHERE etiqueta = :e ORDER BY concepto ASC")
    fun getIngresosAlfabetica(e: Int): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = :e ORDER BY fecha ASC")
    fun getIngresosFechados(e: Int): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = :e ORDER BY valor ASC")
    fun getIngresosValuados(e: Int): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = :e ORDER BY frecuencia ASC")
    fun getIngresosFrecuentes(e: Int): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = :e ORDER BY etiqueta ASC")
    fun getIngresosEtiquetados(e: Int): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = :e ORDER BY interes ASC")
    fun getIngresosInteres(e: Int): List<Monto>

    // GET POR FILTROS para listas de gastos (con etiqueta)
    @Query("SELECT * FROM monto WHERE etiqueta = :e ORDER BY concepto ASC")
    fun getGastosAlfabetica(e: Int): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = :e ORDER BY fecha ASC")
    fun getGastosFechados(e: Int): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = :e ORDER BY valor ASC")
    fun getGastosValuados(e: Int): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = :e ORDER BY frecuencia ASC")
    fun getGastosFrecuentes(e: Int): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = :e ORDER BY etiqueta ASC")
    fun getGastosEtiquetados(e: Int): List<Monto>
    @Query("SELECT * FROM monto WHERE etiqueta = :e ORDER BY interes ASC")
    fun getGastosInteres(e: Int): List<Monto>

    // GET ATRIBUTOS
    @Query("SELECT idmonto FROM monto WHERE idmonto = :id")
    fun getIdmonto(id: Int): Long
    @Query("SELECT iduser FROM monto WHERE idmonto = :id")
    fun getIduser(id: Int): Long
    @Query("SELECT concepto FROM monto WHERE idmonto = :id")
    fun getConcepto(id: Int): String
    @Query("SELECT valor FROM monto WHERE idmonto = :id")
    fun getValor(id: Int): Double
    @Query("SELECT fecha FROM monto WHERE idmonto = :id")
    fun getFecha(id: Int): String
    @Query("SELECT frecuencia FROM monto WHERE idmonto = :id")
    fun getFrecuencia(id: Int): Long
    @Query("SELECT etiqueta FROM monto WHERE idmonto = :id")
    fun getEtiqueta(id: Int): Long
    @Query("SELECT interes FROM monto WHERE idmonto = :id")
    fun getInteres(id: Int): Double
    @Query("SELECT veces FROM monto WHERE idmonto = :id")
    fun getVeces(id: Int): Long
}