package com.example.android.politicalpreparedness.database

import androidx.room.*
import com.example.android.politicalpreparedness.network.models.AdministrationBody
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Query("SELECT * FROM election_table")
    fun getAllElections(): List<Election>

    @Query("SELECT id FROM election_table WHERE id=:electionID")
    fun checkElectionSaved(electionID: Int): Int

    @Delete
    fun deleteElection(election: Election)

    @Transaction
    @Query("SELECT * FROM election_table WHERE id=:electionID")
    fun getElectionAndAdministrationBody(electionID: Int): ElectionAndAdministrationBody

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElectionAndAdministrationBody(
        election: Election,
        administrationBody: AdministrationBody
    )

    @Transaction
    @Delete
    fun deleteElectionAndAdminstrationBody(
        electionID: Election,
        administrationBody: AdministrationBody
    )

}