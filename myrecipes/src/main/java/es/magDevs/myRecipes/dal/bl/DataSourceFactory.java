package es.magDevs.myRecipes.dal.bl;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import es.magDevs.myRecipes.ui.error.MailDataSource;

public class DataSourceFactory implements ObjectFactory {

	@Override
	public Object getObjectInstance(Object refObj, Name nm, Context ctx, Hashtable<?,?> env) throws Exception {
		Reference ref = (Reference) refObj;
		String className = ref.getClassName();
		
		if (SynologyDataSource.class.getName().equals(className)) {
			String url = nullSafeRefAddrStringGet("url", ref);
			String usr = nullSafeRefAddrStringGet("usr", ref);
			String pass = nullSafeRefAddrStringGet("pass", ref);
			return new SynologyDataSource(url, usr, pass);
		} else if (MailDataSource.class.getName().equals(className)) {
			String host = nullSafeRefAddrStringGet("host", ref);
			String port = nullSafeRefAddrStringGet("port", ref);
			String usr = nullSafeRefAddrStringGet("usr", ref);
			String pass = nullSafeRefAddrStringGet("pass", ref);
			String mailto = nullSafeRefAddrStringGet("mailto", ref);
			return new MailDataSource(host, port, usr, pass, mailto);
		}
		return null;
	}
	
	private String nullSafeRefAddrStringGet(String referenceName, Reference ref) {
		RefAddr refAddr = ref.get(referenceName);
		
		String asString = refAddr != null ? (String)refAddr.getContent() : null;
		
		return asString;
	}

}
