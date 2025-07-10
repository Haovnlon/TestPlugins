package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.extractors.*
import com.lagradost.cloudstream3.utils.*

class HelvidExtractor : ExtractorApi() {
    override val name = "Helvid"
    override val mainUrl = "https://helvid.net"

    override suspend fun extract(url: String, referer: String?): List<ExtractorLink> {
        val id = url.substringAfter("/e/").substringBefore(".html")
        val res = app.post("$mainUrl/api/source/$id", headers = mapOf("referer" to url)).parsed<HelvidResponse>()
        return res.data.map {
            ExtractorLink(name, mainUrl, it.file, referer ?: url, getQualityFromName(it.label), isM3u8 = true)
        }
    }

    data class HelvidResponse(val data: List<Source>)
    data class Source(val file: String, val label: String)
}
