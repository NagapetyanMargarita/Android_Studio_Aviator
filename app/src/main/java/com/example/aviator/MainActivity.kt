package com.example.aviator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentTransaction
import com.example.aviator.data.Flight
import com.example.aviator.repository.AppRepository
import com.example.aviator.ui.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
//import com.example.aviator.ui.PLACE_TAG
//import com.example.aviator.ui.PlaceFragment
import java.util.*

class MainActivity : AppCompatActivity(), AirlaneFragment.Callbacks, PlaceFragment.Callbacks,
  PlaceListFragment.Callbacks
     {
    private var miNewAirlane: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager
            .beginTransaction()
            //замена текущий фрагмент на новый FacultyFragment, который мы создаем с помощью метода newInstance() + Тэг
            .replace(R.id.mainFrame, AirlaneFragment.newInstance(), AIRLANE_TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)//анимацию перехода между фрагментами
            .commit()// чтобы завершить транзакцию фрагментов и применить все изменения.


        //функционал кнопки назад. передача данной активности(this)
        onBackPressedDispatcher.addCallback(this, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount>0){
                    supportFragmentManager.popBackStack()//возврат на прошлую вкладку
                }
                else
                    finish()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        miNewAirlane = menu?.findItem(R.id.miNewAirlanePlace)
        return true
    }
         //Работа с меню приложения (добавить и т.п)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.miNewAirlanePlace -> {
                val myFragment = supportFragmentManager.findFragmentByTag(PLACE_TAG)//есть ли фрагмент с тегом GROUP_TAG в менеджере фрагментов supportFragmentManager
                if (myFragment == null){
                    showNameInputDialog(0)
                }
                else
                    showNameInputDialog(1
                    )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
         //Диалоговое окно с заполнением Авиакомпании и города
    private fun showNameInputDialog(index:Int=-1){//создание диалогового окна
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(true)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.name_input, null)//подсоединение html
        builder.setView(dialogView)
        val nameInput = dialogView.findViewById(R.id.editTextName) as EditText//элементы управления в макете
        val surnameInput = dialogView.findViewById(R.id.editTextPersonName) as EditText//элементы управления в макете
        val tvInfo = dialogView.findViewById(R.id.tvInfo) as TextView//элементы управления в макете
        val cdate = dialogView.findViewById(R.id.dpCalendar) as DatePicker
        builder.setTitle(getString(R.string.inputTitle))//устанавливаемм заголовок
        when (index){
            0 ->{

                tvInfo.text =getString(R.string.inputFaculty)
                builder.setPositiveButton(getString(R.string.commit)){_, _ ->//обработчик нажатия ок
                    val s = nameInput.text.toString()//получение значения из поля
                    val sn = surnameInput.text.toString()//получение значения из поля

                    val year = cdate.year
                    val month = cdate.month
                    val dayOfMonth = cdate.dayOfMonth
                    val calendar = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }

                    val datem = calendar.timeInMillis
                    if (s.isNotBlank() && sn.isNotBlank()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            AppRepository.get().newAirlane(s, sn, datem)//вызов метода нф в репозитории
                        }
                    }
                }
            }
            1 -> {
                cdate.visibility = View.GONE
                surnameInput.visibility = View.GONE
                tvInfo.text = getString(R.string.inputPlace)
                builder.setPositiveButton(getString(R.string.commit)) { _, _ ->
                    val s = nameInput.text.toString()
                    if (s.isNotBlank()) {
                        CoroutineScope(Dispatchers.Main).launch {
                            AppRepository.get().newPlace(PlaceFragment. getAirlineId,s)//вызов метода нф в репозитории
                        }
                    }
                }
            }
        }
        builder.setNegativeButton(R.string.cansel, null)
        val alert = builder.create()
        alert.show()
    }

    override fun setTitle(_title: String) {
        title=_title
    }
         override fun showPlace(placeID: Long, flight: Flight?) {
             supportFragmentManager
                 .beginTransaction()
                 .replace(R.id.mainFrame, FlightFragment.newInstance(placeID, flight), FLIGHT_TAG)
                 .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                 .addToBackStack(null)
                 .commit()
         }
    //Функция срабатывает при нажатии Названия Авиакомпании и перебрасывает на фрагмент Города. связь между фрагментами
    override fun showAirlane(id: Long) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrame, PlaceFragment.newInstance(id), PLACE_TAG)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .addToBackStack(null)// позволяет пользователю вернуться к предыдущему фрагменту, когда он нажимает кнопку "назад" на устройстве.
            .commit()
    }
}