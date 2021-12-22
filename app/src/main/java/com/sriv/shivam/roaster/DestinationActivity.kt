package com.sriv.shivam.roaster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View.inflate
import android.widget.Toast
import com.sriv.shivam.roaster.databinding.ActivityDestinationBinding
import com.sriv.shivam.roaster.databinding.ActivityDestinationBinding.inflate
import com.sriv.shivam.roaster.databinding.ActivityMainBinding
import com.sriv.shivam.roaster.databinding.ActivityMainBinding.inflate

class DestinationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDestinationBinding
    var answer: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDestinationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createRandomQuestion()

        binding.buttonSubmit.setOnClickListener {
            if(binding.editTextAnswer.text.toString() == "") {
                Toast.makeText(this, "Please enter a value before submitting!", Toast.LENGTH_SHORT).show()
            }
            else if(binding.editTextAnswer.text.toString() == answer.toString()) {
                // Close the alarm and show message
                Toast.makeText(this, "Correct Answer. Alarm is set to off.", Toast.LENGTH_SHORT).show()
                AlarmReceiver.stopMedia()
            }
            else {
                // Error message for wrong answer
                Toast.makeText(this, "Wrong Answer. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createRandomQuestion() {
        // Generating 3 random numbers
        val num1 = (0..100).shuffled().last()
        val num2 = (0..100).shuffled().last()
        val num3 = (0..100).shuffled().last()

        answer = num1 + num2 - num3

        if(answer != null) {
            binding.tvQuestion.text = "$num1 + $num2 - $num3 = ?"
        }
        else {
            binding.tvQuestion.text = "Question unavailable!"
        }
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)
}