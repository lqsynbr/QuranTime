package edu.luqsiyana.qurantime

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import edu.luqsiyana.qurantime.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Slide In Animation
        val slideInAnim = AnimationUtils.loadAnimation(this, R.anim.slide_in)
        binding.animLoading.startAnimation(slideInAnim)

        // Jalankan coroutine untuk delay + cek user
        lifecycleScope.launch {
            delay(2500) // animasi 2,5 detik
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null) {
                // User sudah login → langsung ke MainActivity
                startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            } else {
                // User belum login → ke LoginActivity
                startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
            }
            finish() // tutup SplashScreen
        }
    }
}