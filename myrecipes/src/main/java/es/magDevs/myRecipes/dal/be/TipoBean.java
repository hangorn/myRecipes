package es.magDevs.myRecipes.dal.be;

public class TipoBean extends BasicBean {
	private String descripcion;
	
	public TipoBean() {
	}
	
	public TipoBean(TipoBean obj) {
		this.descripcion = obj.descripcion;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new TipoBean(this);
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
