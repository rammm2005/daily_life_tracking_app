package com.example.gym_app.repository

import com.example.gym_app.model.Message
import com.example.gym_app.model.Reminder
import org.bson.Document
import org.bson.types.ObjectId

class ReminderRepository(mongoUri: String, dbName: String) : BaseRepository<Reminder>(mongoUri, dbName, "reminders") {
    override fun toDocument(item: Reminder) = Document().apply {
        append("_id", item._id)
        append("userId", item.userId)
        append("title", item.title)
        append("schedule", item.schedule)
        append("days", item.days)
        append("method", item.method)
        append("type", item.type)
    }
    override fun fromDocument(doc: Document) = Reminder(
        _id = doc.getObjectId("_id"),
        userId = doc.getObjectId("userId"),
        title = doc.getString("title"),
        schedule = doc.getString("schedule"),
        days = doc.getList("days", String::class.java),
        method = doc.getList("method", String::class.java),
        type = doc.getString("type").toString()
    )
}



