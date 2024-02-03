package com.example.aviator.ui

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aviator.R
import com.example.aviator.data.AirPlane
import com.example.aviator.data.Flight
import com.example.aviator.data.Place
import com.example.aviator.data.Seat
import com.example.aviator.databinding.FlightInputBinding
import com.example.aviator.databinding.FragmentFlightBinding
import com.example.aviator.models.FlightViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.*

const val FLIGHT_TAG="FlightFragment"
class FlightFragment (): Fragment() {

 private var _binding: FlightInputBinding? = null

private val binding
    get() = _binding!!
//private var adapter: FlightFragment.FlightListAdapter = FlightListAdapter(emptyList())
private val daysOfWeek = arrayOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота","Воскресенье")
    private val planeTypes = arrayOf("Pegas", "Vodolaz", "Ananas")
    val plaseONflights = listOf("Сочи", "Москва", "Екатеринбург", "Краснодар", "Брянск", "Ульяновск")

companion object {
    //private  var airlaneID: Long = -1
    private  var placeID: Long = -1
    private var flight: Flight? = null
    fun newInstance( placeID: Long, flight: Flight?): FlightFragment {
       // this.airlaneID = airlaneID
        this.placeID = placeID
        this.flight = flight
        return FlightFragment()
    }

}

   private lateinit var viewModel: FlightViewModel


//Создание польз. интерфейса + работа с RecyclerView. Создание компонентов внутри фрагмента. загрузка интерфейса
override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    _binding = FlightInputBinding.inflate(inflater, container, false)
    return binding.root

}
    val backPressedCallback=object: OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
            showCommitDialog()
        }
    }
    override fun onAttach(context: Context){
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewModel = ViewModelProvider(this).get(FlightViewModel::class.java)
    val adapterDayOfWeek = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, daysOfWeek)
    binding.spinDayOfWeek.adapter = adapterDayOfWeek
    val adapterPlane = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, planeTypes)
    binding.spinTypeOfPlane.adapter = adapterPlane
    val adapterPlace = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, plaseONflights)
    binding.spinArrivalCity.adapter = adapterPlane

    //binding.tvFlightDepCityValue.text = cityName

    if (flight!= null) {
        binding.tvDepartureCity.setText(flight!!.fromPlace)
        binding.spinArrivalCity.setSelection(planeTypes.indexOf(flight!!.dayOfWeek))
        //binding.timePicker.setText(flight!!.timev)
        binding.price.setText(flight!!.prices.toString())
        binding.spinDayOfWeek.setSelection(daysOfWeek.indexOf(flight!!.dayOfWeek))
        binding.spinTypeOfPlane.setSelection(planeTypes.indexOf(flight!!.nameOfPlane))
    }
}



