package com.example.guidomia

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guidomia.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private var carListAdapter: CarExpandableListAdapter? = null

    private lateinit var jsonDataStoreManager: JsonDataStoreManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        jsonDataStoreManager = JsonDataStoreManager(context = this)

        // Check if JSON data is already saved
        lifecycleScope.launch {
            jsonDataStoreManager.getJsonData().collect { jsonData ->
                if (jsonData != null) {
                    processData(jsonData)
                } else {
                    val rawJsonString = readRawResource(this@MainActivity, R.raw.car_list)
                    jsonDataStoreManager.saveJsonData(rawJsonString)
                    processData(rawJsonString)
                }
            }
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

    private fun loadCarsFromJson(jsonData: String): List<Car> {
        return Gson().fromJson(jsonData, Array<Car>::class.java).toList()
    }

    private fun processData(jsonData: String) {
        val carList = loadCarsFromJson(jsonData)

        with(binding) {
            rvCarList.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)

                carListAdapter = CarExpandableListAdapter(this@MainActivity, carList, this)
                adapter = carListAdapter
            }

            val makeList = mutableListOf(getString(R.string.filter_any_make))
            makeList.addAll(carList.map { it.make }.distinct())
            val makeAdapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, makeList)
            makeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            val modelList = mutableListOf(getString(R.string.filter_any_model))
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
}
