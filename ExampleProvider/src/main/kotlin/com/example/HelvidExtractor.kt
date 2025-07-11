package com.example

import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.getQualityFromName
import com.lagradost.cloudstream3.utils.newExtractorLink

class HelvidExtractor : ExtractorApi() {
    override val name = "Helvid"
    override val mainUrl = "https://helvid.com"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val videoUrl = app.get(url, referer = referer).document
            .selectFirst("video > source")?.attr("src") ?: return

        callback(
            newExtractorLink(
                name,
                name,
                videoUrl,
                referer = referer,
                quality = getQualityFromName(videoUrl)
            )
        )
    }
}
