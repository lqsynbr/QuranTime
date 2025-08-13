package edu.luqsiyana.qurantime

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import edu.luqsiyana.qurantime.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    lateinit var binding : ActivityRegisterBinding
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.tvToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignup.setOnClickListener {
            val email = binding.edtEmailSignup.text.toString()
            val password = binding.edtPasswordSignup.text.toString()

            // Validasi email
            if (email.isEmpty()) {
                binding.edtEmailSignup.error = "Email Harus Diisi"
                binding.edtEmailSignup.requestFocus()
                return@setOnClickListener
            }

            // Validasi email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmailSignup.error = "Email Tidak Valid"
                binding.edtEmailSignup.requestFocus()
                return@setOnClickListener
            }

            // Validasi Password
            if (password.isEmpty()) {
                binding.edtPasswordSignup.error = "Password Harus Diisi"
                binding.edtPasswordSignup.requestFocus()
                return@setOnClickListener
            }

            // Validasi panjang password
            if (password.length < 6) {
                binding.edtPasswordSignup.error = "Password Minimal 6 Karakter"
                binding.edtPasswordSignup.requestFocus()
                return@setOnClickListener
            }

            RegisterFirebase(email,password)

        }
    }

    private fun RegisterFirebase(email: String, password: String) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(this) {
            if (it .isSuccessful) {
                auth.signOut()
                Toast.makeText(this, "Sign Up Berhasil", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}