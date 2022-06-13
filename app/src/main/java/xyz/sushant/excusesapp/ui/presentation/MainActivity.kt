package xyz.sushant.excusesapp.ui.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.sushant.excusesapp.R
import xyz.sushant.excusesapp.base.network.ApiClient
import xyz.sushant.excusesapp.databinding.ActivityNewExcuseBinding
import xyz.sushant.excusesapp.domain.entities.Excuse
import java.net.UnknownHostException

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityNewExcuseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityNewExcuseBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.appbar.toolbar)

        supportActionBar?.apply {
            title = "Excuses"
            subtitle = "New Excuses"
        }

        initializeWidgets()
    }

    fun apiCallForId(id: Int) {
        showLoading()
        when(id) {
            R.id.random -> requestExcuseUsingCoroutines()
            R.id.family -> requestExcuseUsingCall("family")
            R.id.office -> requestExcuseUsingCall("office")
            R.id.children -> requestExcuseUsingCall("children")
            R.id.college -> requestExcuseUsingCall("college")
        }
    }

    private fun initializeWidgets() {
        with(viewBinding) {
            categoryRadio.setOnCheckedChangeListener { _, id ->
                apiCallForId(id)
            }

            nextButton.setOnClickListener {
                if(categoryRadio.checkedRadioButtonId != -1) {
                    apiCallForId(categoryRadio.checkedRadioButtonId)
                }
            }
        }
    }

    private fun requestExcuseUsingCoroutines() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getRandomExcuse()

                if (response.isSuccessful && response.body() != null && response.body()!!
                        .isNotEmpty()
                ) {
                    withContext(Dispatchers.Main) { displayExcuse(response.body()!![0]) }
                } else {
                    throw Exception(response.errorBody()?.string())
                }
            }catch(networkError: UnknownHostException) {

                withContext(Dispatchers.Main) { displayError(Exception("Could not connect to server")) }
            }catch(e: Exception) {
                withContext(Dispatchers.Main) { displayError(e) }
            }
        }
    }

    private fun requestExcuseUsingCall(category: String) {
        val request: Call<List<Excuse>> = ApiClient.apiService.getExcuseForCategoryViaCall(category)

        request.enqueue(object: Callback<List<Excuse>> {
            override fun onResponse(call: Call<List<Excuse>>, response: Response<List<Excuse>>) {
                if(response.body() != null && response.body()!!.isNotEmpty()) {
                    displayExcuse(response.body()!![0])
                }
            }

            override fun onFailure(call: Call<List<Excuse>>, t: Throwable) {
                displayError(t)
            }

        })
    }

    fun showLoading() {
        with(viewBinding) {
            categoryRadio.isEnabled = false
            progressCircular.visibility = View.VISIBLE
        }
    }

    fun dismissLoading() {
        with(viewBinding) {
            categoryRadio.isEnabled = true
            progressCircular.visibility = View.GONE
        }
    }

    fun displayExcuse(excuse: Excuse) {
        viewBinding.excuseText.text = excuse.excuse
        dismissLoading()
    }

    @SuppressLint("SetTextI18n")
    fun displayError(t: Throwable) {
        viewBinding.excuseText.text = "Error!!"
        Toast.makeText(this, t.message ?: "Oops!! Something went wrong", Toast.LENGTH_SHORT).show()
        dismissLoading()
    }
}