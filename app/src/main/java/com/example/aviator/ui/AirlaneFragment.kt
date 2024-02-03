package com.example.aviator.ui

import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aviator.R
import com.example.aviator.data.Airlane
import com.example.aviator.databinding.FragmentAirlaneBinding
import com.example.aviator.models.AirlaneViewModel
import com.example.aviator.repository.AppRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

const val AIRLANE_TAG = "AirlaneFragment"
const  val AIRLANE_TITLE="Авиакомпания"

class AirlaneFragment : Fragment() {
    private lateinit var viewModel: AirlaneViewModel //связь фрагмента с его моделью
    private var _binding: FragmentAirlaneBinding? = null// связь с fragment_airlane
    val binding
        get() = _binding!!

    private var adapter: AirlaneListAdapter = AirlaneListAdapter(emptyList())

    companion object {//статическое объявление
        fun newInstance() = AirlaneFragment() // создание нового экземпляра фрагмента
    }
    //код для отображения сохранения
    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }*/
    //Создание польз. интерфейса + работа с RecyclerView. Создание компонентов внутри фрагмента. загрузка интерфейса
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentAirlaneBinding.inflate(inflater, container,false)//привязка к fragment_airlane
        //отображение по вертикали
        binding.rvAirlane.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)//сохранение состояниий
        viewModel = ViewModelProvider(this).get(AirlaneViewModel:: class.java)//создание экземпляра модели или возврат создан
        viewModel.university.observe(viewLifecycleOwner){//отслеживание изменений элемента
            adapter=AirlaneListAdapter(it)//создание адаптера связанного с RecyclerView
            binding.rvAirlane.adapter=adapter//запись
        }
        callbacks?.setTitle(AIRLANE_TITLE) //безопасный вызов метода
        viewModel.loadAirlane()//показ всех факультетов
    }
    //
    private inner class AirlaneHolder(view: View)//внутр класс расширяет RecyclerView.ViewHolder,  содержит ссылки на все элементы пользовательского интерфейса
        : RecyclerView.ViewHolder(view), View.OnClickListener{
        lateinit var airlane: Airlane

        fun bind(airlane: Airlane){//отображение данных на элементы польз.интерф
            this.airlane=airlane
            itemView.findViewById<Button>(R.id.openBtn).text = airlane.name
            val pattern = "dd.MM.yy"
            val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())

            val formattedDate: String = dateFormat.format(airlane.date)
            itemView.findViewById<TextView>(R.id.tvAirlaneElement).text=" ${formattedDate} ${airlane.surname}"
            itemView.findViewById<ConstraintLayout>(R.id.crudButtons).visibility = View.GONE
            itemView.findViewById<Button>(R.id.openBtn).setOnClickListener {
                callbacks?.showAirlane(airlane.id!!)
            }
        }

        init{//слушатель кликов на View
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?){//нажатие на элемент списка

            val cl = itemView.findViewById<ConstraintLayout>(R.id.crudButtons)
            cl.visibility = View.VISIBLE
            lastItemView?.findViewById<ConstraintLayout>(R.id.crudButtons)?.visibility = View.GONE
            lastItemView = if (lastItemView == itemView) null else itemView
            if (cl.visibility == View.VISIBLE) {

                itemView.findViewById<ImageButton>(R.id.delBtn).setOnClickListener {
                    commitDelete(airlane)
                }
                itemView.findViewById<ImageButton>(R.id.editBtn).setOnLongClickListener {
                    editCreate(airlane)
                    true
                }
            }

        }
        private fun commitDelete(airline: Airlane) {
            val builder = AlertDialog.Builder(requireContext())
            builder.setCancelable(true)
            builder.setMessage("Удалить авиакомпанию ${airline.name} ?")
            builder.setTitle("Подтверждение")
            builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
                CoroutineScope(Dispatchers.Main). launch {
                    AppRepository.get().deleteAirlane(airlane)
                }
            }
            builder.setNegativeButton(R.string.cansel, null)
            builder.show()
        }
    }
    private fun editCreate(airlane: Airlane?){
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.name_input, null)
        builder.setView(dialogView)

        val editTextName = dialogView.findViewById(R.id.editTextName) as EditText
        val editTextPersonName = dialogView.findViewById(R.id.editTextPersonName) as EditText
        val dpCalendar = dialogView.findViewById(R.id.dpCalendar) as DatePicker
        val tvInfo = dialogView.findViewById(R.id.tvInfo) as TextView
        if(airlane != null){
            builder.setTitle("Редактирование авиакомпании")
            tvInfo.text =getString(R.string.inputFaculty)
            editTextName.setText(airlane.name)
            editTextPersonName.setText(airlane.surname)
            val dt = GregorianCalendar().apply {
                time = Date(airlane!!.date!!)
            }
            dpCalendar.init(dt.get(Calendar.YEAR),dt.get(Calendar.MONTH),
                dt.get(Calendar.DAY_OF_MONTH),null)
        }
        builder.setPositiveButton(getString(R.string.commit)) { _, _, ->
            var p = true
            editTextName.text.toString().trim().ifBlank {
                p = false
                editTextName.error = "Укажите значение названия"
            }
            editTextPersonName.text.toString().trim().ifBlank {
                p = false
                editTextPersonName.error = "Укажите значение основателя"
            }
            if (p) {
                val selectedDate = GregorianCalendar().apply {
                    set(GregorianCalendar.YEAR, dpCalendar.year)
                    set(GregorianCalendar.MONTH, dpCalendar.month)
                    set(GregorianCalendar.DAY_OF_MONTH, dpCalendar.dayOfMonth)
                }
                if(airlane != null) {
                    val upAirlane = Airlane(airlane.id, editTextName.text.toString().trim(), editTextPersonName.text.toString().trim(),
                        selectedDate.time.time)
                    CoroutineScope(Dispatchers.Main).launch {
                        AppRepository.get().updateAirlane(upAirlane)
                    }
                }
                else
                    CoroutineScope(Dispatchers.Main). launch {
                        AppRepository.get().newAirlane(
                            editTextName.text.toString().trim(),
                            editTextPersonName.text.toString().trim(),
                            selectedDate.time.time
                        )
                    }
            }
            else {
                val builder1 = AlertDialog.Builder(requireContext())
                builder1.setTitle("Некорректный ввод")
                builder1.setMessage("Пожалуйста, заполните все поля.")
                builder1.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    // Код, который будет выполнен при нажатии на кнопку OK
                })
                builder1.setCancelable(false) // Запретить закрытие диалога при нажатии на кнопку Back
                builder1.show()
            }
        }
        builder.setNegativeButton(R.string.cansel, null)
        val alert = builder.create()
        alert.show()
    }
    private var lastItemView: View? = null

    private inner class AirlaneListAdapter(private val items: List<Airlane>)
        : RecyclerView.Adapter<AirlaneHolder>(){//адаптер RV для отображения элементов
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): AirlaneHolder {// кэш-объект элемента списка
            val view = layoutInflater.inflate(R.layout.element_airlane_list,parent,false)
            return AirlaneHolder(view)
        }

        override fun getItemCount(): Int = items.size //кол-во элементов в списке

        override fun onBindViewHolder(holder: AirlaneHolder, position: Int) {
            holder.bind(items[position])//связь элемента с AirlaneHolder
        }
    }
    //интерфейс для изменения title приложения на университет . Взаимодействие активити и фрагмента
    interface Callbacks{
        fun setTitle(_title: String)
        fun showAirlane (id: Long)
    }
    var callbacks : Callbacks? =null
    override fun onAttach(context: Context){//присоединение фрагмента к активности
        super.onAttach(context)
        callbacks=context as Callbacks
    }

    override fun onDetach(){//отсоединение фрагмента от активности
        callbacks=null
        super.onDetach()
    }
    //
}

