package com.example.jorgelapenaanton.chatapp.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.jorgelapenaanton.chatapp.R
import com.example.jorgelapenaanton.chatapp.application.injector
import com.example.jorgelapenaanton.chatapp.viewmodel.auth.AuthViewModel
import com.example.jorgelapenaanton.chatapp.viewmodel.auth.AuthViewModelFactory
import com.example.jorgelapenaanton.chatapp.viewmodel.chat.ChatViewModel
import com.example.jorgelapenaanton.chatapp.viewmodel.chat.ChatViewModelFactory
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_login.*

interface LoginFragmentDelegate {
    fun login(isChecked: Boolean, email: String)
    fun isUserRegister(): String?
}

class LoginFragment: Fragment() {

    private val authViewModelFactory: AuthViewModelFactory = injector.authViewModelFactory()
    private val chatViewModelFactory: ChatViewModelFactory = injector.chatViewModelFactory()
    private lateinit var authViewModel: AuthViewModel
    private lateinit var chatViewModel: ChatViewModel
    private val disposables = CompositeDisposable()
    private lateinit var dialog: AlertDialog

    private var loginFragmentDelegate: LoginFragmentDelegate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProviders.of(requireNotNull(activity), authViewModelFactory).get(AuthViewModel::class.java)
        chatViewModel = ViewModelProviders.of(requireNotNull(activity), chatViewModelFactory).get(ChatViewModel::class.java)
        dialog = SpotsDialog.Builder().setContext(context).build()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            loginFragmentDelegate = context as LoginFragmentDelegate
        } catch (e: ClassCastException) {
            throw ClassCastException("Activity must implement MainActivityFragmentsListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureUI()
    }

    private fun configureUI() {
        if ("" == loginFragmentDelegate?.isUserRegister()) {
            edit_text_email.setText("")
            switch_login.isChecked = false
        } else {
            edit_text_email.setText(loginFragmentDelegate?.isUserRegister())
            switch_login.isChecked = true
        }

        button_login.setOnClickListener {
            actionLoginUser()
        }

        button_register.setOnClickListener {
            actionSignUpUser()
        }
    }

    private fun actionLoginUser() {
        dialog.show()
        disableButtons()
        disposables.add(
            authViewModel.loginUser(username = edit_text_email.text.toString(), password = edit_text_password.text.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    dialog.dismiss()
                    loginOK()
                },{
                    dialog.dismiss()
                    loginKO()
                })
        )
    }

    private fun actionSignUpUser() {
        dialog.show()
        disableButtons()
        disposables.add(
            authViewModel.signUpUser(username = edit_text_email.text.toString(), password = edit_text_password.text.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    chatViewModel.setUsers(edit_text_email.text.toString())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            dialog.dismiss()
                            registerOK()
                        }, {
                            dialog.dismiss()
                            registerKO()
                        })
                },{
                    dialog.dismiss()
                    registerKO()
                })
        )
    }

    private fun registerKO() {
        edit_text_email.setText("")
        edit_text_password.setText("")
        enableButtons()
    }

    private fun registerOK() {
        textView.setText(R.string.login)
        edit_text_password.setText("")
        enableButtons()
    }

    private fun loginKO() {
        textView.setText(R.string.registrarse)
        edit_text_email.setText("")
        edit_text_password.setText("")
        enableButtons()
    }

    private fun loginOK() {
        loginFragmentDelegate?.login(isChecked = switch_login.isChecked, email = edit_text_email.text.toString())
        enableButtons()
    }

    private fun enableButtons() {
        button_login.isEnabled = true
        button_register.isEnabled = true
    }

    private fun disableButtons() {
        button_login.isEnabled = false
        button_register.isEnabled = false
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }
}
