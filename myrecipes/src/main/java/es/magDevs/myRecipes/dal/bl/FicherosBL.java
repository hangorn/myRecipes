package es.magDevs.myRecipes.dal.bl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import elemental.json.Json;
import elemental.json.JsonObject;

public class FicherosBL {
	/*
	 * Codigos de error segun:
	 * https://global.download.synology.com/download/Document/Software/DeveloperGuide/Os/DSM/All/enu/DSM_Login_Web_API_Guide_enu.pdf
	 *  Code 	Description
	 *  100		Unknown error
	 *  101 	No parameter of API, method, or version
	 *  102 	The requested API does not exist
	 *  103 	The requested method does not exist
	 *  104 	The requested version does not support the functionality
	 *  105 	The logged in session does not have permission
	 *  106 	Session timeout
	 *  107 	Session interrupted by duplicate login
	 *  108 	Failed to upload the file.
	 *  109 	The network connection is unstable or the system is busy.
	 *  110 	The network connection is unstable or the system is busy.
	 *  111 	The network connection is unstable or the system is busy.
	 *  112 	Preserve for other purpose.
	 *  113 	Preserve for other purpose.
	 *  114 	Missing required parameters
	 *  115 	Not allowed to upload a file.
	 *  116 	Not allowed to perform for a demo site.
	 *  117 	Unknown internal error
	 *  118 	The network connection is unstable or the system is busy.
	 *  119		SID not found
	 *  120 	Invalid parameter
	 *  121-149 Preserve for other purpose.
	 *  150 	Request source IP does not match the login IP
	 *  160 	Insufficient application privilege
	 *  400		Invalid parameter of file operation
	 *  401		Unknown error of file operation
	 *  402		System is too busy
	 *  403		Invalid user does this file operation
	 *  404		Invalid group does this file operation
	 *  405		Invalid user and group does this file operation
	 *  406		Can't get user/group information from the account server
	 *  407		Operation not permitted
	 *  408		No such file or directory
	 *  409		Non-supported file system
	 *  410		Failed to connect internet-based file system (e.g., CIFS)
	 *  411		Read-only file system
	 *  412		Filename too long in the non-encrypted file system
	 *  413		Filename too long in the encrypted file system
	 *  414		File already exists
	 *  415		Disk quota exceeded
	 *  416		No space left on device
	 *  417		Input/output error
	 *  418		Illegal name or path
	 *  419		Illegal file name
	 *  420		Illegal file name on FAT file system
	 *  421		Device or resource busy
	 *  599		No such task of the file operation
	 */
	
	private static final String PARAM_API = "api";
	private static final String PARAM_API_INFO = "SYNO.API.Info";
	private static final String PARAM_API_AUTH = "SYNO.API.Auth";
	private static final String PARAM_API_DOWNLOAD = "SYNO.FileStation.Download";
	private static final String PARAM_API_UPLOAD = "SYNO.FileStation.Upload";
	
	private static final String PARAM_METHOD = "method";
	private static final String PARAM_METHOD_QUERY = "query";
	private static final String PARAM_METHOD_LOGIN = "login";
	private static final String PARAM_METHOD_DOWNLOAD = "download";
	private static final String PARAM_METHOD_UPLOAD = "upload";
	
	private static final String PARAM_MODE = "mode";
	private static final String PARAM_MODE_OPEN = "\"open\"";
	
	private static final String PARAM_VERSION = "version";

	private static final String PARAM_QUERY = "query";
	private static final String PARAM_ACCOUNT = "account";
	private static final String PARAM_PASSWD = "passwd";
	private static final String PARAM_SESSION = "session";
	private static final String PARAM_PATH = "path";
	private static final String PARAM_SID = "_sid";
	
	
	private final static Logger log = LoggerFactory.getLogger(FicherosBL.class);
	private static FicherosBL instance;
	
	private SynologyDataSource synDS;
	private String authUrl;
	private String downloadUrl;
	private String uploadUrl;
	private String authorizedSid;
	
	private interface BytesSupplier {
		byte[] get() throws Exception;
	}
	
	public static FicherosBL getInstance() throws Exception {
		if (instance == null) {
			instance = new FicherosBL();
		}
		return instance;
	}
	
	private FicherosBL() throws Exception {
		Context iniContext = null;
		
		try {
			iniContext = new InitialContext();
			Context envContext = (Context) iniContext.lookup("java:comp/env");
			synDS = (SynologyDataSource) envContext.lookup("SynologyDS");
			
			getConnectionInfo();
			login();
		} finally {
			// Liberamos los recursos de contexto utilizados para acceder por JNDI
			try {
				if (iniContext != null) {
					iniContext.close();
				}
			} catch (NamingException ne) {
				log.error("Error liberando contexto al obtener conexión", ne);
			}
		}
	}

