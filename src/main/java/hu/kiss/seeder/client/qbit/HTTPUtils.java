package hu.kiss.seeder.client.qbit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HTTPUtils {
    private static Logger logger = LogManager.getLogger();
    private static final String BOUNDARY =  "*****";
    private static final String CRLF = "\r\n";
    private static final String TWOHYPHENS = "--";

    public static void postRequest(String url, String sessionId, Map<String, String> parameters){
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Cookie", "SID=" + sessionId);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, UTF_8));
            writer.write(getParamsAsString(parameters));
            writer.flush();
            writer.close();
            os.close();

            logger.debug(connection.getResponseCode()+" - "+ connection.getResponseMessage());
            connection.getInputStream();
        } catch (Exception e) {
            if (parameters == null || parameters.isEmpty()) {
//                throw new QBitPostException(url);
                e.printStackTrace();
            } else {
//                throw new QBitPostException(url, parameters);
                e.printStackTrace();
            }
        }
    }

    public static void postFileRequest(String url, String sessionId, File file, Map<String,String> options){
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Cookie", "SID=" + sessionId);
            connection.setRequestMethod("POST");
            connection.setRequestProperty(
                    "Content-Type", "multipart/form-data;boundary=\"" +BOUNDARY+"\"");

            connection.setDoInput(true);
            connection.setDoOutput(true);

            DataOutputStream request = new DataOutputStream(
                    connection.getOutputStream());


            Map<String,String> fields =Stream.of(
                    new AbstractMap.SimpleEntry<>("name","torrents"),
                    new AbstractMap.SimpleEntry<>("filename",file.getName())
            ).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));

            addContent(fields,FileUtils.readFileToByteArray(file),request);

            for (Map.Entry<String,String> e:options.entrySet()) {
                addBoundary(request,false);
                addContent(e.getKey(),e.getValue().getBytes(),request);
            }

            addBoundary(request,true);

            request.flush();
            request.close();

            //Read response
            InputStream responseStream = new
                    BufferedInputStream(connection.getInputStream());

            BufferedReader responseStreamReader =
                    new BufferedReader(new InputStreamReader(responseStream));

            String line = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((line = responseStreamReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            responseStreamReader.close();

            String response = stringBuilder.toString();

            logger.debug("Response:"+response);
            responseStream.close();
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void postFileRequest(String url, String sessionId, File file){
        postFileRequest(url,sessionId,file,Collections.emptyMap());
    }

    private static void addContent(Map<String,String> fields, byte[] data, DataOutputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder("content-disposition: form-data;");

        fields.forEach((key,value) ->{
            builder.append(key);
            builder.append("=\"");
            builder.append(value+"\";");
        });

        addBoundary(stream,false);
        stream.writeBytes(builder.toString()+CRLF);
        stream.writeBytes(CRLF);
        stream.write(data);
        stream.writeBytes(CRLF);

    }

    private static void addContent(String field, byte[] data, DataOutputStream stream) throws IOException {
//        request.writeBytes("Content-Disposition: form-data; name=\"category\""+ crlf);
//            request.writeBytes(crlf+"Filmek Atinak"+crlf);
        addBoundary(stream,false);
        stream.writeBytes("content-disposition: form-data; name=\""+field+"\"" + CRLF);
        stream.writeBytes(CRLF);
        stream.write(data);
        stream.writeBytes(CRLF);
    }

    private static void addBoundary(DataOutputStream stream, boolean last) throws IOException {
        stream.writeBytes(TWOHYPHENS + BOUNDARY);
        if(last){
            stream.writeBytes(TWOHYPHENS);
        }
        stream.writeBytes(CRLF);
    }
    private static String getParamsAsString(Map<String, String> parameters) {
        try {
            List<NameValuePair> paramsAsNameValuePairs = new LinkedList<NameValuePair>();
            if (parameters != null && !parameters.isEmpty()) {
                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    paramsAsNameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }

            return new URIBuilder().addParameters(paramsAsNameValuePairs).build().toString();
        } catch (Exception e) {
//            throw new QBitParametersException(parameters);
            e.printStackTrace();
        }
        return "";
    }

    public static <T extends Object> T getRequest(String url, String sessionId, TypeReference<T> type, boolean debug) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestProperty("Cookie", "SID=" + sessionId);

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            return mapper.readValue(connection.getInputStream(), type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