/*private fun buyTicketDialog(flight: Flight){
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
    tvPrice.setText(" ${flight.price} P")

    val dates = ArrayList<String>()
    for (plane in flight.airplanes){
        dates.add(plane.date)
    }
    tvBag.setOnClickListener {
        var sum = flight.price + tvBag.text.toString().toInt() * 50
        tvPrice.setText(" ${sum.toString()} P")
    }

    val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dates)
    spinDate.adapter = adapter1

    spinDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {//При нажатии на дату
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val date = spinDate.getItemAtPosition(position) as String//сохраняем выбран элемент
            val plane = flight.airplanes.find { it.date == date }//ищем самолет по дате
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
private fun showCommitDialog(){
    val builder = AlertDialog.Builder(requireContext())
    builder.setCancelable(true)
    val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.flight_input, null)
    builder.setView(dialogView)
    val tvDepartureCity = dialogView.findViewById(R.id.tvDepartureCity) as TextView
    val spinArrivalCity = dialogView.findViewById(R.id.spinArrivalCity) as Spinner
    var price = dialogView.findViewById(R.id.price) as EditText
    val spinDayOfWeek = dialogView.findViewById(R.id.spinDayOfWeek) as Spinner
    val spinTypeOfPlane = dialogView.findViewById(R.id.spinTypeOfPlane) as Spinner
    val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)
    CoroutineScope(Dispatchers.Main).launch {
        val f = viewModel.NameOfPlace()//получение аэропорта по id
        tvDepartureCity.text = f?.name
    }


    val plaseONflights = listOf("Сочи", "Москва", "Екатеринбург", "Краснодар", "Брянск", "Ульяновск")
    val placeTypes = listOf(
        "Pegas",
        "Vodolaz",
        "Ananas"
    )

    val daysOfWeek = listOf("Понедельник", "Вторник", "Среда", "Четверг",
        "Пятница", "Суббота", "Воскресенье")
    val availableAirplaneTypes = listOf( "Pegas", "Vodolaz", "Ananas")

    val typeOfPlaneMap = mapOf("Pegas" to 240, "Vodolaz" to 60, "Ananas" to 120)

    val dayOfWeekMap = mapOf("Понедельник" to DayOfWeek.MONDAY, "Вторник" to DayOfWeek.TUESDAY,
        "Среда" to DayOfWeek.WEDNESDAY, "Четверг" to DayOfWeek.THURSDAY, "Пятница" to DayOfWeek.FRIDAY,
        "Суббота" to DayOfWeek.SATURDAY, "Воскресенье" to DayOfWeek.SUNDAY
    )

    val adapter1 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, daysOfWeek)
    spinDayOfWeek.adapter = adapter1
    val adapter2 = ArrayAdapter<String>(requireContext(),
        android.R.layout.simple_spinner_dropdown_item, availableAirplaneTypes)
    spinTypeOfPlane.adapter = adapter2
    val adapter3 = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, plaseONflights)
    spinArrivalCity.adapter = adapter3

    builder.setPositiveButton(getString(R.string.commit)) { _, _, ->
        var p = true
        price.text.toString().trim().ifBlank {
            p = false
            price.error = "Укажите значение"
        }
        if (p) {
            val startDate = LocalDate.now()//иниц данной даты
            val endDate = startDate.plusDays(65)// иниц кон даты
            val datesInRange = mutableListOf<LocalDate>()
            var date = startDate
            /*while (!date.isAfter(endDate)) {
                datesInRange.add(date)// собираем список каждой даты от нач до кон
                date = date.plusDays(1)
            }

            val dayOfWeek = dayOfWeekMap[spinDayOfWeek.selectedItem.toString()]
            val days = datesInRange.filter { it.dayOfWeek == dayOfWeek }//выбор определенных дней
            val planes = ArrayList<AirPlane>()
            for (day in days){
                val numberOfSeats = typeOfPlaneMap[spinTypeOfPlane.selectedItem.toString()] ?: 0//по самолету выбирается typeOfPlaneMap
                val numberOfRows = (typeOfPlaneMap[spinTypeOfPlane.selectedItem.toString()] ?: 0) / 3

                val plane = AirPlane(
                    id = null,
                    date = day.toString(),
                    num_Seats = numberOfSeats,
                    num_Rows = numberOfRows,
                    flightID = flight?.id
                )
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.newAirplaneWithSeats(plane, numberOfRows)
                }
                planes.add(plane)
            }*/

            if(flight==null) {
              val flight = Flight(
                    id=null,
                    fromPlace = tvDepartureCity.text.toString().trim(),
                    inPlace = spinArrivalCity.selectedItem.toString().trim(),
                    nameOfPlane = spinTypeOfPlane.selectedItem.toString().trim(),
                    dayOfWeek = spinDayOfWeek.selectedItem.toString().trim(),
                    prices = price.text.toString().toInt(),
                    timev = "${timePicker.hour}:${timePicker.minute}",
                    placeID= placeID
                )
                CoroutineScope(Dispatchers.Main). launch {
                    viewModel.newFlight(flight!!, placeID)
                }
            }
            else {
                flight?.apply{
                    fromPlace = binding.tvDepartureCity.text.toString()
                    inPlace = binding.spinArrivalCity.selectedItem.toString().trim()
                    nameOfPlane = binding.spinTypeOfPlane.selectedItem.toString().trim()
                    dayOfWeek = binding.spinDayOfWeek.selectedItem.toString().trim()
                    prices = price.text.toString().toInt()
                    timev = "${timePicker.hour}:${timePicker.minute}"
                }
              /* val upFlight = Flight(flight.id!!, tvDepartureCity.text.toString().trim(),spinArrivalCity.selectedItem.toString().trim(),
                    spinTypeOfPlane.selectedItem.toString().trim(),price.text.toString().toInt(),"${timePicker.hour}:${timePicker.minute}",
                    spinDayOfWeek.selectedItem.toString().trim(), placeID)*/
                CoroutineScope(Dispatchers.Main). launch {
                    viewModel.editFlight(flight!!)
                }

            }
            backPressedCallback.isEnabled = false
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
    builder.setNegativeButton(R.string.cansel) { _, _ ->
        backPressedCallback.isEnabled = false
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }
    var alert = builder.create()
    alert.show()
}
}

