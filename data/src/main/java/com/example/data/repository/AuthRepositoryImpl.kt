package com.example.data.repository

import com.example.domain.auth.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(private val db: FirebaseAuth) : AuthRepository {

    override fun logautUser() {
       db.signOut()
    }

    override fun getUserAuth(): String? {
        return db.currentUser?.email
    }

    override fun signup(username: String, password: String): Completable {
        return Completable.create { emitter ->
            db.createUserWithEmailAndPassword(username, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    emitter.onComplete()
                } else {
                    emitter.onError(Throwable().initCause(it.exception))
                }
            }
        }
    }

    override fun login(username: String, password: String): Completable {
        return Completable.create { emitter ->
            db.signInWithEmailAndPassword(username, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    emitter.onComplete()
                } else {
                    emitter.onError(Throwable().initCause(it.exception))
                }
            }
        }
    }
}