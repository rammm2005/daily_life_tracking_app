package com.example.gym_app.repository

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.types.ObjectId

abstract class BaseRepository<T>(
    mongoUri: String,
    dbName: String,
    collectionName: String
) {
    protected val client: MongoClient = MongoClients.create(mongoUri)
    protected val database: MongoDatabase = client.getDatabase(dbName)
    protected val collection: MongoCollection<Document> = database.getCollection(collectionName)

    abstract fun toDocument(item: T): Document
    abstract fun fromDocument(doc: Document): T

    fun insert(item: T) = collection.insertOne(toDocument(item))
    fun getAll(): List<T> = collection.find().map { fromDocument(it) }.toList()
    fun getById(id: ObjectId): T? = collection.find(Document("_id", id)).first()?.let { fromDocument(it) }
    fun update(item: T, id: ObjectId) = collection.replaceOne(Document("_id", id), toDocument(item))
    fun delete(id: ObjectId) = collection.deleteOne(Document("_id", id))
}
