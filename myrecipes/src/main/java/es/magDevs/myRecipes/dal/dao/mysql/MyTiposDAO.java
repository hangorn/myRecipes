package es.magDevs.myRecipes.dal.dao.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import es.magDevs.myRecipes.dal.be.RecetaBean;
import es.magDevs.myRecipes.dal.dao.BasicDAO;

public class MyTiposDAO extends MyBasicDAO<RecetaBean> implements BasicDAO<RecetaBean> {
	private static final String TABLA = "recetas";
	private static final List<String> COLUMNAS = Arrays.asList("descripcion", "foto");
	
	public MyTiposDAO(Connection connection) {
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
	protected RecetaBean getBeanFromRs(ResultSet rs) throws SQLException {
		RecetaBean bean = new RecetaBean();
		int i = 1;
		bean.setId(rs.getLong(i++));
		bean.setDescripcion(rs.getString(i++));
		bean.setFoto(rs.getString(i++));
		return bean;
	}

	@Override
	protected String getConditions(RecetaBean filtro, List<Object> params) throws Exception {
		String where = addEqualCondition("", params, "id", filtro.getId());
		where = addLikeCondition(where, params, "descripcion", filtro.getDescripcion());
		return where;
	}

	@Override
	protected List<Object> getValoresFromBean(RecetaBean bean) {
		return Arrays.asList(bean.getDescripcion(), bean.getFoto());
	}

	@Override
	protected RecetaBean getNewBean() {
		return new RecetaBean();
	}
}
