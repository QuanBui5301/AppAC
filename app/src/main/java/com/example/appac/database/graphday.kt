package com.example.appac.database

data class graphday(
    val `data`: List<DataXXX>,
    val links: LinksX,
    val meta: MetaX
) {
    data class LinksX(
        val first: String,
        val last: String,
        val next: String,
        val prev: Any
    )
    data class DataXXX(
        val createdAt: String,
        val `data`: String,
        val id: Int
    )
    data class MetaX(
        val current_page: Int,
        val from: Int,
        val last_page: Int,
        val links: List<LinkX>,
        val path: String,
        val per_page: Int,
        val to: Int,
        val total: Int
    )
}