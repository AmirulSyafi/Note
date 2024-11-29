package com.amirulsyafi.note.data.assignment

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Assignment(
    @PrimaryKey
    @SerializedName("SlotNo") // Maps the JSON field "SlotNo" to "slotNo"
    val slotNo: Int,

    @SerializedName("AssignmentName") // Maps the JSON field "AssignmentName" to "assignmentName"
    val assignmentName: String,

    @SerializedName("MO") // Maps the JSON field "MO" to "mo"
    val mo: String,

    @SerializedName("OP") // Maps the JSON field "OP" to "op"
    val op: String,

    @SerializedName("SMV") // Maps the JSON field "SMV" to "smv"
    val smv: String,

    @SerializedName("OpRatio") // Maps the JSON field "OpRatio" to "opRatio"
    val opRatio: String,

    @SerializedName("PreviousOp") // Maps the JSON field "PreviousOp" to "previousOp"
    val previousOp: String,

    @SerializedName("IsGroupOp") // Maps the JSON field "IsGroupOp" to "isGroupOp"
    val isGroupOp: Boolean
)