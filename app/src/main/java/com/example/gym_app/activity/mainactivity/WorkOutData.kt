package com.example.gym_app.activity.mainactivity

import com.example.gym_app.model.Lession
import com.example.gym_app.model.Workout
import org.bson.types.ObjectId

object WorkoutDataProvider {

    fun getData(): List<Workout> = listOf(
        Workout(
            title = "Running",
            description = "Desc...",
            picPath = "pic_1",
            kcal = 160,
            durationAll = "9 min",
            lessons = getLessions1()
        ),
        Workout(
            title = "Stretching",
            description = "Desc...",
            picPath = "pic_2",
            kcal = 230,
            durationAll = "85 min",
            lessons = getLessions2()
        ),
        Workout(
            title = "Yoga",
            description = "Desc...",
            picPath = "pic_3",
            kcal = 180,
            durationAll = "65 min",
            lessons = getLessions3()
        )
    )

    fun getLessions1(): List<Lession> = listOf(
        Lession(
            _id = ObjectId(),
            workoutId = ObjectId(),
            title = "Lesson 1",
            description = "Introduction to warm-up exercises",
            duration = "03:46",
            link = "HBPMvFkpNgE",
            picPath = "pic_1_1",
            repetitions = 10,
            rest_seconds = 30,
            video_url = "https://youtube.com/watch?v=HBPMvFkpNgE"
        ),
        Lession(
            _id = ObjectId(),
            workoutId = ObjectId(),
            title = "Lesson 2",
            description = "Stretching basics",
            duration = "03:41",
            link = "K6I24WgiiPw",
            picPath = "pic_1_2",
            repetitions = 15,
            rest_seconds = 20,
            video_url = "https://youtube.com/watch?v=K6I24WgiiPw"
        ),
        Lession(
            _id = ObjectId(),
            workoutId = ObjectId(),
            title = "Lesson 3",
            description = "Core strengthening",
            duration = "01:57",
            link = "Zc08v4YYOeA",
            picPath = "pic_1_3",
            repetitions = 12,
            rest_seconds = 25,
            video_url = "https://youtube.com/watch?v=Zc08v4YYOeA"
        )
    )

    fun getLessions2(): List<Lession> = listOf(
        Lession(
            _id = ObjectId(),
            workoutId = ObjectId(),
            title = "Lesson 1",
            description = "Basic stretching routine",
            duration = "20:23",
            link = "L3eImBAXT7I",
            picPath = "pic_3_1",
            repetitions = 8,
            rest_seconds = 40,
            video_url = "https://youtube.com/watch?v=L3eImBAXT7I"
        ),
        Lession(
            _id = ObjectId(),
            workoutId = ObjectId(),
            title = "Lesson 2",
            description = "Intermediate stretching",
            duration = "18:27",
            link = "47ExgzO7FlU",
            picPath = "pic_3_2",
            repetitions = 10,
            rest_seconds = 35,
            video_url = "https://youtube.com/watch?v=47ExgzO7FlU"
        ),
        Lession(
            _id = ObjectId(),
            workoutId = ObjectId(),
            title = "Lesson 3",
            description = "Advanced stretches",
            duration = "32:25",
            link = "OmLx8tmaQ-4",
            picPath = "pic_3_3",
            repetitions = 12,
            rest_seconds = 30,
            video_url = "https://youtube.com/watch?v=OmLx8tmaQ-4"
        ),
        Lession(
            _id = ObjectId(),
            workoutId = ObjectId(),
            title = "Lesson 4",
            description = "Cool down",
            duration = "07:52",
            link = "w86EalEoFRY",
            picPath = "pic_3_4",
            repetitions = 6,
            rest_seconds = 50,
            video_url = "https://youtube.com/watch?v=w86EalEoFRY"
        )
    )

    fun getLessions3(): List<Lession> = listOf(
        Lession(
            _id = ObjectId(),
            workoutId = ObjectId(),
            title = "Lesson 1",
            description = "Beginner yoga session",
            duration = "23:00",
            link = "v7AYKMP6rOE",
            picPath = "pic_3_1",
            repetitions = 5,
            rest_seconds = 60,
            video_url = "https://youtube.com/watch?v=v7AYKMP6rOE"
        ),
        Lession(
            _id = ObjectId(),
            workoutId = ObjectId(),
            title = "Lesson 2",
            description = "Sun salutations",
            duration = "27:00",
            link = "Eml2xnoLpYE",
            picPath = "pic_3_2",
            repetitions = 7,
            rest_seconds = 55,
            video_url = "https://youtube.com/watch?v=Eml2xnoLpYE"
        ),
        Lession(
            _id = ObjectId(),
            workoutId = ObjectId(),
            title = "Lesson 3",
            description = "Yoga for flexibility",
            duration = "25:00",
            link = "v7SN-d4qXx0",
            picPath = "pic_3_3",
            repetitions = 8,
            rest_seconds = 45,
            video_url = "https://youtube.com/watch?v=v7SN-d4qXx0"
        ),
        Lession(
            _id = ObjectId(),
            workoutId = ObjectId(),
            title = "Lesson 4",
            description = "Relaxation techniques",
            duration = "21:00",
            link = "LqXZ628YNj4",
            picPath = "pic_3_4",
            repetitions = 6,
            rest_seconds = 50,
            video_url = "https://youtube.com/watch?v=LqXZ628YNj4"
        )
    )


}
