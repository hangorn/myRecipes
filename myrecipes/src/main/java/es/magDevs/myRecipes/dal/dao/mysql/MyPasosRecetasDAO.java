package es.magDevs.myRecipes.dal.dao.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import es.magDevs.myRecipes.dal.be.PasoRecetaBean;
import es.magDevs.myRecipes.dal.dao.BasicDAO;

public class MyPasosRecetasDAO extends MyBasicDAO<PasoRecetaBean> implements BasicDAO<PasoRecetaBean> {
	private static final String TABLA = "pasos_recetas";
	private static final List<String> COLUMNAS = Arrays.asList("descripcion", "receta");
	
	public MyPasosRecetasDAO(Connection connection) {
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
	protected PasoRecetaBean getBeanFromRs(ResultSet rs) throws SQLException {
		PasoRecetaBean bean = new PasoRecetaBean();
		int i = 1;
		bean.setId(rs.getLong(i++));
		bean.setDescripcion(rs.getString(i++));
		bean.setReceta(rs.getLong(i++));
		return bean;
	}

	@Override
	protected String getConditions(PasoRecetaBean filtro, List<Object> params) throws Exception {
		String where = addEqualCondition("", params, "id", filtro.getId());
		where = addLikeCondition(where, params, "descripcion", filtro.getDescripcion());
		where = addEqualCondition(where, params, "receta", filtro.getReceta());
		return where;
	}

	@Override
	protected List<Object> getValoresFromBean(PasoRecetaBean bean) {
		return Arrays.asList(bean.getDescripcion(), bean.getReceta());
	}

	@Override
	protected PasoRecetaBean getNewBean() {
		return new PasoRecetaBean();
	}
}
