/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.devbyteviewer.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface VideoDao {

    /**
     * Gets a list of database video
     *
     * Since this is returning a LiveData object, room will run this query in a background thread
     * when we call it form the main UI thread
     */
    @Query("select * from databasevideo")
    fun getVideos(): LiveData<List<DatabaseVideo>>

    /**
     * Inserts and updates videos in the database
     *
     * OnConflictStrategy.REPLACE will replace any primary keys that are the same
     *
     * vararg lets us pass in a unknown number of arguments
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg videos: DatabaseVideo)
}


@Database(entities = [DatabaseVideo::class], version = 1)
abstract class VideosDatabase : RoomDatabase() {
    abstract val videoDao: VideoDao
}

/**
 * This is a singleton. A singleton is an object that can only have one instance.
 */
private lateinit var INSTANCE: VideosDatabase

/**
 * This function returns the an instance of the databse if it does not already exist
 */
fun getDatabase(context: Context): VideosDatabase {

    // synchronizes the code so that  it is thread safe
    synchronized(VideosDatabase::class.java) {
        // Checks whether the database has been initialized
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                VideosDatabase::class.java,
                "videos"
            ).build()
        }
    }

    return INSTANCE
}