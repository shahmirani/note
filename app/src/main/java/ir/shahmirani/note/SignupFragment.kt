package ir.shahmirani.note

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import ir.shahmirani.note.databinding.FragmentSignupBinding


class SignupFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var binding: FragmentSignupBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        registerEvents()
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
    }

    private fun registerEvents() {
        binding.authTextView.setOnClickListener {
            navController.navigate(R.id.action_signupFragment_to_signinFragment)
        }




        binding.nextBtn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val pass = binding.pass.text.toString().trim()
            val verifyPass = binding.rePass.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty() && verifyPass.isNotEmpty()) {
                if (pass == verifyPass) {
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(context, "registered successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate(R.id.action_signupFragment_to_signinFragment)
                        } else {
                            Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}