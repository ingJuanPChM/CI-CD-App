package com.lalorosas.retirementcalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.lalorosas.retirementcalculator.databinding.ActivityMainBinding
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import kotlin.math.pow

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCenter.start(application, "5640318d-ecff-4e38-9aca-c16793e6eef4", Analytics::class.java, Crashes::class.java);

        binding.calculateButton.setOnClickListener {
            // Crashes.generateTestCrash()
            try {
                val interestRate = binding.interestEditText.text.toString().toFloat()
                val currentAge = binding.ageEditText.text.toString().toInt()
                val retirementAge = binding.retirementEditText.text.toString().toInt()
                val monthly = binding.monthlySavingsEditText.text.toString().toFloat()
                val current = binding.currentEditText.text.toString().toFloat()

                val properties:HashMap<String, String> = HashMap<String, String>()
                properties.put("interest_rate", interestRate.toString())
                properties.put("current_age", currentAge.toString())
                properties.put("retirement_age", retirementAge.toString())
                properties.put("monthly_savings", monthly.toString())
                properties.put("current_savings", current.toString())

                if (interestRate <= 0) {
                    Analytics.trackEvent("wrong_interest_rate", properties)
                }
                if (retirementAge <= currentAge) {
                    Analytics.trackEvent("wrong_age", properties)
                }

                val futureSavings = calculateRetirement(interestRate, current, monthly, (retirementAge - currentAge)*12)

                binding.resultTextView.text = "At the current rate of $interestRate%, saving \$$monthly a month you will have \$${
                    String.format(
                        "%f",
                        futureSavings
                    )
                } by $retirementAge."

            } catch(ex: Exception){
                Analytics.trackEvent(ex.message)
            }
        }
    }

    fun calculateRetirement(interestRate: Float, currentSavings: Float, monthly: Float, numMonths: Int): Float {
        var futureSavings = currentSavings * (1+(interestRate/100/12)).pow(numMonths)

        for (i in 1..numMonths) {
            futureSavings += monthly * (1+(interestRate/100/12)).pow(i)
        }

        return  futureSavings
    }
}
