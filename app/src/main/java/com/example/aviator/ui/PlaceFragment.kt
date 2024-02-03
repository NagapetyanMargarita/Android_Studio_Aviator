package com.example.aviator.ui

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.aviator.R
import com.example.aviator.data.Airlane
import com.example.aviator.data.Flight
import com.example.aviator.data.Place
import com.example.aviator.databinding.FragmentPlaceBinding
import com.example.aviator.models.PlaceViewModel
import com.example.aviator.repository.AppRepository
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

const val  PLACE_TAG = "PlaceFragment"
class PlaceFragment : Fragment() {
    private var miDelAirlane: MenuItem? = null
    private var miModAirlane: MenuItem? = null

    private var _binding: FragmentPlaceBinding? = null

    private val binding
        get() = _binding!!

    companion object {
        private  var id : Long=-1
        private  var _place: Place?=null
        fun newInstance(id: Long): PlaceFragment{
           // PlaceFragment()//создание нового объекта с id
            this.id=id
            return PlaceFragment()
        }
         val getAirlineId
            get() = id
    }

    private lateinit var viewModel: PlaceViewModel

    //Работа с Меню
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu_place, menu)
        miDelAirlane = menu?.findItem(R.id.miDelAirlanePlace)
        miModAirlane = menu?.findItem(R.id.miModAirlanePlace)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var per = -1
         when (item?.itemId) {
            R.id.miDelAirlanePlace -> {
            per = 0
            }
             R.id.miModAirlanePlace -> {
                 per = 1
             }
        }
        if(binding.tabPlace.tabCount > 0){
            if (per == 0)
                 commitDelete(_place!!)
             if (per == 1)
             { editCreate(_place)
                 }
        }
        else
            Toast.makeText(requireContext(), "Список городов пуст", Toast.LENGTH_SHORT).show()
        return super.onOptionsItemSelected(item)
    }
    //

    //Фрагмент для Сохранения
   /* override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.let {
        }
    }*/
    //
//Создание польз. интерфейса + работа с RecyclerView. Создание компонентов внутри фрагмента. загрузка интерфейса
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _binding = FragmentPlaceBinding.inflate(inflater,container,false)//привязка фрагмента
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(PlaceViewModel:: class.java)
        viewModel.setAirlane(getAirlineId)

        CoroutineScope(Dispatchers.Main).launch {
            val f = viewModel.getAirlane()//получение аэропорта по id
            callbacks?.setTitle(f?.name ?: "UNKNOWN")
        }
        viewModel.place.observe(viewLifecycleOwner){
            updateUI(it)
        }
    }
    //Удаление Городов
    private fun commitDelete(place: Place) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setMessage("Удалить город ${place.name} ?")
        builder.setTitle("Подтверждение")
        builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
            CoroutineScope(Dispatchers.Main). launch {
                viewModel.deletePlace(place)
            }
        }
        builder.setNegativeButton(R.string.cansel, null)
        builder.show()
    }
    //Редактирование Городов
    private fun editCreate(place: Place?){
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.name_input, null)
        builder.setView(dialogView)
        val editTextName = dialogView.findViewById(R.id.editTextName) as EditText
        val editTextPersonName = dialogView.findViewById(R.id.editTextPersonName) as EditText
        val dpCalendar = dialogView.findViewById(R.id.dpCalendar) as DatePicker
        val tvInfo = dialogView.findViewById(R.id.tvInfo) as TextView
        dpCalendar.visibility = View.GONE
        editTextPersonName.visibility = View.GONE
        if(place != null){
            builder.setTitle("Редактирование города")
            tvInfo.text =getString(R.string.inputPlace)
            editTextName.setText(place.name)
        }
        else
            builder.setTitle("Добавление города")
        builder.setPositiveButton(getString(R.string.commit)) { _, _, ->
            var p = true
            editTextName.text.toString().trim().ifBlank {
                p = false
                editTextName.error = "Укажите значение"
            }
            if (p) {
                val s = editTextName.text.toString().trim()//получение значения
                if(place != null) {
                    val upPlace = Place(place.id, editTextName.text.toString().trim(), getAirlineId)
                    CoroutineScope(Dispatchers.Main).launch {
                        viewModel.editPlace(upPlace)
                    }
                }
                else {
                    CoroutineScope(Dispatchers.Main). launch {
                        AppRepository.get().newPlace(getAirlineId,s)
                    }
                }
            }
            else {
                Toast.makeText(requireContext(), "Пожалуйста, заполните все поля.", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton(R.string.cansel, null)
        val alert = builder.create()
        alert.show()
    }

    private var tabPosition: Int = 0

    private fun updateUI(places: List<Place>) {
        binding.tabPlace.clearOnTabSelectedListeners()
        binding.tabPlace.removeAllTabs()

        binding.faBtnNewFlight.visibility= if((places.size) > 0) {
            binding.faBtnNewFlight.setOnClickListener {
                callbacks?.showPlace(places.get(tabPosition).id!!, null)
            }
            View.VISIBLE
        }
        else View.GONE
        for (i in 0 until (places?.size ?: 0)) {
            binding.tabPlace.addTab(binding.tabPlace.newTab().apply {
                text = i.toString()
            })
        }

        val adapter = GroupPageAdapter(requireActivity(), places!!)
        binding.vpPlace.adapter = adapter
        TabLayoutMediator(binding.tabPlace, binding.vpPlace, true, true) { tab, pos ->
            tab.text = places.get(pos).name
        }.attach()
        if (tabPosition < binding.tabPlace.tabCount){
            binding.tabPlace.selectTab(binding.tabPlace.getTabAt(tabPosition))
            if (places.size > 0){
                _place = places[tabPosition]
            }
        }
        else{
            binding.tabPlace.selectTab(binding.tabPlace.getTabAt(tabPosition - 1))
            if (places.size > 0){
                _place= places[tabPosition - 1]
            }
        }
        binding.tabPlace.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tabPosition = tab?.position!!
                _place = places[tabPosition]
                viewModel.loadFlight(places[tabPosition].id!!)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }
    private inner class GroupPageAdapter(fa: FragmentActivity, private val places: List<Place>) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int {//возвращает кол-во элементов списка гоодов в авиак
            return (places.size ?:0)
        }

        override fun createFragment(position: Int): Fragment {
            return PlaceListFragment(places[position])
        }
    }
    //Callbacks
    interface Callbacks {
        fun setTitle(_title: String)
        fun showPlace(placeID: Long, flight: Flight?)
    }

    var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onDetach() {
        callbacks = null
        super.onDetach()
    }
}