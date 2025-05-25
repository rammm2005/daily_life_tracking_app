package com.example.gym_app.repository

import com.example.gym_app.model.Lession
import org.bson.Document

class SessionRepository(mongoUri: String, dbName: String) : BaseRepository<Lession>(mongoUri, dbName, "sessions") {
    override fun toDocument(item: Lession) = Document().apply {
        append("_id", item._id)
        append("workoutId", item.workoutId)
        append("title", item.title)
        append("description", item.description)
        append("duration", item.duration)
        append("repetitions", item.repetitions)
        append("rest_seconds", item.rest_seconds)
        append("video_url", item.video_url)
        append("link", item.link)
        append("picPath", item.picPath)
    }
    override fun fromDocument(doc: Document) = Lession(
        _id = doc.getObjectId("_id"),
        workoutId = doc.getObjectId("workoutId"),
        title = doc.getString("name"),
        description = doc.getString("description"),
        duration = doc.getInteger("duration_minutes").toString(),
        repetitions = doc.getInteger("repetitions"),
        rest_seconds = doc.getInteger("rest_seconds"),
        video_url = doc.getString("video_url"),
        link = doc.getString("link"),
        picPath = doc.getString("picPath")
    )
}
