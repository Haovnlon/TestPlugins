package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.Jsoup
import com.example.HelvidExtractor

class SieuTamPhim : MainAPI() {
    override var name = "SieuTamPhim"
    override var mainUrl = "https://sieutamphim.org"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)
    override val hasMainPage = true
    override val lang = "vi"

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val doc = app.get(mainUrl).document
        val items = doc.select("div.post").mapNotNull {
            val title = it.selectFirst("h2")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img")?.attr("src")
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = poster
            }
        }
        return newHomePageResponse(listOf(HomePageList("Phim mới", items)))
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(url).document
        val title = doc.selectFirst("h1")?.text().orEmpty()
        val iframe = doc.selectFirst("iframe[src*='helvid']")?.attr("src")
            ?: throw ErrorLoadingException("Không tìm thấy iframe Helvid")

        return newMovieLoadResponse(title, url, TvType.Movie, iframe)
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        HelvidExtractor().extract(data, referer = mainUrl).forEach {
            callback(it)
        }
        return true
    }
}
