package com.example.jorgelapenaanton.chatapp.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jorgelapenaanton.chatapp.R
import com.example.jorgelapenaanton.chatapp.application.injector
import com.example.jorgelapenaanton.chatapp.filesuport.ChatUtility
import com.example.jorgelapenaanton.chatapp.view.adapter.UsersAdapter
import com.example.jorgelapenaanton.chatapp.viewmodel.auth.AuthViewModel
import com.example.jorgelapenaanton.chatapp.viewmodel.auth.AuthViewModelFactory
import com.example.jorgelapenaanton.chatapp.viewmodel.chat.ChatViewModel
import com.example.jorgelapenaanton.chatapp.viewmodel.chat.ChatViewModelFactory
import com.example.jorgelapenaanton.chatapp.viewmodel.storage.StorageViewModel
import com.example.jorgelapenaanton.chatapp.viewmodel.storage.StorageViewModelFactory
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_users.*

interface UserFragmentDelegate {
    fun onUserClick(userSelected: String)
}

interface UserClickCallbacks {
    fun onUserClick(userSelected: String)
}

class UsersFragment: Fragment(), UserClickCallbacks {

    private val authViewModelFactory: AuthViewModelFactory = injector.authViewModelFactory()
    private val chatViewModelFactory: ChatViewModelFactory = injector.chatViewModelFactory()
    private val storageViewModelFactory: StorageViewModelFactory = injector.storageViewModelFactory()
    private lateinit var authViewModel: AuthViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var storagetViewModel: StorageViewModel
    private val disposables = CompositeDisposable()
    private lateinit var dialog: AlertDialog
    private var isData = true

    private var userFragmentDelegate: UserFragmentDelegate? = null

    private lateinit var adapter: UsersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProviders.of(requireNotNull(activity), authViewModelFactory).get(AuthViewModel::class.java)
        chatViewModel = ViewModelProviders.of(requireNotNull(activity), chatViewModelFactory).get(ChatViewModel::class.java)
        storagetViewModel = ViewModelProviders.of(requireNotNull(activity), storageViewModelFactory).get(StorageViewModel::class.java)
        dialog = SpotsDialog.Builder().setContext(context).build()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            userFragmentDelegate = context as UserFragmentDelegate
        } catch (e: ClassCastException) {
            throw ClassCastException("Activity must implement MainActivityFragmentsListener")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureUI()
    }

    private fun configureUI(){
        recycler_view_users.layoutManager = LinearLayoutManager(context)
        adapter = UsersAdapter(listOf(), context, storagetViewModel, this)
        recycler_view_users.adapter = adapter
        if (isData) {
            fechData()
            isData = false
        }
    }

    private fun fechData() {
        dialog.show()
        disposables.add(
            chatViewModel.getUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { users ->
                        adapter.updateData(users.users)
                        disposables.add(
                            chatViewModel.getConversations()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                for (user in users.users){
                                    var setNewConversation = false
                                    if (!it.contains(
                                            chatViewModel.getStringConversationIdentifier(ChatUtility.trimUser(user as String)!!,
                                                ChatUtility.trimUser(authViewModel.emailUser())!!, true))
                                        && !it.contains(
                                            chatViewModel.getStringConversationIdentifier(
                                                ChatUtility.trimUser(user)!!,
                                                ChatUtility.trimUser(authViewModel.emailUser())!!, false))) {
                                        setNewConversation = true
                                    }

                                    if (setNewConversation) {
                                        disposables.add(
                                            chatViewModel.setConversation(chatViewModel.getStringConversationIdentifier(
                                                ChatUtility.trimUser(user)!!,
                                                ChatUtility.trimUser(authViewModel.emailUser())!!, false))
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe({
                                                    dialog.dismiss()
                                                }, {
                                                    dialog.dismiss()
                                                })
                                        )
                                    }
                                }
                                dialog.dismiss()
                            },{
                                disposables.add(
                                    chatViewModel.setConversation(chatViewModel.getStringConversationIdentifier(
                                        ChatUtility.trimUser((authViewModel.emailUser()))!!,
                                        ChatUtility.trimUser(authViewModel.emailUser())!!, false))
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            dialog.dismiss()
                                        }, {
                                            dialog.dismiss()
                                        })
                                )
                            })
                        )
                    }, {
                        dialog.dismiss()
                    }
                )
        )
    }

    override fun onUserClick(userSelected: String) {
        userFragmentDelegate?.onUserClick(userSelected)
    }

    override fun onStop() {
        super.onStop()
        recycler_view_users.adapter = null
        disposables.clear()
    }
}