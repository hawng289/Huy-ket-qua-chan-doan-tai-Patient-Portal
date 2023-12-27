package vn.com.itechcorp.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.safety.Safelist;
import org.jsoup.safety.Whitelist;

public class HtmlUtil {

    private static final HtmlUtil instance = new HtmlUtil();

    public static HtmlUtil getInstance() {
        return instance;
    }

    public String convertToText(String htmlContent) {
        Document document = Jsoup.parse(htmlContent);
        document.outputSettings(new Document.OutputSettings().prettyPrint(false));
        document.select("li").append("\\n");
        document.select("div").append("\\n");
        document.select("br").append("\\n");
        document.select("p").prepend("\\n\\n");
        String s = document.html().replaceAll("\\\\n", "\\\\X0D\\\\\\\\X0A\\\\");

        String clean = Jsoup.clean(s, "", Safelist.none());

        return Parser.unescapeEntities(clean, false);
    }
}
