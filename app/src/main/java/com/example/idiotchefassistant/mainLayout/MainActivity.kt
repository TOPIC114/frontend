package com.example.idiotchefassistant.mainLayout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.idiotchefassistant.AuthTokenManager
import com.example.idiotchefassistant.MyApp
import com.example.idiotchefassistant.R
import com.example.idiotchefassistant.search.SearchPage
import com.example.idiotchefassistant.databinding.ActivityMainBinding
import com.example.idiotchefassistant.login.LoginActivity
import com.example.idiotchefassistant.login.LoginViewModel
import com.example.idiotchefassistant.login.LoginViewModelFactory
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(applicationContext)
        )[LoginViewModel::class.java]

        if (AuthTokenManager.getAuthTokenSync() != null)
            MyApp.setLogin(true)

        drawerLayout = binding.drawerLayout
        val navigationView = binding.navView
        val menu = navigationView.menu
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        MyApp.isLogin.observe(this) { isLoggedIn ->
            menu.findItem(R.id.nav_login).isVisible = !isLoggedIn
            menu.findItem(R.id.nav_logout).isVisible = isLoggedIn
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomePage()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    // 可以直接調用此方法來關閉當前 Activity 或 Fragment
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_header, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search_btn -> {
                val intent = Intent(this, SearchPage::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomePage()).commit()

            R.id.nav_history -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HistoryPage()).commit()

            R.id.nav_about -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AboutUsPage()).commit()

            R.id.nav_info -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, InfoPage()).commit()

            R.id.nav_login -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_logout -> {
                logout()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        AlertDialog.Builder(this)
            .setTitle("登出確認")
            .setMessage("你確定要登出嗎?")
            .setPositiveButton("Yes") { _, _ ->
                loginViewModel.logout()
                loginViewModel.loginResult.observe(this) { result ->
                    Toast.makeText(applicationContext, result, Toast.LENGTH_SHORT).show()
                    if (result.contains("登出")) {
                        setResult(Activity.RESULT_OK)
                    }
                }
            }
            .setNegativeButton("No", null)
            .show()
    }
}
