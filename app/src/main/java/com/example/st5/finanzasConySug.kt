package com.example.st5

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.st5.database.Stlite
import com.example.st5.databinding.FragmentFinanzasconsejosysugBinding
import com.example.st5.models.ConySug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class finanzasConySug : Fragment(){

    private lateinit var binding: FragmentFinanzasconsejosysugBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val back = finanzasmain();

        binding.goback.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromright, R.anim.toleft)
                .replace(R.id.finanzas_container, back).addToBackStack(null).commit()
        }

        binding.ConfigButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fromleft, R.anim.toright)
                .replace(R.id.finanzas_container, Configuracion()).addToBackStack(null).commit()
        }
    }

    private suspend fun allOut() {
        withContext(Dispatchers.IO) {
            val assetsDao = Stlite.getInstance(requireContext()).getAssetsDao()
            val conySugDao = Stlite.getInstance(requireContext()).getConySugDao()
            val eventosDao = Stlite.getInstance(requireContext()).getEventosDao()
            val gruposDao = Stlite.getInstance(requireContext()).getGruposDao()
            val ingresosGastosDao = Stlite.getInstance(requireContext()).getIngresosGastosDao()
            val labelsDao = Stlite.getInstance(requireContext()).getLabelsDao()
            val montoDao = Stlite.getInstance(requireContext()).getMontoDao()
            val montoGrupoDao = Stlite.getInstance(requireContext()).getMontoGrupoDao()
            val usuarioDao = Stlite.getInstance(requireContext()).getUsuarioDao()

            //Consulta de todos los datos de cada tabla

            /* CARACTERÍSTICAS

            Influencia en el conysug.estado y conysug.type
            U = único
            R = repetible
            M = múltiple

            Según la gravedad del consejo, será su conysug.style (Puedes, Tienes, Debes)

            Los siguientes son las características de cada conysug.flag

            1. Assets
                1.1. Notificaciones [U] {START}
                1.2. Tema [U] {START}

            2. ConySug
                2.1. Se están rechazando demasiados consejos [U] {MORE}

            3. Eventos
                3.1. ¿Hay pocos eventos? [U] {MORE}
                3.2. ¿Hay demasiados eventos? [U] {LESS}
                3.3. Se están ignorando los eventos programados [U] {START}
                3.4. Hay una cantidad moderada de eventos pero las notificaciones están apagadas [R] {START} (1.1)
                3.5. Dos eventos cuyos nombres se parecen en más de un 90% [M] {MODIFY/DESTROY}
                3.6. No se han agregado eventos en mucho tiempo [R] {CREATE}
                3.7. Nunca se ha creado un evento [U] {START}

            4. Grupos
                4.1. ¿Hay pocos grupos? [U] {MORE}
                4.2. ¿Hay demasiados grupos? [U] {LESS}
                4.3. No hay actividad del usuario en cierto grupo [M] {MORE/DESTROY}
                4.4. No hay actividad de nadie en cierto grupo [M] {MORE/DESTROY}
                4.5. Dos grupos cuyos nombres se parecen en más de un 90% [M] {MODIFY/DESTROY}
                4.6. No se han creado grupos en mucho tiempo [R] {CREATE}
                4.7. Nunca se han creado grupos [U] {START}

            5. Ingresos y Gastos
                5.1. Los ingresos son menos del 5% superiores al egreso (mensual) [R] {LESS}
                5.2. Los ingresos son inferiores a los egresos (mensual) [R] {LESS}
                5.3. Los ingresos han bajado con respecto al mes pasado [R] {LESS}
                5.4. Los egresos han subido con respecto al mes pasado [R] {LESS}

            6. Etiquetas
                6.1. No hay etiquetas [R] {CREATE}
                6.2. Los colores de dos etiquetas se parecen en más de un 80% [M] {MODIFY/DESTROY}
                6.3. Los nombres de dos etiquetas se parecen en más de un 90% [M] {MODIFY/DESTROY}
                6.4. Hay pocas etiquetas [R] {CREATE}
                6.5. Hay demasiadas etiquetas [R] {DESTROY}
                6.6. No se está usando una etiqueta [M] {DESTROY}

            7. Montos
                7.1. No hay montos [R] {START}
                7.2. No hay ingresos [R] {CREATE}
                7.3. No hay egresos [R] {CREATE}
                7.4. Los nombres de dos montos se parecen en más de un 90% [M] {MODIFY/DESTROY}
                7.5. Este monto se ha pospuesto u omitido demasiado [M] {DESTROY}
                7.6. Las frecuencias de los montos no coinciden con el perfil [R] {MORE} (9.1)
                7.7. Más del 80% de los montos tienen la misma frecuencia [U] {MORE/LESS}
                7.8. Hay pocos montos [U] {MORE}
                7.9. Hay demasiados montos [R] {LESS}

            8. Deudas
                8.1. Más del 30% de los montos son deudas [R] {LESS}
                8.2. Las deudas se están posponiendo [R] {LESS/STOP}
                8.3. El costo de la deuda es superior al 50% de los ingresos mensuales [R] {LESS/STOP}
                8.4. El interés de la deuda hará que su costo sea superior al 50% de los ingresos mensuales [R] {LESS/STOP}

            9. Usuario
                9.1. ¿Los roles del usuario coinciden con las frecuencias de sus montos? [U] {MODIFY} (7.6)
                9.2. ¿Los roles del usuario coinciden con su edad? [U] {MODIFY}
                9.3. La meta de ahorro es mayor al 100% del diferencial entre ingresos y egresos [R] {LESS} (5.1)
                9.4. La meta de ahorro es inferior al 10% del diferencial entre ingresos y egresos [R] {MORE} (5.1)
                9.5. No hay meta de ahorro o es 0 [U] {START}

             */
        }
    }

    private inner class ConsejoAdapter(private val consejos: List<ConySug>) :
        RecyclerView.Adapter<ConsejoAdapter.ConsejoViewHolder>() {
        inner class ConsejoViewHolder(
            itemView: View,
            val consejo: TextView,
            val accept: Button,
            val ignore: Button
        ) : RecyclerView.ViewHolder(itemView)


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsejoViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_consejo, parent, false)
            val consejo = itemView.findViewById<TextView>(R.id.consejo)
            val accept = itemView.findViewById<Button>(R.id.aceptar)
            val ignore = itemView.findViewById<Button>(R.id.ignorar)
            return ConsejoViewHolder(
                itemView,
                consejo,
                accept,
                ignore
            )
        }


        override fun onBindViewHolder(holder: ConsejoViewHolder, position: Int) {
            holder.consejo.text = ""
            holder.accept.text = ""
            holder.ignore.text = ""
        }


        override fun getItemCount(): Int {
            Log.v("size de Consejossss", consejos.size.toString())
            return consejos.size
            // tienes que hacer un generador de listas con un objeto que sea una lista, y tendrás que generar sus objetos a partir de datos internos con condicionales (si el usuario tiene ventas, ponerle al usuario vendedor en su perfil, solo en caso de que no lo tenga)
        }
    }
}