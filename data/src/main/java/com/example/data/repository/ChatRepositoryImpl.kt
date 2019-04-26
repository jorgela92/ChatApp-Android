package com.example.data.repository

import android.annotation.SuppressLint
import com.example.domain.chat.model.Users
import com.example.domain.chat.model.Message
import com.example.domain.chat.repository.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

@Singleton
class ChatRepositoryImpl @Inject constructor(private val db: FirebaseFirestore) : ChatRepository {

    override fun updateMessage(identifier: String): Observable<Array<Message>> {
        return Observable.create(ObservableOnSubscribe<Array<Message>> {
            db.collection("conversations").document(identifier).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    it.onError(firebaseFirestoreException)
                    return@addSnapshotListener
                }
                if (documentSnapshot != null) {
                    if (documentSnapshot.data != null) {
                        val messagesArrayList: ArrayList<*>? = documentSnapshot.data!!["messages"] as ArrayList<*>
                        try {
                            if (messagesArrayList != null) {
                                val messagesArray: Array<Message> = Array(messagesArrayList.size) { Message() }
                                for (index in 0..messagesArrayList.lastIndex) {
                                    val hasMap: HashMap<*,*> = messagesArrayList[index] as HashMap<*, *>
                                    messagesArray[index] = Message(message = hasMap["message"] as String, user = hasMap["user"] as String, hour = hasMap["hour"] as String)
                                }
                                it.onNext(messagesArray)
                            }
                        } catch (error: IndexOutOfBoundsException){
                            it.onError(error)
                        }
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
    }

    @SuppressLint("SimpleDateFormat")
    override fun setMessage(identifier: String, user: String, message: String): Completable {
        return Completable.create { emitter ->
            getMessage(identifier = identifier, completion = {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                setArrayMessage(identifier = identifier, arrayMessages = it.plus(Message(message = message, user = user, hour = sdf.format(Date()))), completion = {
                    emitter.onComplete()
                }, failure = { error ->
                    emitter.onError(error)
                })
            }, failure = {
                emitter.onError(it)
            })
        }.subscribeOn(Schedulers.io())
    }

    private fun setArrayMessage(identifier: String, arrayMessages: Array<Message>, completion: () -> Unit, failure: ((Exception) -> Unit)) {
        db.collection("conversations").document(identifier)
            .set(mapOf("messages" to arrayMessages.toList()))
            .addOnSuccessListener { completion() }
            .addOnFailureListener { failure(it) }
    }

    private fun getMessage(identifier: String, completion: (Array<Message>) -> Unit, failure: ((Exception) -> Unit)) {
        db.collection("conversations").document(identifier)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    if (documentSnapshot.data != null) {
                        val messagesArrayList: ArrayList<*>? = documentSnapshot.data!!["messages"] as ArrayList<*>
                        try {
                            if (messagesArrayList != null) {
                                val messagesArray: Array<Message> = Array(messagesArrayList.size) { Message() }
                                for (index in 0..messagesArrayList.lastIndex) {
                                    val hasMap: HashMap<*,*> = messagesArrayList[index] as HashMap<*, *>
                                    messagesArray[index] = Message(message = hasMap["message"] as String, user = hasMap["user"] as String, hour = hasMap["hour"] as String)
                                }
                                completion(messagesArray)
                            }
                        } catch (error: IndexOutOfBoundsException){
                            failure(error)
                        }
                    }
                }
            }.addOnFailureListener { failure(it) }
    }

    override fun setConversations(identifier: String): Completable {
        return Completable.create {
            db.collection("conversations").document(identifier).set(hashMapOf("messages" to  Array(1) { Message() }))
        }.subscribeOn(Schedulers.io())
    }

    override fun getConversations(): Observable<Array<String>> {
        return Observable.create(ObservableOnSubscribe<Array<String>> {
            db.collection("conversations").addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    it.onError(firebaseFirestoreException)
                    return@addSnapshotListener
                }
                if (documentSnapshot != null) {
                    val conversationsArray: Array<String> = Array(documentSnapshot.documents.size) { "" }
                    for (conversationElement in documentSnapshot.documents) {
                        conversationsArray[documentSnapshot.documents.indexOf(conversationElement)] = conversationElement.id
                    }
                    it.onNext(conversationsArray)
                }
            }
        }).subscribeOn(Schedulers.io())
    }

    override fun setUsers(users: String): Completable {
        return Completable.create { emitter ->
            val arrayU: Array<String> = Array(1) { users }
            getUsers(completion = {
                setArrayUser(arrayU.plus(it), completion = {
                    emitter.onComplete()
                }, failure = { error ->
                    emitter.onError(error)
                })
            }, failure = {
                setArrayUser(arrayU, completion = {
                    emitter.onComplete()
                }, failure = {
                    emitter.onError(it)
                })
            })
        }.subscribeOn(Schedulers.io())
    }

    override fun updatetUsers(): Observable<Users> {
        return Observable.create(ObservableOnSubscribe<Users> {
            db.collection("chatusers").document("users").addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null) {
                    it.onError(firebaseFirestoreException)
                    return@addSnapshotListener
                }
                if (documentSnapshot != null) {
                    if (documentSnapshot.data != null) {
                        documentSnapshot.data!!["users"]
                        it.onNext(Users(users = documentSnapshot.data!!["users"] as ArrayList<*>))
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
    }

    private fun setArrayUser(userArray: Array<String>, completion: () -> Unit, failure: ((Exception) -> Unit)) {
        db.collection("chatusers").document("users")
            .set(userArray)
            .addOnSuccessListener { completion() }
            .addOnFailureListener { failure(it) }
    }

    private fun getUsers(completion: (Array<String>) -> Unit, failure: ((Exception) -> Unit)) {
        db.collection("chatusers").document("users")
            .get()
            .addOnSuccessListener {
                val users: Array<String>? = it.toObject(Array<String>::class.java)
                if (users != null) {
                    completion(users)
                }
            }
            .addOnFailureListener { failure(it) }
    }
}