package com.example.jorgelapenaanton.chatapp.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.example.jorgelapenaanton.chatapp.R
import com.example.jorgelapenaanton.chatapp.application.injector
import com.example.jorgelapenaanton.chatapp.filesuport.ChatConstans
import com.example.jorgelapenaanton.chatapp.filesuport.ChatUtility
import com.example.jorgelapenaanton.chatapp.view.fragment.*
import com.example.jorgelapenaanton.chatapp.viewmodel.auth.AuthViewModel
import com.example.jorgelapenaanton.chatapp.viewmodel.storage.StorageViewModel
import com.google.android.material.appbar.AppBarLayout
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_base.*
import java.io.ByteArrayOutputStream
import java.io.IOException

enum class TypeFragment {
    LOGINFRAGMENT, USERSFRAGMENT, CHATFRAGMENT
}

class BaseActivity: AppCompatActivity(), LoginFragmentDelegate, UserFragmentDelegate {

    private val authViewModelFactory = injector.authViewModelFactory()
    private val storageViewModelFactory = injector.storageViewModelFactory()
    private lateinit var authViewModel: AuthViewModel
    private lateinit var storagetViewModel: StorageViewModel
    private val disposables = CompositeDisposable()
    private lateinit var dialog: android.app.AlertDialog

    private val managerFragment: FragmentManager = supportFragmentManager
    private var typeFragment: TypeFragment = TypeFragment.LOGINFRAGMENT

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        storagetViewModel = ViewModelProviders.of(requireNotNull(this), storageViewModelFactory).get(StorageViewModel::class.java)
        authViewModel = ViewModelProviders.of(requireNotNull(this), authViewModelFactory).get(AuthViewModel::class.java)
        configureActivity()
    }

    private fun configureActivity(){
        setContentView(R.layout.activity_base)
        setSupportActionBar(mainToolbar)
        dialog = SpotsDialog.Builder().setContext(this).build()
        loadFragment(TypeFragment.LOGINFRAGMENT, "")
    }

    private fun loadFragment(typeFragment: TypeFragment, userSelected: String){
        when (typeFragment) {
            TypeFragment.LOGINFRAGMENT -> loadLoginFragment()
            TypeFragment.USERSFRAGMENT -> loadUsersFragment()
            TypeFragment.CHATFRAGMENT -> loadChatFragment(userSelected)
        }
    }

    private fun loadLoginFragment(){
        if (typeFragment == TypeFragment.USERSFRAGMENT) {
            managerFragment.popBackStack()
        }
        typeFragment = TypeFragment.LOGINFRAGMENT
        val transaction = managerFragment.beginTransaction()
        val loginFragment = LoginFragment()
        transaction.replace(R.id.fragment_base, loginFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        configureToolBarLoginFragment()
    }

    private fun loadUsersFragment(){
        if (typeFragment == TypeFragment.CHATFRAGMENT) {
            managerFragment.popBackStack()
        }
        typeFragment = TypeFragment.USERSFRAGMENT
        val transaction = managerFragment.beginTransaction()
        val usersFragment = UsersFragment()
        transaction.replace(R.id.fragment_base, usersFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        configureToolBarUsersFragment()
    }

    private fun loadChatFragment(userSelected: String){
        typeFragment = TypeFragment.CHATFRAGMENT
        val transaction = managerFragment.beginTransaction()
        val storageBundle = Bundle()
        storageBundle.putString(ChatConstans.BUNDLE_KEY_USER, userSelected)
        val chatFragment = ChatFragment()
        chatFragment.arguments = storageBundle
        transaction.replace(R.id.fragment_base, chatFragment)
        transaction.addToBackStack(null)
        transaction.commit()
        configureToolBarChatFragment()
    }

    private fun configureToolBarLoginFragment(){
        val params: AppBarLayout.LayoutParams = mainToolbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
        mainToolbar.layoutParams = params
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        button_image_camera.visibility = View.GONE
        mainToolbar.title = resources.getString(R.string.chat)
    }

    private fun configureToolBarUsersFragment(){
        mainToolbar.title = resources.getString(R.string.users)
        val params: AppBarLayout.LayoutParams = mainToolbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        mainToolbar.layoutParams = params
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        button_image_camera.visibility = View.VISIBLE
        button_image_camera.setOnClickListener{ showPictureDialog() }
    }

    private fun configureToolBarChatFragment(){
        mainToolbar.title = resources.getString(R.string.chat)
        val params: AppBarLayout.LayoutParams = mainToolbar.layoutParams as AppBarLayout.LayoutParams
        params.scrollFlags = 0
        mainToolbar.layoutParams = params
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        button_image_camera.visibility = View.GONE
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home){
            when (typeFragment) {
                TypeFragment.LOGINFRAGMENT -> {}
                TypeFragment.USERSFRAGMENT -> {
                    loadLoginFragment()
                    authViewModel.logautUser()
                }
                TypeFragment.CHATFRAGMENT -> loadUsersFragment()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("CommitPrefEdits", "ApplySharedPref")
    override fun login(isChecked: Boolean, email: String) {
        if (isChecked){
            val editing = getSharedPreferences(ChatConstans.PREFERENCES_LOGIN, Context.MODE_PRIVATE)?.edit()
            editing?.putString(ChatConstans.PREFERENCES_KEY_EMAIL, email)
            editing?.commit()
        }
        loadFragment(TypeFragment.USERSFRAGMENT, "")
    }

    override fun isUserRegister(): String? {
        return getSharedPreferences(ChatConstans.PREFERENCES_LOGIN, Context.MODE_PRIVATE)?.getString(ChatConstans.PREFERENCES_KEY_EMAIL, "")
    }

    override fun onUserClick(userSelected: String) {
       loadFragment(TypeFragment.CHATFRAGMENT, userSelected)
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
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

    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
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
                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, contentURI)
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
            dialog.show()
            disposables.add(
                storagetViewModel.setImage(null, userEmail, imageArray)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        dialog.dismiss()
                    }, {
                        dialog.dismiss()
                    })
            )
        }
    }

    override fun onBackPressed() {
        if (typeFragment != TypeFragment.LOGINFRAGMENT) { super.onBackPressed() }
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }
}