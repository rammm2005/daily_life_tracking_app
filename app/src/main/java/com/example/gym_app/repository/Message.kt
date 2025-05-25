package com.example.gym_app.repository

import com.example.gym_app.model.Message
import org.bson.Document
import org.bson.types.ObjectId
import java.util.Date

class MessageRepository(mongoUri: String, dbName: String) : BaseRepository<Message>(mongoUri, dbName, "messages") {
    override fun toDocument(item: Message) = Document().apply {
        append("_id", item._id)
        append("timestamp", item.timestamp)
        append("userId", item.userId)
        append("content", item.content)
        append("role", item.role)
    }
    override fun fromDocument(doc: Document) = Message(
        _id = doc.getObjectId("_id"),
        userId = doc.getObjectId("userId"),
        timestamp = doc.getDate("timestamp"),
        content = doc.getString("content"),
        role = doc.getString("sentAt")
    )
}