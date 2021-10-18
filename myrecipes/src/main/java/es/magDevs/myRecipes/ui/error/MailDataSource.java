package es.magDevs.myRecipes.ui.error;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;

public class MailDataSource implements Referenceable {
	
	private String host;
	private String port;
	private String usr;
	private String pass;
	private String mailto;
	
	public MailDataSource(String host, String port, String usr, String pass, String mailto) {
		this.host = host;
		this.port = port;
		this.usr = usr;
		this.pass = pass;
		this.mailto = mailto;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getUsr() {
		return usr;
	}

	public String getPass() {
		return pass;
	}

	public String getMailto() {
		return mailto;
	}

	@Override
	public Reference getReference() throws NamingException {
		String factoryName = "es.magDevs.myRecipes.dal.bl.DataSourceFactory";
		Reference ref = new Reference(getClass().getName(), factoryName, null);
		ref.add(new StringRefAddr("host", host));
		ref.add(new StringRefAddr("port", port));
		ref.add(new StringRefAddr("usr", usr));
		ref.add(new StringRefAddr("pass", pass));
		ref.add(new StringRefAddr("mailto", mailto));
		return ref;
	}
}