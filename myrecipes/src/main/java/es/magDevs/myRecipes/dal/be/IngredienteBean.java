package es.magDevs.myRecipes.dal.be;

public class IngredienteBean extends BasicBean {
	private String descripcion;
	private String foto;
	
	public IngredienteBean() {
	}
	
	public IngredienteBean(IngredienteBean obj) {
		this.descripcion = obj.descripcion;
		this.foto = obj.foto;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new IngredienteBean(this);
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
