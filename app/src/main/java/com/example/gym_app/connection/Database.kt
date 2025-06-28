package com.example.gym_app.connection

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import com.mongodb.MongoException
import java.net.UnknownHostException

object MongoDBConnection {

    private const val CONNECTION_STRING = "mongodb://"

    private var mongoClient: MongoClient? = null

    fun getMongoClient(): MongoClient? {
        if (mongoClient == null) {
            synchronized(this) {
                if (mongoClient == null) {
                    try {
                        mongoClient = MongoClients.create(CONNECTION_STRING)
                        println("Koneksi ke MongoDB berhasil dibuat.")
                    } catch (e: UnknownHostException) {
                        println("ERROR: Tidak dapat menyelesaikan hostname MongoDB.")
                        println("Pastikan koneksi internet stabil, konfigurasi DNS benar, dan IP Anda diizinkan di MongoDB Atlas.")
                        println("Pesan Kesalahan: ${e.message}")
                        mongoClient = null
                    } catch (e: MongoException) {
                        println("ERROR: Terjadi masalah saat koneksi ke MongoDB.")
                        println("Pastikan string koneksi, nama pengguna, dan kata sandi sudah benar.")
                        println("Pesan Kesalahan: ${e.message}")
                        mongoClient = null
                    } catch (e: Exception) {
                        println("ERROR: Terjadi kesalahan tak terduga saat koneksi ke MongoDB.")
                        println("Pesan Kesalahan: ${e.message}")
                        mongoClient = null
                    }
                }
            }
        }
        return mongoClient
    }


    fun getDatabase(databaseName: String): MongoDatabase? {
        val client = getMongoClient()
        return client?.getDatabase(databaseName)
    }

    fun closeConnection() {
        mongoClient?.close()
        mongoClient = null
        println("Koneksi MongoDB ditutup.")
    }
}

// --- TestConnection ---
fun main() {

    val databaseName = "tracking_app"
    val myDatabase = MongoDBConnection.getDatabase(databaseName)

    if (myDatabase != null) {
        println("Berhasil terhubung ke database: ${myDatabase.name}")


        val usersCollection = myDatabase.getCollection("users")
        println("Berhasil mengakses koleksi: ${usersCollection.namespace.collectionName}")


        try {
            val document = org.bson.Document("username", "kotlin_user")
                .append("email", "kotlin@example.com")
                .append("status", "active")

            usersCollection.insertOne(document)
            println("Dokumen berhasil dimasukkan ke koleksi 'users'.")


            val foundDocument = usersCollection.find(org.bson.Document("username", "kotlin_user")).first()
            if (foundDocument != null) {
                println("Dokumen ditemukan: $foundDocument")
            } else {
                println("Dokumen tidak ditemukan.")
            }

        } catch (e: Exception) {
            println("ERROR saat melakukan operasi database: ${e.message}")
        }

    } else {
        println("Gagal terhubung ke database. Periksa log error di atas.")
    }

    MongoDBConnection.closeConnection()
}