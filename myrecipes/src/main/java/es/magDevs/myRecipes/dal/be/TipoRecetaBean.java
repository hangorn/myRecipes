package es.magDevs.myRecipes.dal.be;

public class TipoRecetaBean extends BasicBean {
	private Long tipo;
	private Long receta;
	
	public TipoRecetaBean() {
	}
	
	public TipoRecetaBean(TipoRecetaBean obj) {
		this.tipo = obj.tipo;
		this.receta = obj.receta;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new TipoRecetaBean(this);
	}

	public Long getTipo() {
		return tipo;
	}

	public void setTipo(Long descripcion) {
		this.tipo = descripcion;
	}

	public Long getReceta() {
		return receta;
	}

	public void setReceta(Long receta) {
		this.receta = receta;
	}
}
