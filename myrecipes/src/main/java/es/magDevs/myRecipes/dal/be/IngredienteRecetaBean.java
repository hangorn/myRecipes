package es.magDevs.myRecipes.dal.be;

public class IngredienteRecetaBean extends BasicBean {
	private Long ingrediente;
	private Long receta;
	private String cantidad;
	
	public IngredienteRecetaBean() {
	}

	public IngredienteRecetaBean(IngredienteRecetaBean obj) {
		this.ingrediente = obj.ingrediente;
		this.receta = obj.receta;
		this.cantidad = obj.cantidad;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new IngredienteRecetaBean(this);
	}

	public Long getIngrediente() {
		return ingrediente;
	}

	public void setIngrediente(Long ingrediente) {
		this.ingrediente = ingrediente;
	}

	public Long getReceta() {
		return receta;
	}

	public void setReceta(Long receta) {
		this.receta = receta;
	}
	
	public String getCantidad() {
		return cantidad;
	}

	public void setCantidad(String cantidad) {
		this.cantidad = cantidad;
	}
}
