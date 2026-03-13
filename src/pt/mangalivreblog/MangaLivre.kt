package eu.kanade.tachiyomi.extension.pt.mangalivre

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class MangaLivre : ParsedHttpSource() {

    override val name = "MangaLivre"
    override val baseUrl = "https://mangalivre.blog"
    override val lang = "pt-BR"
    override val supportsLatest = true

    // Popular manga list
    override fun popularMangaRequest(page: Int): Request {
        return GET("$baseUrl/page/$page/", headers)
    }

    override fun popularMangaSelector() = "article"

    override fun popularMangaFromElement(element: Element): SManga {
        val manga = SManga.create()
        manga.title = element.select("h2").text()
        manga.setUrlWithoutDomain(element.select("a").attr("href"))
        manga.thumbnail_url = element.select("img").attr("src")
        return manga
    }

    override fun popularMangaNextPageSelector() = "a.next"

    // Chapters
    override fun chapterListSelector() = "li.wp-manga-chapter"

    override fun chapterFromElement(element: Element): SChapter {
        val chapter = SChapter.create()
        chapter.name = element.select("a").text()
        chapter.setUrlWithoutDomain(element.select("a").attr("href"))
        return chapter
    }

    // Pages
    override fun pageListParse(document: Document): List<Page> {
        val pages = mutableListOf<Page>()

        document.select("img").forEachIndexed { i, element ->
            val img = element.attr("src")
            if (img.contains(".jpg") || img.contains(".png")) {
                pages.add(Page(i, "", img))
            }
        }

        return pages
    }
}