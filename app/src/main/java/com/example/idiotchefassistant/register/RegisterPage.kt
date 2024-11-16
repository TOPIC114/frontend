package com.example.idiotchefassistant.register

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.idiotchefassistant.databinding.ActivityRegisterPageBinding
import com.example.idiotchefassistant.login.afterTextChanged

class RegisterPage : AppCompatActivity() {
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var binding: ActivityRegisterPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.username
        val email = binding.email
        val password = binding.password
        val passwordRetype = binding.passwordRetype
        val register = binding.register
        val loading = binding.loading

        registerViewModel = ViewModelProvider(
            this,
            RegisterFactory()
        )[RegisterViewModel::class.java]

        registerViewModel.registerFormState.observe(this@RegisterPage, Observer {
            val registerState = it ?: return@Observer
            register.isEnabled = registerState.isDataValid
            if (registerState.emailError != null) {
                email.error = getString(registerState.emailError)
            }
            if (registerState.passwordError != null) {
                password.error = getString(registerState.passwordError)
            }
            if (registerState.passwordRtypeError != null) {
                passwordRetype.error = getString(registerState.passwordRtypeError)
            }
        })

        registerViewModel.registerResult.observe(this@RegisterPage, Observer {
            val registerResult = it ?: return@Observer
            loading.visibility = View.GONE
            if (registerResult.isEmpty()) {
                Toast.makeText(applicationContext, registerResult, Toast.LENGTH_SHORT).show()
            }
            if (registerResult.isNotEmpty()) {
                Toast.makeText(applicationContext, registerResult, Toast.LENGTH_SHORT).show()
            }
            setResult(Activity.RESULT_OK)
            finish()
        })

        username.afterTextChanged {
            val infoData = RegisterData(
                username.text.toString(),
                email.text.toString(),
                password.text.toString(),
                passwordRetype.text.toString()
            )
            registerViewModel.registerDataChange(infoData)
        }

        email.afterTextChanged {
            val infoData = RegisterData(
                username.text.toString(),
                email.text.toString(),
                password.text.toString(),
                passwordRetype.text.toString()
            )
            registerViewModel.registerDataChange(infoData)
        }

        password.afterTextChanged {
            val infoData = RegisterData(
                username.text.toString(),
                email.text.toString(),
                password.text.toString(),
                passwordRetype.text.toString()
            )
            registerViewModel.registerDataChange(infoData)
        }

        passwordRetype.apply {
            val infoData = RegisterData(
                username.text.toString(),
                email.text.toString(),
                password.text.toString(),
                passwordRetype.text.toString()
            )
            afterTextChanged {
                registerViewModel.registerDataChange(infoData)
            }
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        registerViewModel.register(infoData)
                }
                false
            }
            register.setOnClickListener {
                loading.visibility = View.VISIBLE
                registerViewModel.register(infoData)
            }
        }
    }
}

