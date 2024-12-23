package com.example.idiotchefassistant.mainLayout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.idiotchefassistant.MyApp
import com.example.idiotchefassistant.UserResponse
import com.example.idiotchefassistant.databinding.FragmentInformationPageBinding
import com.example.idiotchefassistant.login.LoginActivity
import com.example.idiotchefassistant.userDataService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InfoPage : Fragment() {
    private var _binding: FragmentInformationPageBinding? = null
    private val binding get() = _binding!!
    private val _userData = MutableLiveData<UserResponse>()
    private var userData: LiveData<UserResponse> = _userData


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInformationPageBinding.inflate(inflater, container, false)
        val view = binding.root

        val info = binding.userData
        val remind = binding.remindBlock
        val email = binding.email
        val name = binding.name
        val login = binding.login

        login.setOnClickListener{
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
        }
        MyApp.isLogin.observe(viewLifecycleOwner) { isLoggedIn ->
            info.isVisible = isLoggedIn
            if (isLoggedIn)
                me()
            remind.isVisible = !isLoggedIn
        }

        userData.observe(viewLifecycleOwner){ data ->
            email.text = data.email
            name.text = data.username
        }

        return view
    }

    private fun me(){
        userDataService.me().enqueue(object :
            Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                if (response.isSuccessful) {
                    _userData.value = response.body()
                    Log.i("userHistory","Success:${response.body().toString()}")
                } else {
                    Log.i("userHistory","Failed:${response.code()} ${response.message()}")
                    _userData.value = UserResponse("", 0, "")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("userHistory", "API call failed: ${t.message}")
                _userData.value = UserResponse("", 0, "")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}