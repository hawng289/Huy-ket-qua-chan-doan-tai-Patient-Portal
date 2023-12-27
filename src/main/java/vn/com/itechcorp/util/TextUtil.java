package vn.com.itechcorp.util;


import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.rtf.RTFEditorKit;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextUtil {

    private final Map<Character, Character> unicodeMap = new HashMap();
    private final Map<Character, Character> toLowerMap = new HashMap();
    private static final TextUtil instance = new TextUtil();

    public static TextUtil getInstance() {
        return instance;
    }

    private TextUtil() {
        char[] SOURCE_CHARACTERS = new char[]{'À', 'Á', 'Â', 'Ã', 'È', 'É', 'Ê', 'Ì', 'Í', 'Ò', 'Ó', 'Ô', 'Õ', 'Ù', 'Ú', 'Ý', 'à', 'á', 'â', 'ã', 'è', 'é', 'ê', 'ì', 'í', 'ò', 'ó', 'ô', 'õ', 'ù', 'ú', 'ý', 'Ă', 'ă', 'Đ', 'đ', 'Ĩ', 'ĩ', 'Ũ', 'ũ', 'Ơ', 'ơ', 'Ư', 'ư', 'Ạ', 'ạ', 'Ả', 'ả', 'Ấ', 'ấ', 'Ầ', 'ầ', 'Ẩ', 'ẩ', 'Ẫ', 'ẫ', 'Ậ', 'ậ', 'Ắ', 'ắ', 'Ằ', 'ằ', 'Ẳ', 'ẳ', 'Ẵ', 'ẵ', 'Ặ', 'ặ', 'Ẹ', 'ẹ', 'Ẻ', 'ẻ', 'Ẽ', 'ẽ', 'Ế', 'ế', 'Ề', 'ề', 'Ể', 'ể', 'Ễ', 'ễ', 'Ệ', 'ệ', 'Ỉ', 'ỉ', 'Ị', 'ị', 'Ọ', 'ọ', 'Ỏ', 'ỏ', 'Ố', 'ố', 'Ồ', 'ồ', 'Ổ', 'ổ', 'Ỗ', 'ỗ', 'Ộ', 'ộ', 'Ớ', 'ớ', 'Ờ', 'ờ', 'Ở', 'ở', 'Ỡ', 'ỡ', 'Ợ', 'ợ', 'Ụ', 'ụ', 'Ủ', 'ủ', 'Ứ', 'ứ', 'Ừ', 'ừ', 'Ử', 'ử', 'Ữ', 'ữ', 'Ự', 'ự', 'Ỳ', 'ỳ', 'Ỵ', 'ỵ', 'Ỷ', 'ỷ', 'Ỹ', 'ỹ'};
        char[] DESTINATION_CHARACTERS = new char[]{'A', 'A', 'A', 'A', 'E', 'E', 'E', 'I', 'I', 'O', 'O', 'O', 'O', 'U', 'U', 'Y', 'a', 'a', 'a', 'a', 'e', 'e', 'e', 'i', 'i', 'o', 'o', 'o', 'o', 'u', 'u', 'y', 'A', 'a', 'D', 'd', 'I', 'i', 'U', 'u', 'O', 'o', 'U', 'u', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'A', 'a', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'E', 'e', 'I', 'i', 'I', 'i', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'O', 'o', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'U', 'u', 'Y', 'y', 'Y', 'y', 'Y', 'y', 'Y', 'y'};
        char[] LOWER_CHARACTERS = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        char[] UPPER_CHARACTERS = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

        int i;
        for (i = 0; i < SOURCE_CHARACTERS.length; ++i) {
            this.unicodeMap.put(SOURCE_CHARACTERS[i], DESTINATION_CHARACTERS[i]);
        }

        for (i = 0; i < UPPER_CHARACTERS.length; ++i) {
            this.toLowerMap.put(UPPER_CHARACTERS[i], LOWER_CHARACTERS[i]);
        }

    }

    public Character toASCII(char ch, boolean toLowerCase) {
        char out = ch;
        if (this.unicodeMap.containsKey(ch)) {
            out = this.unicodeMap.get(ch);
        }

        if (!toLowerCase) {
            return out;
        } else {
            return this.toLowerMap.containsKey(out) ? this.toLowerMap.get(out) : out;
        }
    }

    public String toASCII(String input, boolean toLowerCase) {
        if (input == null) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder();
            char[] var4 = input.toCharArray();
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                char ch = var4[var6];
                builder.append(this.toASCII(ch, toLowerCase));
            }

            return builder.toString();
        }
    }

    public String toCleanText(String text) {
        if (text == null || text.trim().isEmpty()) return " ";
        return text.trim();
    }

    public String convertToRTF(String content) {
        RTFEditorKit rtfEditorKit = new RTFEditorKit();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            Document document = rtfEditorKit.createDefaultDocument();
            document.insertString(0, content, null);
            rtfEditorKit.write(os, document, 0, document.getLength());

            return os.toString();
        } catch (IOException | BadLocationException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
