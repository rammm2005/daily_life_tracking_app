//package com.example.gym_app.repository
//
//import com.example.gym_app.model.DailyTracker
//import org.bson.Document
//import org.bson.types.ObjectId
//
//
//class DailyTrackerRepository(mongoUri: String, dbName: String) : BaseRepository<DailyTracker>(mongoUri, dbName, "dailyTrackers") {
//    override fun toDocument(item: DailyTracker) = Document().apply {
//        append("id", item.id)
//        append("userId", item.userId)
//        append("date", item.date)
//        append("weight_kg", item.weight_kg)
//        append("water_intake_liters", item.water_intake_liters)
//        append("sleep_hours", item.sleep_hours)
//        append("mood", item.mood)
//        append("workoutCompletedIds", item.workoutCompletedIds)
//        append("mealsEatenIds", item.mealsEatenIds)
//    }
//    override fun fromDocument(doc: Document) = DailyTracker(
//        id = doc.getObjectId("id"),
//        userId = doc.getObjectId("userId"),
//        date = doc.getString("date").toString(),
//        weight_kg = doc.getDouble("weight_kg"),
//        water_intake_liters = doc.getDouble("water_intake_liters"),
//        sleep_hours = doc.getInteger("sleep_hours"),
//        mood = doc.getString("mood"),
//        workoutCompletedIds = doc.getList("workoutCompletedIds", ObjectId::class.java) ?: emptyList(),
//        mealsEatenIds = doc.getList("mealsEatenIds", ObjectId::class.java) ?: emptyList()
//    )
//}
