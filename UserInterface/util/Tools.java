package util;

import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;
import socketClient.SocketClient;

import java.io.IOException;

public class Tools {
    public static String socketConnect(String jsonString) throws IOException {
        SocketClient client = new SocketClient("localhost", 5001);
        client.connect();
        client.send(jsonString);
        String date = client.receive();
        client.close();
        return date;
    }

    public static JSONObject transferToJSONObject(String s) throws JSONException {
        String tokenInfoEsca = StringEscapeUtils.unescapeJava(s);
        // 去除前后的双引号
        tokenInfoEsca = tokenInfoEsca.substring(1, tokenInfoEsca.length() - 1);
        // 转换为json对象
        JSONObject jsonObject = new JSONObject(tokenInfoEsca);
        return jsonObject;
    }

    public static String unicodeToChinese(String unicodeString) {
        StringBuilder chineseText = new StringBuilder();
        int startIndex = 0;

        while (startIndex < unicodeString.length()) {
            int slashIndex = unicodeString.indexOf("\\u", startIndex);
            if (slashIndex == -1) {
                chineseText.append(unicodeString.substring(startIndex));
                break;
            }
            chineseText.append(unicodeString, startIndex, slashIndex);

            int codeStart = slashIndex + 2;
            int codeEnd = codeStart + 4;
            if (codeEnd <= unicodeString.length()) {
                String unicodeCode = unicodeString.substring(codeStart, codeEnd);
                char character = (char) Integer.parseInt(unicodeCode, 16);
                chineseText.append(character);
                startIndex = codeEnd;
            } else {
                chineseText.append(unicodeString.substring(slashIndex));
                break;
            }
        }

        return chineseText.toString();
    }
}
