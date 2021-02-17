package es.magDevs.myRecipes.dal.be;

public class PasoRecetaBean extends BasicBean {
	private String descripcion;
	private Long receta;
	
	public PasoRecetaBean() {
	}
	
	public PasoRecetaBean(PasoRecetaBean obj) {
		this.descripcion = obj.descripcion;
		this.receta = obj.receta;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new PasoRecetaBean(this);
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Long getReceta() {
		return receta;
	}

	public void setReceta(Long receta) {
		this.receta = receta;
	}
}
