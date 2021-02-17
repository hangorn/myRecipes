package es.magDevs.myRecipes.dal.dao.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import es.magDevs.myRecipes.dal.be.IngredienteBean;
import es.magDevs.myRecipes.dal.dao.BasicDAO;

public class MyIngredientesDAO extends MyBasicDAO<IngredienteBean> implements BasicDAO<IngredienteBean> {
	private static final String TABLA = "ingredientes";
	private static final List<String> COLUMNAS = Arrays.asList("descripcion", "foto");
	
	public MyIngredientesDAO(Connection connection) {
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
	protected IngredienteBean getBeanFromRs(ResultSet rs) throws SQLException {
		IngredienteBean bean = new IngredienteBean();
		int i = 1;
		bean.setId(rs.getLong(i++));
		bean.setDescripcion(rs.getString(i++));
		bean.setFoto(rs.getString(i++));
		return bean;
	}

	@Override
	protected String getConditions(IngredienteBean filtro, List<Object> params) throws Exception {
		String where = addEqualCondition("", params, "id", filtro.getId());
		where = addLikeCondition(where, params, "descripcion", filtro.getDescripcion());
		return where;
	}

	@Override
	protected List<Object> getValoresFromBean(IngredienteBean bean) {
		return Arrays.asList(bean.getDescripcion(), bean.getFoto());
	}

	@Override
	protected IngredienteBean getNewBean() {
		return new IngredienteBean();
	}
}
