package es.magDevs.myRecipes.dal.bl;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;

public class SynologyDataSource implements Referenceable {
	private String url;
	private String usr;
	private String pass;
	
	public SynologyDataSource(String url, String usr, String pass) {
		this.url = url;
		this.usr = usr;
		this.pass = pass;
	}
	
	public String getUrl() {
		return url;
	}
	public String getUsr() {
		return usr;
	}
	public String getPass() {
		return pass;
	}

	@Override
	public Reference getReference() throws NamingException {
		String factoryName = "es.magDevs.myRecipes.dal.bl.SynologyDataSourceFactory";
		Reference ref = new Reference(getClass().getName(), factoryName, null);
		ref.add(new StringRefAddr("url", url));
		ref.add(new StringRefAddr("usr", usr));
		ref.add(new StringRefAddr("pass", pass));
		return ref;
	}
}