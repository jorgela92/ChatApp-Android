package com.example.jorgelapenaanton.chatapp.view.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.chat.model.Message
import com.example.jorgelapenaanton.chatapp.R
import com.example.jorgelapenaanton.chatapp.filesuport.ChatUtility
import com.example.jorgelapenaanton.chatapp.viewmodel.storage.StorageViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.cell_message_received.view.*
import kotlinx.android.synthetic.main.cell_message_sent.view.*

class ChatAdapter(
    private var items: Array<Message>,
    private val context: Context?,
    private val userSelected: String?,
    private val storageViewModel: StorageViewModel)
    : RecyclerView.Adapter<ChatViewHolder>() {

    private val disposables = CompositeDisposable()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ChatViewHolder {
        var chatViewHolder = ChatViewHolder(View(context), false)
        when (p1) {
            0 -> chatViewHolder = ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_message_received, p0, false), true)
            1 -> chatViewHolder = ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.cell_message_sent, p0, false), false)
        }
        return chatViewHolder
    }

    override fun onBindViewHolder(p0: ChatViewHolder, p1: Int) {
        p0.bind(position= p1, items = items, userSelected = userSelected, storageViewModel = storageViewModel, disposables = disposables, context = context)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].user == userSelected) { 0 } else { 1 }
    }

    fun updateData(items: Array<Message>) {
        this.items = items.copyOfRange(1, items.size)
        notifyDataSetChanged()
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        disposables.clear()
    }
}

class ChatViewHolder (view: View, received: Boolean) : RecyclerView.ViewHolder(view) {

    private var textUser: AppCompatTextView?
    private var textHour: AppCompatTextView?
    private var textMessage: AppCompatTextView?
    private var imageUser: AppCompatImageView?
    private var imageMessage: AppCompatImageView?
    private var cardView: CardView?

    init {
        if (received) {
            textUser = view.text_user_received
            textHour = view.text_hour_received
            textMessage = view.text_message_received
            imageUser = view.image_user_received
            imageMessage = view.image_message_received
            cardView = view.cardView_received
        } else {
            textUser = view.text_user_sent
            textHour = view.text_hour_sent
            textMessage = view.text_message_sent
            imageUser = view.image_user_sent
            imageMessage = view.image_message_sent
            cardView = view.cardView_sent
        }
    }

    fun bind(position: Int, items: Array<Message>, userSelected: String?, storageViewModel: StorageViewModel, disposables: CompositeDisposable, context: Context?) {
        textUser?.text = ChatUtility.trimUser(items[position].user)
        textHour?.text = ChatUtility.getDateTime(items[position].hour)

        if (items[position].message.contains("image-")) {
            disposables.add(
                storageViewModel.getImage(null, ChatUtility.trimUser(items[position].user).toString())
                    .subscribe({
                        if (it != null) {
                            textMessage?.visibility = View.GONE
                            imageMessage?.visibility = View.VISIBLE
                            imageMessage?.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
                        } else {
                            imageMessage?.setImageDrawable(context?.getDrawable(R.drawable.camera))
                        }
                    },{
                        imageMessage?.setImageDrawable(context?.getDrawable(R.drawable.camera))
                    })
            )
        } else {
            textMessage?.visibility = View.VISIBLE
            imageMessage?.visibility = View.GONE
            textMessage?.text = items[position].message
        }
        if (position - 1 >= 0) {
            if (items[position].user != items[position - 1].user) {
                getImageUser(position, items, userSelected, storageViewModel, disposables, context)
                imageUser?.visibility = View.VISIBLE
                textUser?.visibility = View.VISIBLE
            } else {
                imageUser?.visibility = View.INVISIBLE
                textUser?.visibility = View.INVISIBLE
            }
        } else {
            getImageUser(position, items, userSelected, storageViewModel, disposables, context)
            imageUser?.visibility = View.VISIBLE
            textUser?.visibility = View.VISIBLE
        }
    }

    private fun getImageUser(position: Int, items: Array<Message>, userSelected: String?, storageViewModel: StorageViewModel, disposables: CompositeDisposable, context: Context?) {
        disposables.add(
            storageViewModel.getImage(null, ChatUtility.trimUser(items[position].user).toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != null) {
                        imageUser?.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
                    } else {
                        if (items[position].user == userSelected){
                            imageUser?.setImageDrawable(context?.getDrawable(R.drawable.speech))
                        } else {
                            imageUser?.setImageDrawable(context?.getDrawable(R.drawable.speechblack))
                        }
                    }
                },{
                    if (items[position].user == userSelected){
                        imageUser?.setImageDrawable(context?.getDrawable(R.drawable.speech))
                    } else {
                        imageUser?.setImageDrawable(context?.getDrawable(R.drawable.speechblack))
                    }
                })
        )
    }
}