	private byte[] httpRequest(String url, Collection<BasicHeader> headers, HttpEntity reqEntity, Pair<String, String>... params) throws IOException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			StringBuilder requestUrl = new StringBuilder(synDS.getUrl()).append(url);
			if (params != null && params.length != 0) {
				boolean first = true;
				for (Pair<String, String> pair : params) {
					requestUrl.append(first ? "?" : "&");
					first = false;
					requestUrl.append(pair.getKey()).append("=").append(URLEncoder.encode(pair.getValue(), StandardCharsets.UTF_8.displayName()));
				}
			}
			HttpRequestBase httpReq;
			if (reqEntity != null) {
				HttpPost httppost = new HttpPost(requestUrl.toString());
				httppost.setEntity(reqEntity);
				httpReq = httppost;
			} else {
				httpReq = new HttpGet(requestUrl.toString());
			}
			if (headers != null) {
				for (BasicHeader header : headers) {
					httpReq.setHeader(header);
				}
			}
			CloseableHttpResponse response = httpclient.execute(httpReq);
			if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
				return null;
			}
			return IOUtils.toByteArray(response.getEntity().getContent());
		} finally {
			httpclient.close();
		}
	}
	
	private byte[] httpRequest(String url, Pair<String, String>... params) throws IOException {
		return httpRequest(url, null, null, params);
	}
	
	private void getConnectionInfo() throws Exception {
		String jsonInfo = new String (httpRequest("/webapi/query.cgi",
				Pair.of(PARAM_API, PARAM_API_INFO),
				Pair.of(PARAM_VERSION, "1"),
				Pair.of(PARAM_METHOD, PARAM_METHOD_QUERY),
				Pair.of(PARAM_QUERY, PARAM_API_AUTH+","+PARAM_API_DOWNLOAD+","+PARAM_API_UPLOAD)), StandardCharsets.UTF_8);
		JsonObject json = Json.parse(jsonInfo);
		
		if (!json.getBoolean("success")) {
			throw new Exception("Consulta Synology sin existo. Respuesta:\n"+jsonInfo);
		}
		JsonObject data = json.getObject("data");
		authUrl = data.getObject(PARAM_API_AUTH).getString("path");
		downloadUrl = data.getObject(PARAM_API_DOWNLOAD).getString("path");
		uploadUrl = data.getObject(PARAM_API_UPLOAD).getString("path");
	}
	
	private void login() throws Exception {
		String jsonResp = new String (httpRequest("/webapi/"+authUrl,
				Pair.of(PARAM_API, PARAM_API_AUTH),
				Pair.of(PARAM_VERSION, "3"),
				Pair.of(PARAM_METHOD, PARAM_METHOD_LOGIN),
				Pair.of(PARAM_ACCOUNT, synDS.getUsr()),
				Pair.of(PARAM_PASSWD, synDS.getPass()),
				Pair.of(PARAM_SESSION, "FileStation")), StandardCharsets.UTF_8);
		JsonObject json = Json.parse(jsonResp);
		
		if (!json.getBoolean("success")) {
			throw new Exception("Login en Synology sin existo. Respuesta:\n"+jsonResp);
		}
		authorizedSid = json.getObject("data").getString("sid");
	}
	
	private byte[] reintentarTrasLogin(BytesSupplier orden) throws Exception {
		byte[] bytes = orden.get();
		
		if (bytes.length > 2 && bytes[0] == 123 && bytes[1] == 34 && bytes[bytes.length-1] == 125) {
			// Si la respuesta tiene mas de dos bytes y el primero es '{', el segundo '"' y el ultimo '}', intentamos sacar un JSON
			String jsonResp = new String (bytes);
			JsonObject json = Json.parse(jsonResp);
			if (!json.getBoolean("success")) {
				login();
				bytes = orden.get();
			}
		}
		
		return bytes;
	}
	
	public byte[] consultar(String ruta, String nombre) throws Exception {
		if (!ruta.endsWith("/")) {
			ruta += "/";
		}
		String fichero = "[\""+ruta+nombre+"\"]";
		return reintentarTrasLogin(()->httpRequest("/webapi/"+downloadUrl,
				Pair.of(PARAM_API, PARAM_API_DOWNLOAD),
				Pair.of(PARAM_VERSION, "2"),
				Pair.of(PARAM_METHOD, PARAM_METHOD_DOWNLOAD),
				Pair.of(PARAM_PATH, fichero),
				Pair.of(PARAM_SID, authorizedSid),
				Pair.of(PARAM_MODE, PARAM_MODE_OPEN)));
	}

	public void guardar(byte[] fichero, String nombre, String ruta) throws Exception {
		HttpEntity reqEntity = MultipartEntityBuilder.create()
				.addPart("overwrite", new StringBody("false", ContentType.TEXT_PLAIN))
				.addPart("path", new StringBody(ruta, ContentType.TEXT_PLAIN))
				.addPart("create_parents", new StringBody("true", ContentType.TEXT_PLAIN))
				.addPart("filename", new ByteArrayBody(fichero, nombre)).setLaxMode().build();
		
		byte[] bytes = reintentarTrasLogin(()->httpRequest("/webapi/"+uploadUrl, null, reqEntity,
				Pair.of(PARAM_API, PARAM_API_UPLOAD),
				Pair.of(PARAM_VERSION, "2"),
				Pair.of(PARAM_METHOD, PARAM_METHOD_UPLOAD),
				Pair.of(PARAM_SID, authorizedSid)));
		String jsonResp = new String (bytes, StandardCharsets.UTF_8);
		JsonObject json = Json.parse(jsonResp);
		
		if (!json.getBoolean("success")) {
			throw new Exception("No se ha podido guardar el fichero. Respuesta:\n"+jsonResp);
		}
	}
}
