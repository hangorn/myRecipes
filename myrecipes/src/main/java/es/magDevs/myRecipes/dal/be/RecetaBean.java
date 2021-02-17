package es.magDevs.myRecipes.dal.be;

public class RecetaBean extends BasicBean {
	private String descripcion;
	private String foto;
	
	public RecetaBean() {
	}
	
	public RecetaBean(RecetaBean obj) {
		this.descripcion = obj.descripcion;
		this.foto = obj.foto;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new RecetaBean(this);
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}
}
