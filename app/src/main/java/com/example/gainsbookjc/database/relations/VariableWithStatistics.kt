package com.example.gainsbookjc.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.gainsbookjc.database.entities.Statistic
import com.example.gainsbookjc.database.entities.Variable

data class VariableWithStatistics(
    @Embedded val variable: Variable,
    @Relation(
        parentColumn = "variableID",
        entityColumn = "variableID"
    )
    val statistics: List<Statistic>
)
