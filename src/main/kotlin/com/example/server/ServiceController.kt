package com.example.server

import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import java.io.IOException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*


val settingsPath = """C:\Users\Niko\Desktop\server\parsersSettings.xml"""

@RestController
class ServiceController
{
    @GetMapping("/getSettings")
    fun getSettings():List<SettingsResponse>
    {
        val fXmlFile = File(settingsPath)
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = dBuilder.parse(fXmlFile)
        val response = ArrayList<SettingsResponse>(10)

        doc.getDocumentElement().normalize();

        val parsersList = doc.getElementsByTagName("parser")

        for (i in 0..parsersList.length-1)
        {
            val sr = SettingsResponse()
            val parser = parsersList.item(i) as Element

            sr.store = parser.getElementsByTagName("store").item(0).textContent
            sr.searchUrl = parser.getElementsByTagName("searchUrl").item(0).textContent
            sr.pathToBlock = parser.getElementsByTagName("pathToBlock").item(0).textContent
            sr.relPathToName = parser.getElementsByTagName("relPathToName").item(0).textContent
            sr.relPathToImg = parser.getElementsByTagName("relPathToImg").item(0).textContent
            sr.relPathToPrice = parser.getElementsByTagName("relPathToPrice").item(0).textContent
            sr.relPathToAuthor = parser.getElementsByTagName("relPathToAuthor").item(0).textContent
            sr.relPathToBook = parser.getElementsByTagName("relPathToBook").item(0).textContent
            sr.pathToISBN = parser.getElementsByTagName("pathToISBN").item(0).textContent
            sr.delimiter = parser.getElementsByTagName("delimiter").item(0).textContent
            response.add(sr)
        }

        return response
    }

    @PostMapping("/getISBN")
    fun getISBN(@RequestBody reqBody:String) : String
    {
        val url:String = reqBody.split("|||")[0];
        val pathtoISBN:String = reqBody.split("|||")[1];
        var doc : Document? = null

        try
        {
           doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64;" + " x64; rv:25.0) Gecko/20100101 Firefox/25.0")
                    .referrer("http://www.google.com").get()
        } catch (e: IOException)
        {
            e.printStackTrace()
            return ""
        }

        val elements = doc.select(pathtoISBN)
        if (url.startsWith("https://www.bookvoed.ru"))
        {
            for (e in elements)
            {
                if (e.getElementsByClass("vw").first().text().equals("ISBN:"))
                {
                    return e.getElementsByClass("ww").first().text()
                }
            }
        }
        if (url.startsWith("https://www.spbdk.ru"))
        {
            for (e in elements)
            {
                if (e.getElementsByClass("params__title").first()
                                .getElementsByTag("span").text().equals("ISBN"))
                {
                    return e.getElementsByClass("params__value").first()
                            .getElementsByTag("span").text()
                }
            }
        }
        if (url.startsWith("https://www.labirint.ru"))
        {
            if (doc.selectFirst(pathtoISBN) == null)
                return ""
            return if (doc.selectFirst(pathtoISBN).text().length < 6 + 17)
                doc.selectFirst(pathtoISBN).text()
            else
                doc.selectFirst(pathtoISBN).text().substring(6, 6 + 17)
        }
        return ""
    }
}