package com.example.gym_app.activity.mainactivity

import com.example.gym_app.model.Lession
import com.example.gym_app.model.Workout
import org.bson.types.ObjectId

object WorkoutDataProvider {

    fun getData(): List<Workout> = listOf(
        Workout(
            _id = ObjectId().toString(),
            title = "Running",
            description = "Desc...",
            category = "Cardio",
            picPath = "pic_1",
            kcal = 160,
            durationAll = "9 min",
            difficulty = "Beginner",
            video_url = null,
            lessons = getLessions1()
        ),
        Workout(
            _id = ObjectId().toString(),
            title = "Stretching",
            description = "Desc...",
            category = "Flexibility",
            picPath = "pic_2",
            kcal = 230,
            durationAll = "85 min",
            difficulty = "Intermediate",
            video_url = null,
            lessons = getLessions2()
        ),
        Workout(
            _id = ObjectId().toString(),
            title = "Yoga",
            description = "Desc...",
            category = "Mind & Body",
            picPath = "pic_3",
            kcal = 180,
            durationAll = "65 min",
            difficulty = "Advanced",
            video_url = null,
            lessons = getLessions3()
        )
    )

    fun getLessions1(): List<Lession> = listOf(
        Lession("","Lesson 1", "03:46", "https://youtube.com/watch?v=HBPMvFkpNgE","Blalalalalal"),
        Lession("","Lesson 2", "03:41", "https://youtube.com/watch?v=K6I24WgiiPw","Blalalalalal"),
        Lession("","Lesson 3", "01:57", "https://youtube.com/watch?v=Zc08v4YYOeA","Blalalalalal")
    )

    fun getLessions2(): List<Lession> = listOf(
        Lession("","Lesson 1", "20:23", "https://youtube.com/watch?v=L3eImBAXT7I","Blalalalalal"),
        Lession("","Lesson 2", "18:27", "https://youtube.com/watch?v=47ExgzO7FlU","Blalalalalal"),
        Lession("","Lesson 3", "32:25", "https://youtube.com/watch?v=OmLx8tmaQ-4","Blalalalalal"),
        Lession("","Lesson 4", "07:52", "https://youtube.com/watch?v=w86EalEoFRY","Blalalalalal")
    )

    fun getLessions3(): List<Lession> = listOf(
        Lession("","Lesson 1", "23:00", "https://youtube.com/watch?v=v7AYKMP6rOE","Blalalalalal"),
        Lession("","Lesson 2", "27:00", "https://youtube.com/watch?v=Eml2xnoLpYE","Blalalalalal"),
        Lession("","Lesson 3", "25:00", "https://youtube.com/watch?v=v7SN-d4qXx0","Blalalalalal"),
        Lession("","Lesson 4", "21:00", "https://youtube.com/watch?v=LqXZ628YNj4","Blalalalalal")
    )
}
