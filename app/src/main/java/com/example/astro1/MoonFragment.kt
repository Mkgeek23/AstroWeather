package com.example.astro1

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.astrocalculator.AstroCalculator
import com.astrocalculator.AstroDateTime
import kotlinx.android.synthetic.main.fragment_moon.*
import java.time.LocalDateTime
import kotlin.math.absoluteValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MoonFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MoonFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_moon, container, false)
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

        if(moonriseText==null)
            return

        moonriseText.setText(astroCalculator.moonInfo.moonrise.hour.toString().padStart(2, '0') + ":" + astroCalculator.moonInfo.moonrise.minute.toString().padStart(2, '0') + ":" +  astroCalculator.moonInfo.moonrise.second.toString().padStart(2, '0'))
        moonsetText.setText(astroCalculator.moonInfo.moonset.hour.toString().padStart(2, '0') + ":" + astroCalculator.moonInfo.moonset.minute.toString().padStart(2, '0') + ":" +  astroCalculator.moonInfo.moonset.second.toString().padStart(2, '0'))
        nextNewMoonText.setText(astroCalculator.moonInfo.nextNewMoon.day.toString().padStart(2, '0') + "-" + astroCalculator.moonInfo.nextNewMoon.month.toString().padStart(2, '0') + "-" +  astroCalculator.moonInfo.nextNewMoon.year.toString().padStart(4, '0'))
        nextFoolMoonText.setText(astroCalculator.moonInfo.nextFullMoon.day.toString().padStart(2, '0') + "-" + astroCalculator.moonInfo.nextFullMoon.month.toString().padStart(2, '0') + "-" +  astroCalculator.moonInfo.nextFullMoon.year.toString().padStart(4, '0'))
        moonIlluminationText.setText((astroCalculator.moonInfo.illumination*100).toInt().toString() + "%")
        moonAgeText.setText(String.format("%.2f", (astroCalculator.moonInfo.age/365.25*29.531)))


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
         * @return A new instance of fragment MoonFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MoonFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}