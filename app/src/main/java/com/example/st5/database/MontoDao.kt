package com.example.st5.database

import androidx.room.*
import com.example.st5.models.Monto

@Dao
interface MontoDao {

    // region BASIC QUERIES
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

    // endregion

    // region GET ETIQUETAS
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

    @Query("SELECT * FROM monto WHERE estado = 5")
    fun getDeudas(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta = 101")
    fun getSalarios(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta = 102")
    fun getIrregulares(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta = 103")
    fun getBecas(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta = 104")
    fun getPensiones(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta = 105")
    fun getManutencion(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta = 106")
    fun getPasivos(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta = 107")
    fun getRegalos(): List<Monto>

    @Query("SELECT * FROM monto WHERE estado = 108")
    fun getPrestamos(): List<Monto>

    // endregion

    // region
    @Query("SELECT * FROM monto WHERE etiqueta = 101 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai)")
    fun getSalariosR(fecha: Int, dom: Int, dow: Int, dai: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta = 102 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai)")
    fun getIrregularesR(fecha: Int, dom: Int, dow: Int, dai: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta = 103 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai)")
    fun getBecasR(fecha: Int, dom: Int, dow: Int, dai: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta = 104 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai)")
    fun getPensionesR(fecha: Int, dom: Int, dow: Int, dai: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta = 105 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai)")
    fun getManutencionR(fecha: Int, dom: Int, dow: Int, dai: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta = 106 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai)")
    fun getPasivosR(fecha: Int, dom: Int, dow: Int, dai: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta = 107 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai)")
    fun getRegalosR(fecha: Int, dom: Int, dow: Int, dai: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE estado = 5 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai)")
    fun getPrestamosR(fecha: Int, dom: Int, dow: Int, dai: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta = :etiqueta AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai)")
    fun getGR(fecha: Int, dom: Int, dow: Int, dai: Int, etiqueta: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE adddate <= :din AND etiqueta <= 100 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai)")
    fun getStatG(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>
    @Query("SELECT * FROM monto WHERE adddate <= :din AND etiqueta > 100 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai)")
    fun getStatI(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>
    // endregion

    // region GET GASTOS/INGRESOS
    @Query("SELECT * FROM monto WHERE etiqueta > 100")
    fun getIngresos(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100")
    fun getGastos(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND etiqueta = :e")
    fun getIngresos(e: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND etiqueta = :e")
    fun getGastos(e: Int): List<Monto>

    //endregion

    // region GET POR FILTROS para listas de monto
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

    @Query("SELECT * FROM monto ORDER BY veces ASC")
    fun getMontosVeces(): List<Monto>

    // endregion

    // region GET POR FILTROS para listas de ingresos
    @Query("SELECT * FROM monto WHERE etiqueta > 100 ORDER BY concepto ASC")
    fun getIngresosAlfabetica(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 ORDER BY fecha ASC")
    fun getIngresosFechados(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 ORDER BY valor ASC")
    fun getIngresosValuados(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 ORDER BY frecuencia ASC")
    fun getIngresosFrecuentes(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 ORDER BY etiqueta ASC")
    fun getIngresosEtiquetados(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 ORDER BY interes ASC")
    fun getIngresosInteres(): List<Monto>

    // endregion

    // region GET POR FILTROS para listas de gastos
    @Query("SELECT * FROM monto WHERE etiqueta < 100 ORDER BY concepto ASC")
    fun getGastosAlfabetica(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 ORDER BY fecha ASC")
    fun getGastosFechados(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 ORDER BY valor ASC")
    fun getGastosValuados(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 ORDER BY frecuencia ASC")
    fun getGastosFrecuentes(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 ORDER BY etiqueta ASC")
    fun getGastosEtiquetados(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 ORDER BY interes ASC")
    fun getGastosInteres(): List<Monto>

    // endregion

    // region GET POR FILTROS para listas de ingresos (con etiqueta)
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

    // endregion

    // region GET POR FILTROS para listas de gastos (con etiqueta)
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

    // endregion

    // region GET ATRIBUTOS
    @Query("SELECT idmonto FROM monto WHERE idmonto = :id")
    fun getIdmonto(id: Int): Long

    @Query("SELECT iduser FROM monto WHERE idmonto = :id")
    fun getIduser(id: Int): Long

    @Query("SELECT concepto FROM monto WHERE idmonto = :id")
    fun getConcepto(id: Int): String

    @Query("SELECT valor FROM monto WHERE idmonto = :id")
    fun getValor(id: Int): Double

    @Query("SELECT valorfinal FROM monto WHERE idmonto = :id")
    fun getValorFinal(id: Int): Double

    @Query("SELECT fecha FROM monto WHERE idmonto = :id")
    fun getFecha(id: Int): Int

    @Query("SELECT fechafinal FROM monto WHERE idmonto = :id")
    fun getFechaFinal(id: Int): Int

    @Query("SELECT frecuencia FROM monto WHERE idmonto = :id")
    fun getFrecuencia(id: Int): Int

    @Query("SELECT etiqueta FROM monto WHERE idmonto = :id")
    fun getEtiqueta(id: Int): Int

    @Query("SELECT interes FROM monto WHERE idmonto = :id")
    fun getInteres(id: Int): Double

    @Query("SELECT veces FROM monto WHERE idmonto = :id")
    fun getVeces(id: Int): Long

    @Query("SELECT estado FROM monto WHERE idmonto = :id")
    fun getEstado(id: Int): Int

    @Query("SELECT adddate FROM monto WHERE idmonto = :id")
    fun getAdded(id: Int): Int

    // endregion

    // region GET POR FECHA ESPECÍFICA
    @Query("SELECT * FROM monto WHERE adddate <= :din AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai)")
    fun getMontoXFecha(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE adddate <= :din AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) ORDER BY concepto ASC")
    fun getMontoXFechaAlfabetica(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE adddate <= :din AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) ORDER BY valor ASC")
    fun getMontoXFechaValuados(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE adddate <= :din AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) ORDER BY frecuencia ASC")
    fun getMontoXFechaFrecuentes(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE adddate <= :din AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) ORDER BY etiqueta ASC")
    fun getMontoXFechaEtiquetados(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE adddate <= :din AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) ORDER BY interes ASC")
    fun getMontoXFechaInteres(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE adddate <= :din AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) ORDER BY veces ASC")
    fun getMontoXFechaVeces(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto")
    fun getMontoXFecha(): List<Monto>

    @Query("SELECT * FROM monto ORDER BY concepto ASC")
    fun getMontoXFechaAlfabetica(): List<Monto>

    @Query("SELECT * FROM monto ORDER BY valor ASC")
    fun getMontoXFechaValuados(): List<Monto>

    @Query("SELECT * FROM monto ORDER BY frecuencia ASC")
    fun getMontoXFechaFrecuentes(): List<Monto>

    @Query("SELECT * FROM monto ORDER BY etiqueta ASC")
    fun getMontoXFechaEtiquetados(): List<Monto>

    @Query("SELECT * FROM monto ORDER BY interes ASC")
    fun getMontoXFechaInteres(): List<Monto>

    @Query("SELECT * FROM monto ORDER BY veces ASC")
    fun getMontoXFechaVeces(): List<Monto>

    // endregion

    // region GET INGRESOS POR FECHA ESPECÍFICA
    @Query("SELECT * FROM monto WHERE etiqueta > 100 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) AND adddate <= :din")
    fun getIXFecha(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) AND adddate <= :din ORDER BY concepto ASC")
    fun getIXFechaAlfabetica(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) AND adddate <= :din ORDER BY valor ASC")
    fun getIXFechaValuados(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) AND adddate <= :din ORDER BY frecuencia ASC")
    fun getIXFechaFrecuentes(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) AND adddate <= :din ORDER BY etiqueta ASC")
    fun getIXFechaEtiquetados(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) AND adddate <= :din ORDER BY interes ASC")
    fun getIXFechaInteres(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) AND adddate <= :din ORDER BY veces ASC")
    fun getIXFechaVeces(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100")
    fun getIXFecha(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 ORDER BY concepto ASC")
    fun getIXFechaAlfabetica(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 ORDER BY valor ASC")
    fun getIXFechaValuados(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 ORDER BY frecuencia ASC")
    fun getIXFechaFrecuentes(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 ORDER BY etiqueta ASC")
    fun getIXFechaEtiquetados(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 ORDER BY interes ASC")
    fun getIXFechaInteres(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 ORDER BY veces ASC")
    fun getIXFechaVeces(): List<Monto>

    // endregion


    // region GET INGRESOS POR FECHA ESPECÍFICA
    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 0 OR estado = 5) AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) AND adddate <= :din")
    fun getGXFecha(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 0 OR estado = 5) AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) AND adddate <= :din ORDER BY concepto ASC")
    fun getGXFechaAlfabetica(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 0 OR estado = 5) AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) AND adddate <= :din ORDER BY valor ASC")
    fun getGXFechaValuados(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 0 OR estado = 5) AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) AND adddate <= :din ORDER BY frecuencia ASC")
    fun getGXFechaFrecuentes(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 0 OR estado = 5) AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) AND adddate <= :din ORDER BY etiqueta ASC")
    fun getGXFechaEtiquetados(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 0 OR estado = 5) AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) AND adddate <= :din ORDER BY interes ASC")
    fun getGXFechaInteres(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 0 OR estado = 5) AND (fecha = :fecha OR fecha = :dom OR fecha = :dow OR fecha = :dai) AND adddate <= :din ORDER BY veces ASC")
    fun getGXFechaVeces(fecha: Int, dom: Int, dow: Int, dai: Int, din: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 0 OR estado = 5)")
    fun getGXFecha(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 0 OR estado = 5) ORDER BY concepto ASC")
    fun getGXFechaAlfabetica(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 0 OR estado = 5) ORDER BY valor ASC")
    fun getGXFechaValuados(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 0 OR estado = 5) ORDER BY frecuencia ASC")
    fun getGXFechaFrecuentes(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 0 OR estado = 5) ORDER BY etiqueta ASC")
    fun getGXFechaEtiquetados(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 0 OR estado = 5) ORDER BY interes ASC")
    fun getGXFechaInteres(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 0 OR estado = 5) ORDER BY veces ASC")
    fun getGXFechaVeces(): List<Monto>

    // endregion

    // region GETFAST GASTOS/INGRESOS
    @Query("SELECT * FROM monto WHERE etiqueta > 100 AND (etiqueta = 10 OR etiqueta = 13)")
    fun getIFast(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (etiqueta = 10 OR etiqueta = 13)")
    fun getGFast(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta > 100 AND etiqueta = :e AND (etiqueta = 10 OR etiqueta = 13)")
    fun getIFast(e: Int): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND etiqueta = :e AND (etiqueta = 10 OR etiqueta = 13)")
    fun getGFast(e: Int): List<Monto>

    //endregion

    @Query("SELECT * FROM monto WHERE (estado = 2 OR estado = 7)")
    fun getPapelera(): List<Monto>

    @Query("SELECT * FROM monto WHERE (estado = 3 OR estado = 4 OR estado = 8 OR estado = 9)")
    fun getFavoritos(): List<Monto>

    @Query("SELECT * FROM monto WHERE etiqueta < 100 AND (estado = 5 OR estado = 8)")
    fun getPDADeudas(): List<Monto>


    // region
    @Query("SELECT concepto FROM monto WHERE etiqueta < 100 ORDER BY valor DESC")
    fun getTopConceptos(): Array<String>
    @Query("SELECT valor FROM monto WHERE etiqueta < 100 ORDER BY valor DESC")
    fun getTopValor(): Array<Double>
    //endregion
}