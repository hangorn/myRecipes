package es.magDevs.myRecipes.dal.dao.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import es.magDevs.myRecipes.dal.be.IngredienteRecetaBean;
import es.magDevs.myRecipes.dal.dao.BasicDAO;

public class MyIngredientesRecetasDAO extends MyBasicDAO<IngredienteRecetaBean> implements BasicDAO<IngredienteRecetaBean> {
	private static final String TABLA = "ingredientes_recetas";
	private static final List<String> COLUMNAS = Arrays.asList("ingrediente", "receta", "cantidad");
	
	public MyIngredientesRecetasDAO(Connection connection) {
		super(connection);
	}

	@Override
	protected String getTabla() {
		return TABLA;
	}

	@Override
	protected List<String> getColumnas() {
		return COLUMNAS;
	}

	@Override
	protected IngredienteRecetaBean getBeanFromRs(ResultSet rs) throws SQLException {
		IngredienteRecetaBean bean = new IngredienteRecetaBean();
		int i = 1;
		bean.setId(rs.getLong(i++));
		bean.setIngrediente(rs.getLong(i++));
		bean.setReceta(rs.getLong(i++));
		bean.setCantidad(rs.getString(i++));
		return bean;
	}

	@Override
	protected String getConditions(IngredienteRecetaBean filtro, List<Object> params) throws Exception {
		String where = addEqualCondition("", params, "id", filtro.getId());
		where = addEqualCondition(where, params, "ingrediente", filtro.getIngrediente());
		where = addEqualCondition(where, params, "receta", filtro.getReceta());
		return where;
	}

	@Override
	protected List<Object> getValoresFromBean(IngredienteRecetaBean bean) {
		return Arrays.asList(bean.getIngrediente(), bean.getReceta(), bean.getCantidad());
	}

	@Override
	protected IngredienteRecetaBean getNewBean() {
		return new IngredienteRecetaBean();
	}
}
