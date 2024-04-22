package com.example.guidomia

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.guidomia.databinding.ActivityMainBinding
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        val carList = loadCarsFromJson(this, R.raw.car_list)

        with(binding) {
            rvCarList.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = CarExpandableListAdapter(this@MainActivity, carList, this)
                setHasFixedSize(true)
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

private fun loadCarsFromJson(context: Context, resourceId: Int): List<Car> {
    val jsonString = readRawResource(context, resourceId)
    val gson = Gson()
    return gson.fromJson(jsonString, Array<Car>::class.java).toList()
}
