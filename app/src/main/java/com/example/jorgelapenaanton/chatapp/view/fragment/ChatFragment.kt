package com.example.jorgelapenaanton.chatapp.view.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.app.AlertDialog
import android.content.Context
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.jorgelapenaanton.chatapp.R
import com.example.jorgelapenaanton.chatapp.application.injector
import com.example.jorgelapenaanton.chatapp.filesuport.ChatConstans
import com.example.jorgelapenaanton.chatapp.filesuport.ChatUtility
import com.example.jorgelapenaanton.chatapp.view.adapter.ChatAdapter
import com.example.jorgelapenaanton.chatapp.viewmodel.auth.AuthViewModel
import com.example.jorgelapenaanton.chatapp.viewmodel.auth.AuthViewModelFactory
import com.example.jorgelapenaanton.chatapp.viewmodel.chat.ChatViewModel
import com.example.jorgelapenaanton.chatapp.viewmodel.chat.ChatViewModelFactory
import com.example.jorgelapenaanton.chatapp.viewmodel.storage.StorageViewModel
import com.example.jorgelapenaanton.chatapp.viewmodel.storage.StorageViewModelFactory
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_chat.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class ChatFragment: Fragment() {

    private val authViewModelFactory: AuthViewModelFactory = injector.authViewModelFactory()
    private val chatViewModelFactory: ChatViewModelFactory = injector.chatViewModelFactory()
    private val storageViewModelFactory: StorageViewModelFactory = injector.storageViewModelFactory()
    private lateinit var authViewModel: AuthViewModel
    private lateinit var chatViewModel: ChatViewModel
    private lateinit var storagetViewModel: StorageViewModel
    private val disposables = CompositeDisposable()
    private lateinit var dialog: AlertDialog
    private var identifier = ""

    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProviders.of(requireNotNull(activity), authViewModelFactory).get(AuthViewModel::class.java)
        chatViewModel = ViewModelProviders.of(requireNotNull(activity), chatViewModelFactory).get(ChatViewModel::class.java)
        storagetViewModel = ViewModelProviders.of(requireNotNull(activity), storageViewModelFactory).get(StorageViewModel::class.java)
        dialog = SpotsDialog.Builder().setContext(context).build()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureUI(arguments?.getString(ChatConstans.BUNDLE_KEY_USER, ""))
    }

    private fun configureUI(usersSelected: String?){
        recyclerView_chat.layoutManager = LinearLayoutManager(context)
        adapter = ChatAdapter(arrayOf(), context, usersSelected, storagetViewModel)
        recyclerView_chat.adapter = adapter
        getData(usersSelected = usersSelected)
        sendMessage.setOnClickListener { actionSendMessage() }
        sendImage.setOnClickListener { actionSendImage() }
    }

    private fun getData(usersSelected: String?) {
        dialog.show()
        disposables.add(
            chatViewModel.getConversations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ conversations ->
                    for (conversation in conversations){
                        if (conversation == chatViewModel.getStringConversationIdentifier(
                                ChatUtility.trimUser(authViewModel.emailUser())!!,
                                ChatUtility.trimUser(usersSelected)!!,
                                false)) {
                            identifier = chatViewModel.getStringConversationIdentifier(
                                ChatUtility.trimUser(authViewModel.emailUser())!!,
                                ChatUtility.trimUser(usersSelected)!!,
                                false)
                        } else if (conversation == chatViewModel.getStringConversationIdentifier(
                                ChatUtility.trimUser(authViewModel.emailUser())!!,
                                ChatUtility.trimUser(usersSelected)!!,
                                true)) {
                            identifier = chatViewModel.getStringConversationIdentifier(
                                ChatUtility.trimUser(authViewModel.emailUser())!!,
                                ChatUtility.trimUser(usersSelected)!!,
                                true)
                        }
                    }
                    if (identifier != ""){
                        disposables.add(
                            chatViewModel.upadteMessages(identifier)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe ({
                                adapter.updateData(it)
                                recyclerView_chat.post {
                                    recyclerView_chat.scrollToPosition(adapter.itemCount - 1)
                                }
                                dialog.dismiss()
                            }, {
                                //ERROR
                                dialog.dismiss()
                            })
                        )
                    } else {
                        dialog.dismiss()
                    }
                },{
                    dialog.dismiss()
                })
        )
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        messageText.isFocusable = false
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun actionSendMessage() {
        hideKeyboard()
        if ("" != messageText.text.toString()) {
            dialog.show()
            disposables.add(
                chatViewModel.setMessage(identifier = identifier, user = ChatUtility.trimUser(authViewModel.emailUser())!!, message = messageText.text.toString())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        messageText.setText("")
                        messageText.isEnabled = false
                        dialog.dismiss()
                    },{
                        messageText.setText("")
                        messageText.isEnabled = false
                        dialog.dismiss()
                    })
            )
        }
    }

    private fun actionSendImage() {
        hideKeyboard()
        showPictureDialog()
    }

    private fun showPictureDialog() {
        val context = activity
        if (context != null) {
            val pictureDialog = AlertDialog.Builder(context)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
            pictureDialog.setItems(pictureDialogItems) { _, which ->
                when (which) {
                    0 -> choosePhotoFromGallary()
                    1 -> takePhotoFromCamera()
                }
            }
            pictureDialog.show()
        }
    }

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, ChatConstans.GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, ChatConstans.CAMERA)
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ChatConstans.GALLERY) {
            if (data != null) {
                val contentURI = data.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, contentURI)
                    saveImage(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        } else if (requestCode == ChatConstans.CAMERA) {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap
                saveImage(thumbnail)
            }
        }
    }

    private fun saveImage(image: Bitmap) {
        ChatUtility.resize(image)
        val stream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 80, stream)
        val imageArray = stream.toByteArray()
        val userEmail = ChatUtility.trimUser(authViewModel.emailUser())
        if (userEmail != null) {
            val nameImage = "image-"+ChatUtility.randomStringNameImage()
            dialog.show()
            disposables.add(
                storagetViewModel.setImage(identifier, nameImage, imageArray)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        disposables.add(
                            chatViewModel.setMessage(identifier = identifier, user = ChatUtility.trimUser(authViewModel.emailUser())!!, message = nameImage)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    messageText.setText("")
                                    messageText.isEnabled = false
                                    dialog.dismiss()
                                },{
                                    messageText.setText("")
                                    messageText.isEnabled = false
                                    dialog.dismiss()
                                })
                        )
                    }, {
                        dialog.dismiss()
                    })
            )
        }
    }

    override fun onStop() {
        super.onStop()
        dialog.dismiss()
        recyclerView_chat.adapter = null
        disposables.clear()
    }
}