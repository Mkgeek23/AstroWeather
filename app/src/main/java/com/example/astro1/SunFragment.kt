package com.example.astro1

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.astrocalculator.AstroCalculator
import com.astrocalculator.AstroDateTime
import kotlinx.android.synthetic.main.activity_options.*
import kotlinx.android.synthetic.main.activity_view_pager.*
import kotlinx.android.synthetic.main.fragment_sun.*
import java.time.LocalDateTime
import java.time.Month
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SunFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SunFragment : Fragment() {


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var astroLocation: AstroCalculator.Location
    private lateinit var astroDateTime: AstroDateTime
    private lateinit var astroCalculator: AstroCalculator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sun, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        var myApplication: MyApplication

        astroLocation = AstroCalculator.Location((this.activity?.application as MyApplication).latitude, (this.activity?.application as MyApplication).longitude);
        astroDateTime = AstroDateTime();
        astroCalculator = AstroCalculator(astroDateTime, astroLocation);

        loadInfo()
    }



    private fun loadTime(currentDateTime: LocalDateTime){
        astroDateTime.year = currentDateTime.year
        astroDateTime.month = currentDateTime.monthValue
        astroDateTime.day = currentDateTime.dayOfMonth
        astroDateTime.hour = currentDateTime.hour+2
        astroDateTime.minute = currentDateTime.minute
        astroDateTime.second = currentDateTime.second
        astroDateTime.timezoneOffset = 1
        astroDateTime.isDaylightSaving = true

        astroCalculator.dateTime = astroDateTime
    }

    private fun loadInfo(){
        loadTime(LocalDateTime.now())

        if(sunriseText==null)
            return

        sunriseText.setText(astroCalculator.sunInfo.sunrise.hour.toString().padStart(2, '0') + ":" + astroCalculator.sunInfo.sunrise.minute.toString().padStart(2, '0') + ":" +  astroCalculator.sunInfo.sunrise.second.toString().padStart(2, '0'))
        sunsetText.setText(astroCalculator.sunInfo.sunset.hour.toString().padStart(2, '0') + ":" + astroCalculator.sunInfo.sunset.minute.toString().padStart(2, '0') + ":" +  astroCalculator.sunInfo.sunset.second.toString().padStart(2, '0'))
        azimuthRiseText.setText(astroCalculator.sunInfo.azimuthRise.absoluteValue.toInt().toString() + "° " + (astroCalculator.sunInfo.azimuthRise.absoluteValue*100%100).toInt().toString() + "'")
        azimuthSetText.setText(astroCalculator.sunInfo.azimuthSet.absoluteValue.toInt().toString() + "° " + (astroCalculator.sunInfo.azimuthSet.absoluteValue*100%100).toInt().toString() + "'")
        twilightEveningText.setText(astroCalculator.sunInfo.twilightEvening.hour.toString().padStart(2, '0') + ":" + astroCalculator.sunInfo.twilightEvening.minute.toString().padStart(2, '0') + ":" +  astroCalculator.sunInfo.twilightEvening.second.toString().padStart(2, '0'))
        twilightMorningText.setText(astroCalculator.sunInfo.twilightMorning.hour.toString().padStart(2, '0') + ":" + astroCalculator.sunInfo.twilightMorning.minute.toString().padStart(2, '0') + ":" +  astroCalculator.sunInfo.twilightMorning.second.toString().padStart(2, '0'))


        //toast("Odświeżono dane!")

        refresh((this.activity?.application as MyApplication).refreshTime.toLong())
    }

    private fun refresh(timeInSec: Long){
        val handler: Handler = Handler()
        val runnable: Runnable = Runnable {
            //handler.removeCallbacksAndMessages(null)
            loadInfo()
        }

        handler.postDelayed(runnable, timeInSec*1000);
    }

    fun toast(text: String){
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(this.activity?.applicationContext, text, duration)
        toast.show()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SunFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SunFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}