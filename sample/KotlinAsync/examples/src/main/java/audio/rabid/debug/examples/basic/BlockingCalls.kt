package audio.rabid.debug.examples.basic

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

/**
 * Created by cjk on 9/18/17.
 */

data class User(val id: Int, val email: String, val name: String)

suspend fun getUserById(db: SQLiteDatabase, id: Int): User? = async(CommonPool) {

    db.query("users", arrayOf("id", "email", "name"), "id = ?",
            arrayOf(id.toString()), null, null, null).use { cursor ->
        if (cursor.count == 0)
            return@async null
        cursor.moveToFirst()
        User(
                id = cursor.getInt(cursor.getColumnIndex("id")),
                email = cursor.getString(cursor.getColumnIndex("email")),
                name = cursor.getString(cursor.getColumnIndex("name"))
        )
    }
}.await()

suspend fun saveUser(db: SQLiteDatabase, user: User) = async(CommonPool) {
    db.insert("users", null, ContentValues().apply {
        put("id", user.id)
        put("name", user.name)
        put("email", user.email)
    })
}.await()