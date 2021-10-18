/**
 * Copyright (c) 2014-2015, Javier Vaquero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * required by applicable law or agreed to in writing, software
 * under the License is distributed on an "AS IS" BASIS,
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * the License for the specific language governing permissions and
 * under the License.
 */
package es.magDevs.myRecipes.ui.error;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import es.magDevs.myRecipes.dal.Constants;

public class MailManager {
	private static Logger log = Logger.getLogger(MailManager.class);
	
	private static MailDataSource mailDS;
	
	private static MailDataSource getDataSource() throws NamingException {
		if (mailDS == null) {
			Context iniContext = null;
			try {
				iniContext = new InitialContext();
				Context envContext = (Context) iniContext.lookup("java:comp/env");
				mailDS = (MailDataSource) envContext.lookup("MailDS");
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
		return mailDS;
	}

	public static void enviarMail(String subject, String body) throws IOException, AddressException, MessagingException, NamingException {
		getDataSource();
		if (mailDS == null) {
			return;
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.host", mailDS.getHost());
		props.put("mail.smtp.socketFactory.port", mailDS.getPort());
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", mailDS.getPort());
		props.put("mail.username", mailDS.getUsr());
		props.put("mail.password", mailDS.getPass());
		String username = mailDS.getUsr();
		String password = mailDS.getPass();
			
		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		session.setDebug(false);

		Message mail = new MimeMessage(session);

		mail.setFrom(new InternetAddress(username));

		mail.setRecipient(Message.RecipientType.TO, new InternetAddress(mailDS.getMailto()));

		mail.setSubject(subject);
		mail.setContent(body, "text/plain; charset=UTF-8");
		Transport.send(mail);
	}
	
	public static void enviarError(Throwable ex, String userAgent, String usuario, String ip) {
		if (Constants.isDebug) {
			return;
		}
		try {
			String cuerpo = componerFallo(ex, userAgent, usuario, ip);
			enviarMail("Informe de error en LaBiblioteca - " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), cuerpo);
		} catch (Exception e) {
			log.error("Error al enviar mensaje de error", e);
		}
	}
	
	private static String componerFallo(Throwable anError, String userAgent, String usuario, String ip) {
		StringBuilder tmpSb = new StringBuilder();
		
		// datos de contexto del error 
		// primero cabecera
		tmpSb.append("Se ha producido un error en La biblioteca.\n");
		tmpSb.append("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + "\n");
		
		try {
			InetAddress addr = InetAddress.getLocalHost();
			byte[] ipAddr = addr.getAddress();
			// Convertimos la dirección IP de bytes (con valor entre -128 y 127) a enteros sin signo (con valor entre 0 y 255)
			int[] ipDir = new int[ipAddr.length];
			for (int i = 0; i < ipAddr.length; i++) {
				ipDir[i] = ipAddr[i] & 0xFF;
			}
			tmpSb.append("Servidor: " + ipDir[0] + "." + ipDir[1] + "." + ipDir[2] + "." + ipDir[3]);
			tmpSb.append(" nombre-host: "+addr.getHostName());
		} catch (UnknownHostException e) {
			log.error("Error al obtener el nombre de la maquina para enviar un email de error", e);
		}
		
		// información del error
		tmpSb.append("\n\nINFORMACIÓN DEL ERROR:");
		tmpSb.append("\n\tTipo de excepción: " + anError.getClass());
		tmpSb.append("\n\tMensaje: " + anError.getLocalizedMessage());
		
		tmpSb.append("\n\tTraza: ");
		tmpSb.append(getStackTrace(anError));
		
		if (usuario == null) {
			usuario = "Usuario no autenticado";
		}
		if (ip == null) {
			ip = "desconocida"; 
		}
		
		tmpSb.append("\n\nINFORMACIÓN DEL USUARIO: ");
		tmpSb.append("\n\tUsuario: "+usuario);
		tmpSb.append("\n\tIP: "+ip);
		
		//navegador
		if (userAgent == null) {
			userAgent = "Sin informacion";
		}
		tmpSb.append("\n\tNavegador: "+userAgent);
		
		return tmpSb.toString().replaceAll("null", "");
	}

	public static String getStackTrace(Throwable anError) {
		StringBuilder tmpSb = new StringBuilder();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = null;
		try {
			ps = new PrintStream(os, false, StandardCharsets.UTF_8.name());
			anError.printStackTrace(ps);
			tmpSb.append("\n").append(new String(os.toByteArray(), StandardCharsets.UTF_8));
		} catch (UnsupportedEncodingException e) {
			for (StackTraceElement traza : anError.getStackTrace()) {
				tmpSb.append("\n\t" + traza.getClassName());
				tmpSb.append("[" + traza.getFileName() + "]: linea " + traza.getLineNumber());
				tmpSb.append(" método ---> " + traza.getMethodName());
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				} else {
					os.close();
				}
			} catch (IOException e) {
				log.error("Error al cerrar streams", e);
			}
		}
		return tmpSb.toString();
	}

}

