package com.hannesdorfmann.data.source

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.Column
import com.hannesdorfmann.sqlbrite.objectmapper.annotation.ObjectMappable

/**
 * Represents an source that can be selected
 * @author Hannes Dorfmann
 */
@ObjectMappable
class Source() { // Unfortunately data class not supported yet by sqlbrite-dao

    object ID {
        internal const val UNKNOWN_ID = -1L
        const val DESIGNER_NEWS_POPULAR = -2L
        const val DESIGNER_NEWS_RECENT = -3L
        const val DRIBBBLE_POPULAR = -4L
        const val DRIBBBLE_FOLLOWING = -5L
        const val DRIBBLE_MY_SHOTS = -6L
        const val DRIBBLE_MY_LIKES = -7L
        const val DRIBBLE_RECENT = -8L
        const val DRIBBLE_DEBUTS = -9L
        const val DRIBBLE_ANIMATED = -10L
        const val DRIBBLE_MATERIAL = -11L
        const val PRODUCT_HUNT = -12L
    }

    @Column(SourceDaoImpl.COL.ID)
    var id: Long = ID.UNKNOWN_ID

    @Column(SourceDaoImpl.COL.ORDER)
    var order: Int = 0

    @Column(SourceDaoImpl.COL.ENABLED)
    var enabled: Boolean = false

    @Column(SourceDaoImpl.COL.AUTH_REQUIRED)
    var authenticationRequired = false

    @Column(SourceDaoImpl.COL.BACKEND_ID)
    var backendId: Int = -1

    @Column(SourceDaoImpl.COL.NAME)
    var name: String? = null

    @Column(SourceDaoImpl.COL.NAME_RES)
    var nameRes: Int = -1

    constructor(id: Long = ID.UNKNOWN_ID, name: String, order: Int, enabled: Boolean, backendId: Int, authenticationRequired: Boolean) : this() {
        this.id = id
        this.order = order
        this.enabled = enabled
        this.authenticationRequired = authenticationRequired
        this.backendId = backendId
        this.name = name
    }


    constructor(id: Long = ID.UNKNOWN_ID, nameRes: Int, order: Int, enabled: Boolean, backendId: Int, authenticationRequired: Boolean) : this() {
        this.id = id
        this.order = order
        this.enabled = enabled
        this.authenticationRequired = authenticationRequired
        this.backendId = backendId
        this.name = name
        this.nameRes = nameRes
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Source

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "id: ${id}, name: ${name}, nameRes: ${nameRes}, enabeld: ${enabled}, order: ${order}, authRequired: ${authenticationRequired}"
    }
}