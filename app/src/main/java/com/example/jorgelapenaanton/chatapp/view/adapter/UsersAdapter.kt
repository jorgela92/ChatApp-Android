package com.example.jorgelapenaanton.chatapp.view.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.jorgelapenaanton.chatapp.R
import com.example.jorgelapenaanton.chatapp.filesuport.ChatUtility
import com.example.jorgelapenaanton.chatapp.view.fragment.UserClickCallbacks
import com.example.jorgelapenaanton.chatapp.viewmodel.storage.StorageViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.cell_user.view.*

class UsersAdapter (
    private var items: List<Any>,
    private val context: Context?,
    private val storageViewModel: StorageViewModel,
    private val userClickCallbacks: UserClickCallbacks)
    : RecyclerView.Adapter<UsersViewHolder>() {

    private val disposables = CompositeDisposable()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): UsersViewHolder {
        return UsersViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_user, p0, false))
    }

    override fun onBindViewHolder(p0: UsersViewHolder, p1: Int) {
        p0.userClickCallbacks = userClickCallbacks
        p0.bind(user = items[p1], storageViewModel = storageViewModel, disposables = disposables, context = context)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(items: List<Any>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposables.clear()
    }
}

class UsersViewHolder (view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

    private val viewCell = view
    private val textUser: AppCompatTextView = view.text_user
    private val imageUser: AppCompatImageView = view.image_user
    internal lateinit var userClickCallbacks: UserClickCallbacks

    fun bind(user: Any, storageViewModel: StorageViewModel, disposables: CompositeDisposable, context: Context?) {
        textUser.text = ChatUtility.trimUser(user.toString())
        disposables.add(
            storageViewModel.getImage(null, ChatUtility.trimUser(user.toString()).toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != null) {
                        imageUser.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
                    } else {
                        imageUser.setImageDrawable(context?.getDrawable(R.drawable.profile))
                    }
                },{
                    imageUser.setImageDrawable(context?.getDrawable(R.drawable.profile))
                })
        )
        viewCell.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        userClickCallbacks.onUserClick(userSelected = v?.text_user?.text as String)
    }
}