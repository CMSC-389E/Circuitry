package cmsc389e.circuitry.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;

import com.google.common.collect.ImmutableMap;

/**
 * Modified version of Apache HttpClient Mime's MultipartFormEntity class to be
 * extremely minimal.<br>
 * <br>
 * <a
 * href=https://hc.apache.org/httpcomponents-client-4.3.x/httpmime/project-reports.html>Apache
 * HttpClient Mime</a>
 */
public class MultipartFormEntity implements HttpEntity {
    private static final ByteArrayBuffer CR_LF = encode("\r\n"), FIELD_SEP = encode(": "), TWO_DASHES = encode("--");

    private static ByteArrayBuffer encode(String string) {
	ByteBuffer encoded = Consts.ASCII.encode(CharBuffer.wrap(string));
	ByteArrayBuffer buffer = new ByteArrayBuffer(encoded.remaining());
	buffer.append(encoded.array(), encoded.position(), encoded.remaining());
	return buffer;
    }

    private static void writeBytes(ByteArrayBuffer buffer, OutputStream out) throws IOException {
	out.write(buffer.buffer(), 0, buffer.length());
    }

    private final String boundary;
    private final Header contentType;
    private final byte[] data;
    private final Map<String, String> header;
    private final long contentLength;

    public MultipartFormEntity(String name, String fileName, byte[] data) throws IOException {
	Random random = new Random();
	int count = random.nextInt(11) + 30;
	StringBuilder boundary = new StringBuilder();
	char[] multipartChars = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	for (int i = 0; i < count; i++)
	    boundary.append(multipartChars[random.nextInt(multipartChars.length)]);

	this.boundary = boundary.toString();
	contentType = new BasicHeader(HTTP.CONTENT_TYPE, "multipart/form-data; boundary=" + boundary);
	this.data = data;
	header = ImmutableMap.of("content-disposition",
		"form-data; name=\"" + name + "\"; filename=\"" + fileName + "\"", "content-type",
		ContentType.DEFAULT_BINARY.getMimeType(), "content-transfer-encoding", "binary");

	ByteArrayOutputStream out = new ByteArrayOutputStream();
	writeTo(out, false);
	contentLength = data.length + out.toByteArray().length;
    }

    @Deprecated
    @Override
    public void consumeContent() throws UnsupportedOperationException {
	if (isStreaming())
	    throw new UnsupportedOperationException("Streaming entity does not implement #consumeContent()");
    }

    @Override
    public InputStream getContent() {
	throw new UnsupportedOperationException("Multipart form entity does not implement #getContent()");
    }

    @Override
    public Header getContentEncoding() {
	return null;
    }

    @Override
    public long getContentLength() {
	return contentLength;
    }

    @Override
    public Header getContentType() {
	return contentType;
    }

    @Override
    public boolean isChunked() {
	return !isRepeatable();
    }

    @Override
    public boolean isRepeatable() {
	return contentLength != -1;
    }

    @Override
    public boolean isStreaming() {
	return !isRepeatable();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
	writeTo(out, true);
    }

    private void writeTo(OutputStream out, boolean write) throws IOException {
	ByteArrayBuffer boundary = encode(this.boundary);
	writeBytes(TWO_DASHES, out);
	writeBytes(boundary, out);
	writeBytes(CR_LF, out);
	for (Entry<String, String> entry : header.entrySet()) {
	    writeBytes(encode(entry.getKey()), out);
	    writeBytes(FIELD_SEP, out);
	    writeBytes(encode(entry.getValue()), out);
	    writeBytes(CR_LF, out);
	}
	writeBytes(CR_LF, out);
	if (write)
	    out.write(data);
	writeBytes(CR_LF, out);
	writeBytes(TWO_DASHES, out);
	writeBytes(boundary, out);
	writeBytes(TWO_DASHES, out);
	writeBytes(CR_LF, out);
    }
}