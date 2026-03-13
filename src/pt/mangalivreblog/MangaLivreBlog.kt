package eu.kanade.tachiyomi.extension.pt.mangalivreblog

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class MangaLivreBlog : ParsedHttpSource() {
    override val name = "MangaLivre.blog"
    override val baseUrl = "https://mangalivre.blog"
    override val lang = "pt-BR"
    override val supportsLatest = true

    override fun popularMangaSelector() = "div.listagem-mangas div.item-manga"
    override fun popularMangaFromElement(element: Element): SManga = SManga.create().apply {
        val anchor = element.selectFirst("a")!!
        setUrlWithoutDomain(anchor.attr("href"))
        title = anchor.selectFirst("h2")?.text() ?: ""
        thumbnail_url = element.selectFirst("img")?.attr("abs:src")
    }
    override fun popularMangaNextPageSelector() = "div.pagination a.next"

    override fun mangaDetailsParse(document: Document): SManga = SManga.create().apply {
        val info = document.selectFirst("div.manga-info")
        description = info?.select("div.sinopse")?.text()
        genre = info?.select("div.genres a")?.joinToString { it.text() }
        status = SManga.ONGOING
    }

    override fun chapterListSelector() = "ul.lista-capitulos li.item-capitulo"
    override fun chapterFromElement(element: Element): SChapter = SChapter.create().apply {
        val anchor = element.selectFirst("a")!!
        setUrlWithoutDomain(anchor.attr("href"))
        name = anchor.text()
    }

    override fun pageListParse(document: Document): List<Page> {
        return document.select("div.reader-area img, div.read-content img").mapIndexed { i, img ->
            val url = img.attr("abs:data-src").ifEmpty { img.attr("abs:src") }
            Page(i, "", url)
        }
    }

    override fun latestUpdatesSelector() = popularMangaSelector()
    override fun latestUpdatesFromElement(element: Element) = popularMangaFromElement(element)
    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList) = GET("$baseUrl/page/$page/?s=$query", headers)
    override fun searchMangaSelector() = popularMangaSelector()
    override fun searchMangaFromElement(element: Element) = popularMangaFromElement(element)
    override fun searchMangaNextPageSelector() = popularMangaNextPageSelector()
}
