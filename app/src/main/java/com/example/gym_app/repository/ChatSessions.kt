package com.example.gym_app.repository

import com.example.gym_app.model.ChatSession
import com.example.gym_app.model.Message
import org.bson.Document
import org.bson.types.ObjectId


class ChatSessionRepository(mongoUri: String, dbName: String) : BaseRepository<ChatSession>(mongoUri, dbName, "chat_sessions") {
    override fun toDocument(item: ChatSession) = Document().apply {
        append("_id", item._id)
        append("userId", item.userId)
        append("startedAt", item.startedAt)
        append("messages", item.messages)
    }
    override fun fromDocument(doc: Document) = ChatSession(
        _id = doc.getObjectId("_id"),
        userId = doc.getObjectId("userId"),
        startedAt = doc.getString("startedAt").toString(),
        messages = doc.getList("messages", Message::class.java) ?: emptyList()
    )
}
