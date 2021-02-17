package es.magDevs.myRecipes.dal.dao.mysql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import es.magDevs.myRecipes.dal.be.TipoBean;
import es.magDevs.myRecipes.dal.dao.BasicDAO;

public class MyRecetasDAO extends MyBasicDAO<TipoBean> implements BasicDAO<TipoBean> {
	private static final String TABLA = "recetas";
	private static final List<String> COLUMNAS = Arrays.asList("descripcion");
	
	public MyRecetasDAO(Connection connection) {
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
	protected TipoBean getBeanFromRs(ResultSet rs) throws SQLException {
		TipoBean bean = new TipoBean();
		int i = 1;
		bean.setId(rs.getLong(i++));
		bean.setDescripcion(rs.getString(i++));
		return bean;
	}

	@Override
	protected String getConditions(TipoBean filtro, List<Object> params) throws Exception {
		String where = addEqualCondition("", params, "id", filtro.getId());
		where = addLikeCondition(where, params, "descripcion", filtro.getDescripcion());
		return where;
	}

	@Override
	protected List<Object> getValoresFromBean(TipoBean bean) {
		return Arrays.asList(bean.getDescripcion());
	}

	@Override
	protected TipoBean getNewBean() {
		return new TipoBean();
	}
}
