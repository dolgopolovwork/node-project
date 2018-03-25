package ru.babobka.vsjws.model.http;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import org.apache.tika.Tika;
import org.apache.tika.io.IOUtils;
import ru.babobka.nodeutils.util.JSONUtil;
import ru.babobka.nodeutils.util.TextUtil;
import ru.babobka.vsjws.enumerations.ContentType;
import ru.babobka.vsjws.enumerations.RegularExpressions;
import ru.babobka.vsjws.enumerations.ResponseCode;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Created by 123 on 03.01.2018.
 */
public class ResponseFactory {

    public static final Charset MAIN_ENCODING = StandardCharsets.UTF_8;
    private static final Gson GSON = new Gson();
    private static final Tika TIKA = new Tika();

    public static HttpResponse raw(byte[] content, ResponseCode code, String contentType) {
        return new HttpResponse(code, contentType, content, null, content.length);
    }

    public static HttpResponse file(File file) {
        if (file.exists() && file.isFile()) {
            try {
                return new HttpResponse(ResponseCode.OK, TIKA.detect(file), null, file, file.length());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        } else {
            throw new IllegalStateException(new FileNotFoundException("File " + file.getAbsolutePath() + " doesn't exist"));
        }
    }

    public static HttpResponse resource(String fileName) {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new IllegalStateException(new FileNotFoundException("File " + fileName + " doesn't exist"));
            }
            byte[] bytes = IOUtils.toByteArray(is);
            return new HttpResponse(ResponseCode.OK, TIKA.detect(bytes), bytes, null, bytes.length);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static HttpResponse resource(InputStream is) {
        try {
            byte[] bytes = IOUtils.toByteArray(is);
            return new HttpResponse(ResponseCode.OK, TIKA.detect(bytes), bytes, null, bytes.length);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static HttpResponse redirect(String url) {
        String localUrl = url;
        if (!localUrl.startsWith("http")) {
            localUrl = "http://" + localUrl;
        }
        if (url.matches(RegularExpressions.URL_PATTERN.toString())) {
            return text("Redirection", ContentType.PLAIN).setResponseCode(ResponseCode.SEE_OTHER).addHeader("Location",
                    localUrl);
        } else {
            throw new IllegalArgumentException("URL '" + url + "' is not valid");
        }
    }

    public static HttpResponse json(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("json object cannot be null");
        } else {
            if (object instanceof String) {
                return json(object.toString());
            } else {
                return text(GSON.toJson(object), ContentType.JSON);
            }
        }
    }

    public static HttpResponse json(String json) {
        if (JSONUtil.isJSONValid(json)) {
            return text(json, ContentType.JSON);
        }
        throw new IllegalArgumentException("Invalid json " + json);
    }

    public static HttpResponse xml(String xml) {
        return text(xml, ContentType.XML);
    }

    public static HttpResponse xslt(Map<String, Serializable> map, String xslFileName) {
        XStream stream = new XStream();
        stream.registerConverter(new XsltConverter());
        stream.alias("root", Map.class);
        return xslt(stream.toXML(map), xslFileName);
    }

    public static HttpResponse xslt(String xml, String xslFileName) {
        try (StringReader reader = new StringReader(xml); StringWriter writer = new StringWriter()) {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer(
                    new StreamSource(Thread.currentThread().getContextClassLoader().getResourceAsStream(xslFileName)));
            transformer.transform(new StreamSource(reader), new StreamResult(writer));
            String html = writer.toString();
            return text(html, ContentType.HTML);
        } catch (TransformerException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static HttpResponse html(String content) {
        return text(content, ContentType.HTML);
    }

    public static HttpResponse text(String content) {
        return text(content, ContentType.PLAIN);
    }

    public static HttpResponse text(Object content) {
        return text(content.toString());
    }

    private static HttpResponse text(String content, String contentType) {
        if (content == null) {
            throw new IllegalArgumentException();
        }
        byte[] bytes = content.getBytes(MAIN_ENCODING);
        return new HttpResponse(ResponseCode.OK, contentType, bytes, null, bytes.length);
    }

    public static HttpResponse text(Object content, ContentType contentType) {
        return text(content.toString(), contentType.toString());
    }

    public static HttpResponse code(ResponseCode code) {
        return text(code.getText(), ContentType.PLAIN).setResponseCode(code);
    }

    public static HttpResponse noContent() {
        return text("", ContentType.PLAIN).setResponseCode(ResponseCode.NO_CONTENT);
    }

    public static HttpResponse ok() {
        return text("Ok", ContentType.PLAIN);
    }

    public static HttpResponse exception(Exception e) {
        String text = TextUtil.getStringFromException(e);
        return text(text, ContentType.PLAIN).setResponseCode(ResponseCode.INTERNAL_SERVER_ERROR);
    }

}
