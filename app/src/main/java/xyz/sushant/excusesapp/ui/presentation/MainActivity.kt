package xyz.sushant.excusesapp.ui.presentation

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.sushant.excusesapp.R
import xyz.sushant.excusesapp.base.localdb.AppDatabase
import xyz.sushant.excusesapp.base.network.ApiClient
import xyz.sushant.excusesapp.constants.AppConstants.*
import xyz.sushant.excusesapp.databinding.ActivityNewExcuseBinding
import xyz.sushant.excusesapp.databinding.UserNameInputBinding
import xyz.sushant.excusesapp.domain.dataTransfer.ExcuseDTO
import xyz.sushant.excusesapp.domain.entities.Excuse
import xyz.sushant.excusesapp.domain.mappers.ExcuseMapper
import java.net.UnknownHostException

class MainActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityNewExcuseBinding
    val excuseDao by lazy {
        AppDatabase.getInstance(this).excuseDao()
    }

    fun getNewSubtitle() : String {
        val subtitle = "New Excuses!!"
        if(getSharedPreferences(USER_PREFS, MODE_PRIVATE).getString(KEY_USERNAME_PREF, "").isNullOrBlank()) return subtitle;
        return "Hi ${getSharedPreferences(USER_PREFS, MODE_PRIVATE).getString(KEY_USERNAME_PREF, "")}!!"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityNewExcuseBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.appbar.toolbar)

        supportActionBar?.apply {
            title = "Excuses"
            subtitle = getNewSubtitle()
        }

        initializeWidgets()
        tryNewUserFlow()
    }

    val isNewUser
    get() = getPreferences(MODE_PRIVATE).getBoolean(KEY_NEW_USER_PREF, true)

    fun tryNewUserFlow() {
        if(isNewUser) {
            val newUserInputDialog = Dialog(this)

            val dialogBinding = UserNameInputBinding.inflate(layoutInflater)
            newUserInputDialog.setContentView(dialogBinding.root)

            dialogBinding.saveUserName.setOnClickListener {
                val name = dialogBinding.userNameInput.text
                if(name.isBlank()) {
                    Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                supportActionBar?.subtitle = "Hi $name"

                getSharedPreferences(USER_PREFS, MODE_PRIVATE).edit().putString(KEY_USERNAME_PREF, name.toString()).apply()
                getPreferences(MODE_PRIVATE).edit().putBoolean(KEY_NEW_USER_PREF, false).apply()

                newUserInputDialog.dismiss()
            }

            newUserInputDialog.show()
        }
    }

    fun getCategoryForId(id: Int) = when(id) {
            R.id.family -> "family"
            R.id.office -> "office"
            R.id.children -> "children"
            else -> "%%"
        }

    fun apiCallForId(id: Int) {
        showLoading()
        when(id) {
            R.id.random -> requestExcuseUsingCoroutines()
            R.id.family -> requestExcuseUsingCall("family")
            R.id.office -> requestExcuseUsingCall("office")
            R.id.children -> requestExcuseUsingCall("children")
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

                    //Adding excuses to database
                    excuseDao.addExcusesList(response.body()!!.map { ExcuseMapper().mapToEntity(it) })
                } else {
                    throw Exception(response.errorBody()?.string())
                }
            }catch(networkError: UnknownHostException) {
                withContext(Dispatchers.Main) { handleErrorInNetworkCall(Exception("Could not connect to server")) }
            }catch(e: Exception) {
                withContext(Dispatchers.Main) { handleErrorInNetworkCall(e) }
            }
        }
    }

    private fun requestExcuseUsingCall(category: String) {
        val request: Call<List<ExcuseDTO>> = ApiClient.apiService.getExcuseForCategoryViaCall(category)

        request.enqueue(object: Callback<List<ExcuseDTO>> {
            override fun onResponse(call: Call<List<ExcuseDTO>>, response: Response<List<ExcuseDTO>>) {
                if(response.body() != null && response.body()!!.isNotEmpty()) {
                    displayExcuse(response.body()!![0])

                    //Adding excuses to database
                    lifecycleScope.launch {
                        excuseDao.addExcusesList(response.body()!!.map { ExcuseMapper().mapToEntity(it) })
                    }
                }
            }

            override fun onFailure(call: Call<List<ExcuseDTO>>, t: Throwable) {
                handleErrorInNetworkCall(t)
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

    fun displayExcuse(excuseDTO: ExcuseDTO) {
        displayExcuse(ExcuseMapper().mapToEntity(excuseDTO))
    }

    fun displayExcuse(excuse: Excuse) {
        viewBinding.excuseText.text = excuse.excuse
        dismissLoading()
    }

    @SuppressLint("SetTextI18n")
    fun handleErrorInNetworkCall(t: Throwable) {
        lifecycleScope.launch {
            val excuse = excuseDao.getRandomExcuse(getCategoryForId(viewBinding.categoryRadio.checkedRadioButtonId))
            if(excuse == null) {
                withContext(Dispatchers.Main) {
                    viewBinding.excuseText.text = "Error!!"
                    Toast.makeText(this@MainActivity, t.message ?: "Oops!! Something went wrong", Toast.LENGTH_SHORT).show()
                    dismissLoading()
                }
            } else {
                withContext(Dispatchers.Main) { displayExcuse(excuse) }
            }
        }

    }
}