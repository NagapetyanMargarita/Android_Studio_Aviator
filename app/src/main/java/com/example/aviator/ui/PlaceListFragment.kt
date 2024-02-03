package com.example.aviator.ui

import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aviator.R
import com.example.aviator.data.Flight
import com.example.aviator.data.Place
import com.example.aviator.databinding.FragmentFlightBinding
import com.example.aviator.models.PlaceListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class PlaceListFragment (private val place: Place) : Fragment() {
    private lateinit var viewModel: PlaceListViewModel
    private var _binding: FragmentFlightBinding? = null
    private var lastItemView: View? = null
    private val binding get() = _binding!!

    private val flightsTime = arrayOf("10:00", "12:00", "14:00", "16:00", "18:00", "20:00")
    private val daysOfWeek = arrayOf("Воскресенье","Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота")
    private val planeTypes = arrayOf("superjet S", "superjet M", "superjet L")

    //TODO возможно придется перенести flights в репозиторий и хранить больше параметров в классе FlightPlane

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFlightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvFlight.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        viewModel = ViewModelProvider(this)[PlaceListViewModel::class.java]
        viewModel.setPlaceID(place.id!!)
        viewModel.flight.observe(viewLifecycleOwner){
            binding.rvFlight.adapter=CityFlightListAdapter(it)
        }
    }

    private inner class CityHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        lateinit var flight: Flight

        fun bind(flight: Flight) {
            this.flight = flight
            itemView.findViewById<Button>(R.id.openBtn).text = flight.fromPlace.plus(" -\n")
                .plus(flight.inPlace)
            itemView.findViewById<TextView>(R.id.tvAirlaneElement).text="${flight.dayOfWeek} ${flight.timev.plus(" \n")} Цена: ${flight.prices}Р"
            itemView.findViewById<ConstraintLayout>(R.id.crudButtons).visibility = View.GONE
            itemView.findViewById<Button>(R.id.openBtn).setOnClickListener {
                   //buyTicketDialog(flight)
            }
        }

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val cl = itemView.findViewById<ConstraintLayout>(R.id.crudButtons)
            cl.visibility = View.VISIBLE
            lastItemView?.findViewById<ConstraintLayout>(R.id.crudButtons)?.visibility = View.GONE
            lastItemView = if (lastItemView == itemView) null else itemView
            if (cl.visibility == View.VISIBLE) {
                itemView.findViewById<ImageButton>(R.id.delBtn).setOnClickListener {
                    commitDelete(flight)
                }
                itemView.findViewById<ImageButton>(R.id.editBtn).setOnLongClickListener {
                    callbacks?.showPlace(place.id!!, flight)
                    true
                }

            }
        }
    }

    private fun commitDelete(flight: Flight) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        builder.setMessage("Удалить рейс ${flight.fromPlace.plus(" - ")
            .plus(flight.inPlace)} из списка?")
        builder.setTitle("Подтверждение")
        builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
            CoroutineScope(Dispatchers.Main). launch {
                viewModel.deleteFlight(flight)
            }
        }
        builder.setNegativeButton(R.string.cansel, null)
        builder.show()
    }

    // диалог для покупки билетов
  /*  private fun buyTicketDialog(flight: Flight){
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(true)
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.ticket_input, null)
        builder.setView(dialogView)

        val spinDate = dialogView.findViewById(R.id.spinDate) as Spinner
        val spinSeat = dialogView.findViewById(R.id.spinSeat) as Spinner
        val tvFlight = dialogView.findViewById(R.id.tvFlight) as TextView
        val tvTypeOfPlane = dialogView.findViewById(R.id.tvTypeOfPlane) as TextView
        val tvTime = dialogView.findViewById(R.id.tvTime) as TextView
        val tvPrice = dialogView.findViewById(R.id.tvPrice) as TextView
        val tvBag = dialogView.findViewById(R.id.tvBag) as EditText
        builder.setTitle(getString(R.string.inputTicket))

        tvFlight.setText("Маршрут рейса: ${flight.fromPlace} - ${flight.inPlace}")
        tvTypeOfPlane.setText("Тип самолета: ${flight.nameOfPlane}")
        tvTime.setText("Вылет осуществляется: ${flight.dayOfWeek} в ${flight.timev}")
        tvPrice.setText(" ${flight.prices} P")

        val dates = ArrayList<String>()
        /*for (plane in flight.airplanes){
            dates.add(plane.date)
        }*/
        tvBag.setOnClickListener {
            var sum = flight.prices?.plus(tvBag.text.toString().toInt() * 50)
            tvPrice.setText(" ${sum.toString()} P")
        }

        val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dates)
        spinDate.adapter = adapter1

        spinDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {//При нажатии на дату
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val date = spinDate.getItemAtPosition(position) as String//сохраняем выбран элемент
            //val plane = flight.airplanes.find { it.date == date }//ищем самолет по дате
            val seats = ArrayList<String>()
            for (seat in plane?.seats!!)
                seats.add(seat.name)//создаем список мест и добавл название места
            spinSeat.adapter = SeatAdapter(requireContext(), plane.seats)//отображение мест

        }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Если ничего не выбрано
            }
        }

        builder.setPositiveButton("Приобрести") { _, _, ->
            viewModel.add_ticket(flight.id, spinDate.selectedItem.toString(), spinSeat.selectedItem.toString())
            Toast.makeText(requireContext(), "Вы приобрели билет", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton(R.string.cancel, null)
        val alert = builder.create()
        alert.show()
    }
    class SeatAdapter(context: Context, seats: List<Seat>) :
        ArrayAdapter<Seat>(context, android.R.layout.simple_spinner_item, seats) {
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)

            val seat = getItem(position)
            if (seat != null) {
                //if (seat.name == "1A" && !seat.isFree)
                // view.setBackgroundColor(Color.RED)
                val color = if (seat.isFree) Color.WHITE else Color.RED
                view.setBackgroundColor(color)
                view.isEnabled = seat.isFree
                view.isClickable = !seat.isFree//отсутствие возмодности выбора
            }

            return view
        }
    }*/

    private inner class CityFlightListAdapter(private val items: List<Flight>) :
        RecyclerView.Adapter<CityHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): CityHolder { val view = layoutInflater.inflate(R.layout.element_airlane_list, parent, false
                )
            return CityHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: CityHolder, position: Int) {
            holder.bind(items[position])
        }
    }

    interface Callbacks {
        fun showPlace(placeId: Long,  flight: Flight?)
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