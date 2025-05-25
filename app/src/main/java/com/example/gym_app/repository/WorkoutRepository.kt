package com.example.gym_app.repository

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.types.ObjectId
import org.bson.Document
import com.example.gym_app.model.Workout
import com.example.gym_app.model.Lession


class WorkoutRepository(mongoUri: String, dbName: String) : BaseRepository<Workout>(mongoUri, dbName, "workouts") {
    override fun toDocument(item: Workout) = Document().apply {
        append("_id", item._id)
        append("title", item.title)
        append("description", item.description)
        append("category", item.category)
        append("duration_minutes", item.durationAll)
        append("difficulty", item.difficulty)
        append("video_url", item.video_url)
        append("calories_burned", item.kcal)
        append("lessons", item.lessons.map { lession ->
            Document().apply {
                append("_id", lession._id)
                append("workoutId", lession.workoutId)
                append("title", lession.title)
                append("description", lession.description)
                append("duration", lession.duration)
                append("link", lession.link)
                append("picPath", lession.picPath)
                append("repetitions", lession.repetitions)
                append("rest_seconds", lession.rest_seconds)
                append("video_url", lession.video_url)
            }
        })
    }
    override fun fromDocument(doc: Document) = Workout(
        _id = doc.getObjectId("_id"),
        title = doc.getString("title"),
        description = doc.getString("description"),
        category = doc.getString("category"),
        durationAll = doc.getInteger("durationAll").toString(),
        difficulty = doc.getString("difficulty"),
        video_url = doc.getString("video_url"),
        kcal = doc.getInteger("kcal"),
        lessons = doc.getList("lessons", Document::class.java)?.map { lessionDoc ->
            Lession(
                _id = lessionDoc.getObjectId("_id"),
                workoutId = lessionDoc.getObjectId("workoutId"),
                title = lessionDoc.getString("title"),
                description = lessionDoc.getString("description"),
                duration = lessionDoc.getString("duration"),
                link = lessionDoc.getString("link"),
                picPath = lessionDoc.getString("picPath"),
                repetitions = lessionDoc.getInteger("repetitions"),
                rest_seconds = lessionDoc.getInteger("rest_seconds"),
                video_url = lessionDoc.getString("video_url")
            )
        } ?: emptyList()
    )
}

