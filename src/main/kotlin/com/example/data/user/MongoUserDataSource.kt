package com.example.data.user

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull


class MongoUserDataSource(
    db: MongoDatabase
): UserDataSource {

    private val users = db.getCollection<User>("user")

    override suspend fun getUserByEmail(email: String): User? {
        return users.find(Filters.eq(User::email.name, email)).firstOrNull()
        //  return users.findOne(User::username eq username) KMongo format
    }


    override suspend fun insertUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }

}

