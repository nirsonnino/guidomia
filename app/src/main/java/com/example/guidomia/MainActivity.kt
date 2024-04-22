package com.example.guidomia

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guidomia.databinding.ActivityMainBinding
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private var carListAdapter: CarExpandableListAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        val carList = loadCarsFromJson(this, R.raw.car_list)

        with(binding) {
            rvCarList.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)

                carListAdapter = CarExpandableListAdapter(this@MainActivity, carList, this)
                adapter = carListAdapter
            }

            val makeList = mutableListOf("Any make")
            makeList.addAll(carList.map { it.make }.distinct())
            val makeAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, makeList)
            makeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            val modelList = mutableListOf("Any model")
            modelList.addAll(carList.map { it.model }.distinct())
            val modelAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, modelList)
            modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinnerMake.apply {
                setSelection(0)
                adapter = makeAdapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val make = parent?.getItemAtPosition(position).toString()
                        carListAdapter?.filter?.filter(make)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Do nothing
                    }
                }
            }

            spinnerModel.apply {
                setSelection(0)
                adapter = modelAdapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val model = parent?.getItemAtPosition(position).toString()
                        carListAdapter?.filter?.filter(model)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Do nothing
                    }
                }
            }

            carListAdapter?.filter?.filter(null)
        }
    }

    private fun readRawResource(context: Context, resourceId: Int): String {
        val inputStream = context.resources.openRawResource(resourceId)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
        inputStream.close()
        return stringBuilder.toString()
    }

    private fun loadCarsFromJson(context: Context, resourceId: Int): List<Car> {
        val jsonString = readRawResource(context, resourceId)
        val gson = Gson()
        return gson.fromJson(jsonString, Array<Car>::class.java).toList()
    }
}
