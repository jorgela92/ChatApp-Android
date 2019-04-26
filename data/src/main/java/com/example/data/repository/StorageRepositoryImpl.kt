package com.example.data.repository

import com.example.domain.storage.repository.StorageRepository
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepositoryImpl @Inject constructor(private val db: FirebaseStorage) : StorageRepository {

    override fun setImage(conversation: String?, nameImage: String, imageByteArray: ByteArray): Completable {
        return Completable.create { emitter ->
            db.reference.child(generatePath(conversation, nameImage)).putBytes(imageByteArray).addOnCompleteListener {
                emitter.onComplete()
            }.addOnFailureListener{
                emitter.onError(Throwable().initCause(it))
            }
        }
    }

    override fun getImage(conversation: String?, nameImage: String): Observable<ByteArray> {
        return Observable.create(ObservableOnSubscribe<ByteArray> {
            db.reference.child(generatePath(conversation, nameImage)).getBytes(Long.MAX_VALUE).addOnSuccessListener { image ->
                it.onNext(image)
            }.addOnFailureListener { exception ->
                it.onError(Throwable().initCause(exception))
            }
        }).subscribeOn(Schedulers.io())
    }

    private fun generatePath(conversation: String?, nameImage: String): String {
        return if (conversation != null){
            "images-chat/$conversation/$nameImage.png"
        } else {
            "images-chat/profile/$nameImage.png"
        }
    }
}