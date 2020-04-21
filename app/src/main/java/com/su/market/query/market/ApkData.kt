package com.su.market.query.market

data class ApkData(
    val download: Float,
    val downloadUnit: String? = "",
    val watch: Float,
    val watchUnit: String? = "",
    val comment: Float,
    val commentUnit: String? = "",
    val rank: Float,
    val rankCount: Float,
    val rankCountUnit: String? = "",
    val time: Long
) {
    fun downloadIncrement(old: ApkData): Float {
        val newData = UnitUtil.toNumber(download, downloadUnit)
        val oldData = UnitUtil.toNumber(old.download, old.downloadUnit)
        return newData - oldData
    }

    fun watchIncrement(old: ApkData): Float {
        val newData = UnitUtil.toNumber(watch, watchUnit)
        val oldData = UnitUtil.toNumber(old.watch, old.watchUnit)
        return newData - oldData
    }

    fun commentIncrement(old: ApkData): Float {
        val newData = UnitUtil.toNumber(comment, commentUnit)
        val oldData = UnitUtil.toNumber(old.comment, old.commentUnit)
        return newData - oldData
    }

    fun rankCountIncrement(old: ApkData): Float {
        val newData = UnitUtil.toNumber(rankCount, rankCountUnit)
        val oldData = UnitUtil.toNumber(old.rankCount, old.rankCountUnit)
        return newData - oldData
    }

    fun rankIncrement(old: ApkData): Float {
        return rank - old.rank
    }
}
