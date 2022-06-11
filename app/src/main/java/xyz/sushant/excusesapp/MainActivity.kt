package xyz.sushant.excusesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.sushant.excusesapp.base.network.ApiClient
import xyz.sushant.excusesapp.domain.entities.Excuse
import xyz.sushant.excusesapp.ui.theme.MyApplicationTheme
import kotlin.coroutines.CoroutineContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_name_input)
        initializeWidgets()
    }

    private fun initializeWidgets() {

    }

    private fun requestExcuseUsingCoroutines() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getRandomExcuse()

                if(response.isSuccessful && response.body() != null && response.body()!!.isNotEmpty()) {
                    withContext(Dispatchers.Main) { displayExcuse(response.body()!![0]) }
                } else {
                    throw Exception(response.errorBody()?.string())
                }
            }catch(e: Exception) {
                withContext(Dispatchers.Main) { displayError(e) }
            }
        }
    }

    private fun requestExcuseUsingCall() {
        val request: Call<Excuse> = ApiClient.apiService.getExcuseWithId(1)

        request.enqueue(object: Callback<Excuse> {
            override fun onResponse(call: Call<Excuse>, response: Response<Excuse>) {
                if(response.body() != null) {
                    displayExcuse(response.body()!!)
                }
            }

            override fun onFailure(call: Call<Excuse>, t: Throwable) {
                displayError(t)
            }

        })
    }

    fun displayExcuse(excuse: Excuse) {

    }

    fun displayError(t: Throwable) {

    }
}