/**
 * Copyright (c) 2014-2020, Javier Vaquero
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * required by applicable law or agreed to in writing, software
 * under the License is distributed on an "AS IS" BASIS,
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * the License for the specific language governing permissions and
 * under the License.
 */
package es.magDevs.myRecipes.dal.dao;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Clase abstracta con con logica comun para todos los DAOs que usen
 * {@link PreparedStatement}
 * 
 * @author javier.vaquero
 *
 */
public abstract class AbstractDAO {
	
	protected Connection con = null;
	
	final static Logger log = LoggerFactory.getLogger(AbstractDAO.class);
	
	public final static String SEPARADOR="#@#";

	public AbstractDAO(Connection connection) {
		this.con = connection;
	}

	/**
	 * Interfaz para procesar un {@link ResultSet}, obtener todos los datos de
	 * una tupla.
	 * 
	 * @author javier.vaquero
	 *
	 * @param <T>
	 *            tipo de dato generado al obtener los datos del
	 *            {@link ResultSet}
	 */
	public static interface ResultSetHandler<T> {
		/**
		 * Obtiene los datos de una tupla del {@link ResultSet} y los guarda en
		 * el bean del tipo indicado por {@link #T}
		 * 
		 * @param rs
		 *            {@link ResultSet} con la tupla de la que se deben obtener
		 *            los datos. El {@link ResultSet} ya esta en la posicion
		 *            necesaria, no cambiar la posicion del {@link ResultSet}
		 * @return bean con los datos extraidos del {@link ResultSet}
		 * @throws SQLException 
		 */
		T processResultSet(ResultSet rs) throws SQLException;
	}
	
	/**
	 * Cierra un {@link ResultSet} y un {@link PreparedStatement} si estan
	 * iniciados
	 * 
	 * @param rs
	 * @param stmt
	 */
	protected void close(ResultSet rs, Statement stmt) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (Exception e) {
			log.error("Error", e);
		}
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Exception e) {
			log.error("Error", e);
		}
	}
	
	/**
	 * Fija en el {@link PreparedStatement} el valor indicado en funcion de su tipo
	 * @param stmt statement SQL
	 * @param pos posicion en la que se fijara el valor dentro del statement
	 * @param value valor que se fijara
	 * @throws Exception 
	 */
	protected static void setStmtValue(PreparedStatement stmt, int pos, Object value) throws SQLException {
		if(value == null) {
			stmt.setObject(pos, null);
		} else if(value instanceof Integer) {
			stmt.setInt(pos, (Integer) value);
		} else if(value instanceof Long) {
			stmt.setLong(pos, (Long) value);
		} else if(value instanceof Double) {
			stmt.setDouble(pos, (Double) value);
		} else if(value instanceof String) {
			stmt.setString(pos, (String) value);
		} else if(value instanceof Blob) {
			stmt.setBlob(pos, (Blob) value);
		} else if(value instanceof Clob) {
			stmt.setClob(pos, (Clob) value);
		} else {
			throw new SQLException("Tipo de parametro no soportado: "+value.getClass());
		}
	}

	/**
	 * Rellena el {@link PreparedStatement} con los datos indicados
	 * 
	 * @param params
	 *            datos a introducir en los parametros del
	 *            {@link PreparedStatement}
	 * @param stmt
	 *            {@link PreparedStatement}
	 * @throws SQLException
	 */
	private static void fillStmt(List<Object> params, PreparedStatement stmt) throws SQLException {
		if(params != null) {
			int count = 1;
			for (Object param : params) {
				setStmtValue(stmt, count++, param);
			}
		}
	}
	
	/**
	 * Transforma los valores dados en una lista de objectos
	 * 
	 * @param params
	 *            valores a meter en la lista
	 * @return lista con los valores recibidos
	 */
	protected static List<Object> buildParams(Object... params) {
		return new ArrayList<Object>(Arrays.asList(params));
	}

	/*
     * ********************************************************************** *
     * SELECTS *
     * **********************************************************************
     */
	
	/**
	 * Ejecuta la SQL indicada con un {@link PreparedStatement} añadiendole los parametros indicados
	 * 
	 * @param sql
	 *            parametrizada a ejecutar
	 * @param params
	 *            parametros que se usaran en la consulta
	 * @param resultSetHandler
	 *            procesador que se ejecuta para cada tupla obtenida en la
	 *            consulta
	 * @return datos obtenidos en la consulta
	 */
	protected <T extends Object> List<T> runSelect(String sql, List<Object> params, ResultSetHandler<T> resultSetHandler) throws SQLException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<T> retorno = new ArrayList<>();
		
		try {
			log.debug("SQL: " + sql);
			log.debug("Parametros: "+StringUtils.join(params, ", "));
			// Creamos el prepared statement
			stmt = con.prepareStatement(sql);
			// Lo rellenamos
			fillStmt(params, stmt);
			// Lo ejecutamos
			rs = stmt.executeQuery();
			while (rs != null && rs.next()) {
				// Por cada tupla, que se encargue quien ha llamado de procesar los datos
				retorno.add(resultSetHandler.processResultSet(rs));
			}
			log.debug("Numero de resultados: " + retorno.size());
		} catch (SQLException e) {
			throw new SQLException("Error al ejecutar SQL:\n"+sql+"\nParametros: "+StringUtils.join(params, ", ")+"\n", e);
		} finally {
			close(rs, stmt);
		}
		return retorno;
	}
	
	/**
	 * Ejecuta la SQL indicada con un {@link PreparedStatement} añadiendole los
	 * parametros indicados. Devuelve el primer elemento obtenido en la
	 * consulta, o <code>null</code> si la consulta no devuelve resultados
	 * 
	 * @param sql
	 *            parametrizada a ejecutar
	 * @param params
	 *            parametros que se usaran en la consulta
	 * @param resultSetHandler
	 *            procesador que se ejecuta para cada tupla obtenida en la
	 *            consulta
	 * @return primer elemento obtenido en la consulta, o <code>null</code> si
	 *         la consulta no devuelve resultados
	 */
	protected <T extends Object> T runSelectOrNull(String sql, List<Object> params, ResultSetHandler<T> resultSetHandler) throws SQLException {
		List<T> retorno = runSelect(sql, params, resultSetHandler);
		return retorno.isEmpty() ? null : retorno.get(0);
	}
	
	/**
	 * Ejecuta la SQL indicada con un {@link PreparedStatement} añadiendole los
	 * parametros indicados. Se obtendra de la primera tupla, el primer dato,
	 * que debe ser un entero
	 * 
	 * @param sql
	 *            parametrizada a ejecutar
	 * @param params
	 *            parametros que se usaran en la consulta
	 * @return datos obtenidos en la consulta
	 */
	protected Integer runCountSelect(String sql, List<Object> params) throws SQLException {
		return runSelect(sql, params, new ResultSetHandler<Integer>() {
			@Override
			public Integer processResultSet(ResultSet rs) throws SQLException {
				return rs.getInt(1);
			}
		}).get(0);
	}
	
	/**
	 * Obtiene un campo sql concatenado.
	 * 
	 * @param field
	 *            Campo sql a concatenar
	 * @param order
	 *            Sentencia de ordenación sql, debe incluir el ORDER BY
	 * @param separador
	 *            cadena de texto que se introducira entre cada elemento
	 *            concatenado
	 * @return Devuelve la sentencia sql para concatenar un campo
	 */
	public static String getConcatField(String field, String order, String separador) {
		return "rtrim(xmlagg(xmlelement(e,"+field+",'"+separador+"').extract('//text()') "+order+").GetClobVal(),'"+separador+"')";
	}
	
	/**
	 * Obtiene un campo sql concatenado.
	 * 
	 * @param field
	 *            Campo sql a concatenar
	 * @param order
	 *            Sentencia de ordenación sql, debe incluir el ORDER BY
	 * @return Devuelve la sentencia sql para concatenar un campo
	 */
	public static String getConcatField(String field, String order) {
		return getConcatField(field, order, SEPARADOR);
	}
	
	/**
	 * Añade una condicion a la clausula WHERE de la SQL indicada. Si no se la
	 * pasa SQL añadira WHERE al principio, si existe alguna condicion en la
	 * SQL, se añadira un AND. Si el valor es <code>null</code> no añadira
	 * ninguna condicion. El operador (=,>,>=,<,<=,...) se podra configurar.
	 * 
	 * @param sql
	 *            setencia WHERE SQL a la que se añadira la condicion
	 * @param params
	 *            lista donde se guardara el valor del parametro, en caso de que
	 *            se añada la condicion. Asi se podra usar un
	 *            {@link PreparedStatement}
	 * @param operator
	 *            operador que se usara en la condicion (=,>,>=,<,<=,...)
	 * @param field
	 *            campo de la tabla
	 * @param value
	 *            valor de la condicion
	 * @return setencia WHERE SQL con la condicion
	 */
	protected static String addCondition(String sql, List<Object> params, String operator, String field, Object value) {
		String result = sql;
		if (value != null && StringUtils.isNotBlank(value.toString())) {
			if (StringUtils.isEmpty(sql)) {
				result = " WHERE ";
			} else {
				result += " AND ";
			}
			result += field + " " + operator + " ?";

			if (value instanceof String) {
				params.add(value.toString().trim());
		    } else{
		    	params.add(value);
		    }

		}
		return result;
	}
	
	protected static String addCondition(String sql, List<Object> params, String codition, Object... values) {
		String result = sql;
		if (StringUtils.isEmpty(sql)) {
			result = " WHERE ";
		} else {
			result += " AND ";
		}
		result += codition;
		for (Object value : values) {
			if (value instanceof String) {
				params.add(value.toString().trim());
			} else {
				params.add(value);
			}
		}
		return result;
	}
	
	/**
	 * Añade una condicion de igual a la clausula WHERE de la SQL indicada. Si
	 * no se la pasa SQL añadira WHERE al principio, si existe alguna condicion
	 * en la SQL, se añadira un AND. Si el valor es <code>null</code> no añadira
	 * ninguna condicion
	 * 
	 * @param sql
	 *            setencia WHERE SQL a la que se añadira la condicion
	 * @param params
	 *            lista donde se guardara el valor del parametro, en caso de que
	 *            se añada la condicion. Asi se podra usar un
	 *            {@link PreparedStatement}
	 * @param field
	 *            campo de la tabla
	 * @param value
	 *            valor de la condicion
	 * @return setencia WHERE SQL con la condicion
	 */
	protected static String addEqualCondition(String sql, List<Object> params, String field, Object value) {
		return addCondition(sql, params, "=", field, value);
	}
	
	/**
	 * Añade una condicion de distinto a la clausula WHERE de la SQL indicada. Si
	 * no se la pasa SQL añadira WHERE al principio, si existe alguna condicion
	 * en la SQL, se añadira un AND. Si el valor es <code>null</code> no añadira
	 * ninguna condicion
	 * 
	 * @param sql
	 *            setencia WHERE SQL a la que se añadira la condicion
	 * @param params
	 *            lista donde se guardara el valor del parametro, en caso de que
	 *            se añada la condicion. Asi se podra usar un
	 *            {@link PreparedStatement}
	 * @param field
	 *            campo de la tabla
	 * @param value
	 *            valor de la condicion
	 * @return setencia WHERE SQL con la condicion
	 */
	protected static String addDistinctCondition(String sql, List<Object> params, String field, Object value) {
		return addCondition(sql, params, "<>", field, value);
	}
	
	/**
	 * Añade una condicion de igual o distinto a la clausula WHERE de la SQL
	 * indicada. Si no se la pasa SQL añadira WHERE al principio, si existe
	 * alguna condicion en la SQL, se añadira un AND. Si el valor es
	 * <code>null</code> no añadira ninguna condicion
	 * 
	 * @param sql
	 *            setencia WHERE SQL a la que se añadira la condicion
	 * @param params
	 *            lista donde se guardara el valor del parametro, en caso de que
	 *            se añada la condicion. Asi se podra usar un
	 *            {@link PreparedStatement}
	 * @param field
	 *            campo de la tabla
	 * @param value
	 *            valor de la condicion
	 * @param distinct
	 *            indica si usar la condicion de distinto: <code>true</code>:
	 *            '<>', <code>false</code>: '='
	 * @return setencia WHERE SQL con la condicion
	 */
	protected static String addEqualOrDistinctCondition(String sql, List<Object> params, String field, Object value, boolean distinct) {
		return addCondition(sql, params, distinct ? "<>" : "=", field, value);
	}
	
	protected static String addInOrNotInCondition(boolean distinct, String sql, List<Object> params, String field, Object... value) {
		return addInCondition(distinct, sql, params, field, value);
	}
	
	
	/**
	 * Añade una condicion de LIKE '%{@code valor}%' a la clausula WHERE de la SQL indicada. Si no
	 * se la pasa SQL añadira WHERE al principio, si existe alguna condicion en
	 * la SQL, se añadira un AND. Si el valor es <code>null</code> no añadira
	 * ninguna condicion
	 * 
	 * @param sql
	 *            setencia WHERE SQL a la que se añadira la condicion
	 * @param params
	 *            lista donde se guardara el valor del parametro, en caso de que
	 *            se añada la condicion. Asi se podra usar un
	 *            {@link PreparedStatement}
	 * @param field
	 *            campo de la tabla
	 * @param value
	 *            valor de la condicion
	 * @return setencia WHERE SQL con la condicion
	 */
	protected static String addLikeCondition(String sql, List<Object> params, String field, Object value) {
		return addLikeCondition(sql, params, field, value, true, true);
	}
	
	/**
	 * Añade una condicion de LIKE '%{@code valor}%' a la clausula WHERE de la
	 * SQL indicada. Si no se la pasa SQL añadira WHERE al principio, si existe
	 * alguna condicion en la SQL, se añadira un AND. Si el valor es
	 * <code>null</code> no añadira ninguna condicion. Si asi se indica se
	 * añadira un comodin al final del valor, pero nunca al inicio
	 * 
	 * @param sql
	 *            setencia WHERE SQL a la que se añadira la condicion
	 * @param params
	 *            lista donde se guardara el valor del parametro, en caso de que
	 *            se añada la condicion. Asi se podra usar un
	 *            {@link PreparedStatement}
	 * @param field
	 *            campo de la tabla
	 * @param value
	 *            valor de la condicion
	 * @param endWildCard
	 *            si es <code>true</code>, se añadira el comodin '%' al final
	 *            del valor a buscar
	 * @return setencia WHERE SQL con la condicion
	 */
	protected static String addLikeCondition(String sql, List<Object> params, String field, Object value, boolean endWildCard) {
		return addLikeCondition(sql, params, field, value, false, endWildCard);
	}
	
	/**
	 * Añade una condicion de LIKE '{@code valor}' a la clausula WHERE de la SQL
	 * indicada. Si no se la pasa SQL añadira WHERE al principio, si existe
	 * alguna condicion en la SQL, se añadira un AND. Si el valor es
	 * <code>null</code> no añadira ninguna condicion. Si asi se indica segun
	 * los parametros se le añadiran comodines al valor
	 * 
	 * @param sql
	 *            setencia WHERE SQL a la que se añadira la condicion
	 * @param params
	 *            lista donde se guardara el valor del parametro, en caso de que
	 *            se añada la condicion. Asi se podra usar un
	 *            {@link PreparedStatement}
	 * @param field
	 *            campo de la tabla
	 * @param value
	 *            valor de la condicion
	 * @param iniWildCard
	 *            si es <code>true</code>, se añadira el comodin '%' al inicio
	 *            del valor a buscar
	 * @param endWildCard
	 *            si es <code>true</code>, se añadira el comodin '%' al final
	 *            del valor a buscar
	 * @return setencia WHERE SQL con la condicion
	 */
	protected static String addLikeCondition(String sql, List<Object> params, String field, Object value, boolean iniWildCard, boolean endWildCard) {
		if (value != null) {
			value = value.toString().toUpperCase().trim();
			if(StringUtils.isNotEmpty(value.toString())){
				if(iniWildCard) {
					value = "%" + value;
				}
				if(endWildCard) {
					value = value + "%";
				}
			}

		}
		return addCondition(sql, params, "LIKE", "UPPER(" + field + ")", value);
	}
	
	/**
	 * Añade una condicion de IN ({@code valor}) a la clausula WHERE de la SQL indicada. Si no
	 * se la pasa SQL añadira WHERE al principio, si existe alguna condicion en
	 * la SQL, se añadira un AND. Si el valor es <code>null</code> no añadira
	 * ninguna condicion
	 * 
	 * @param sql
	 *            setencia WHERE SQL a la que se añadira la condicion
	 * @param params
	 *            lista donde se guardara el valor del parametro, en caso de que
	 *            se añada la condicion. Asi se podra usar un
	 *            {@link PreparedStatement}
	 * @param field
	 *            campo de la tabla
	 * @param values
	 *            valores con el que se filtrara
	 * @return setencia WHERE SQL con la condicion
	 */
	protected static String addInCondition(boolean distinct,String sql, List<Object> params, String field, Object... values) {
		String result = sql;
		if (values != null && values.length != 0 && values[0] != null) {
			result = buildInCondition(sql, field, StringUtils.join(Collections.nCopies(values.length, "?"), ","), distinct);
			params.addAll(Arrays.asList(values));
		}
		return result;
	}
	
	protected static String addInCondition(String sql, List<Object> params, String field, Object... values) {
		return addInCondition(false ,sql, params,field,  values);
	}
	
	/**
	 * Añade una condicion de IN ({@code subselect}) a la clausula WHERE de la
	 * SQL indicada. Si no se la pasa SQL añadira WHERE al principio, si existe
	 * alguna condicion en la SQL, se añadira un AND. Si no se recibe valor, o
	 * si todos los valores son distintos de <code>null</code> se añadira la
	 * condicion
	 * 
	 * @param sql
	 *            setencia WHERE SQL a la que se añadira la condicion
	 * @param params
	 *            lista donde se guardara el valor del parametro, en caso de que
	 *            se añada la condicion. Asi se podra usar un
	 *            {@link PreparedStatement}
	 * @param field
	 *            campo de la tabla
	 * @param subselect
	 *            subselect que se introducira dentro de la condicion IN, se le
	 *            concatenara el valor del campo al final
	 * @param values
	 *            valores por los que se filtrara la subselect
	 * @return setencia WHERE SQL con la condicion
	 */
	protected static String addInCondition(String sql, List<Object> params, String field, String subselect, Object... values) {
		String result;
		if(values.length == 0) {
			result = buildInCondition(sql, field, subselect);
		} else {
			boolean isNull = false;
			for (int i = 0; i < values.length && !isNull; i++) {
				if(values[i] == null || StringUtils.isBlank(values[i].toString())) {
					isNull = true;
				}
			}
			if(!isNull) {
				result = buildInCondition(sql, field, subselect);
				params.addAll(Arrays.asList(values));
			} else {
				result = sql;
			}
		}
		return result;
	}
	
	/**
	 * Construye una sentencia SQL IN y la añade a la clausula WHERE de la SQL
	 * indicada.
	 * 
	 * @param sql
	 *            setencia WHERE SQL a la que se añadira la condicion
	 * @param field
	 *            campo de la tabla
	 * @param in
	 *            valor que se metera dentro de los parentesis
	 * @return setencia WHERE SQL con la condicion
	 */
	private static String buildInCondition(String sql, String field, String in, boolean distinct) {
		String result = sql;
		if (StringUtils.isEmpty(sql)) {
			result = " WHERE ";
		} else {
			result += " AND ";
		}
		if(distinct) {
			result += field + " not IN ("+in+") ";
		}else {
			result += field + " IN ("+in+") ";
		}
		
		return result;
	}
	
	private static String buildInCondition(String sql, String field, String in) {
		return buildInCondition(sql, field, in, false);
	}
	
	/**
	 * Añade una condicion de IS NULL a la clausula WHERE de la SQL indicada. Si no
	 * se la pasa SQL añadira WHERE al principio, si existe alguna condicion en
	 * la SQL, se añadira un AND.
	 * 
	 * @param sql
	 *            setencia WHERE SQL a la que se añadira la condicion
	 * @param field
	 *            campo de la tabla
	 * @return setencia WHERE SQL con la condicion
	 */
	protected static String addIsNullCondition(String sql, String field) {
		String result = sql;
		if (StringUtils.isEmpty(sql)) {
			result = " WHERE ";
		} else {
			result += " AND ";
		}
		result += field + " IS NULL";
		return result;
	}
	
	/**
	 * Añade una condicion de LIKE '%{@code valor}%' para un CLOB a la clausula
	 * WHERE de la SQL indicada. Si no se le pasa SQL, añadira WHERE al
	 * principio, si existe alguna condicion en la SQL, se añadira un AND. Si el
	 * valor es <code>null</code> no añadira ninguna condicion
	 * 
	 * @param sql
	 *            setencia WHERE SQL a la que se añadira la condicion
	 * @param params
	 *            lista donde se guardara el valor del parametro, en caso de que
	 *            se añada la condicion. Asi se podra usar un
	 *            {@link PreparedStatement}
	 * @param field
	 *            campo de la tabla
	 * @param value
	 *            valor de la condicion
	 * @param size
	 *            numero de carateres en los que se buscara dentro del CLOB
	 * @param index
	 *            indice desde donde se empezara a buscar dentro del CLOB
	 * @return setencia WHERE SQL con la condicion
	 */
	protected static String addClobCondition(String sql, List<Object> params, String field, String value, long size, long index) {
		String result = sql;
		if (value != null && StringUtils.isNotBlank(value.toString())) {
			if (StringUtils.isEmpty(sql)) {
				result = " WHERE ";
			} else {
				result += " AND ";
			}
			result += "cast(DBMS_LOB.SUBSTR(" + field + ",?,?) as varchar2("+size+")) like ?";
			params.add(size);
			params.add(index);
			params.add("%" + value + "%");
		}
		return result;
	}
	
	/**
	 * Añade una condicion de LIKE '%{@code valor}%' para un CLOB a la clausula
	 * WHERE de la SQL indicada. Si no se le pasa SQL, añadira WHERE al
	 * principio, si existe alguna condicion en la SQL, se añadira un AND. Si el
	 * valor es <code>null</code> no añadira ninguna condicion
	 * 
	 * @param sql
	 *            setencia WHERE SQL a la que se añadira la condicion
	 * @param params
	 *            lista donde se guardara el valor del parametro, en caso de que
	 *            se añada la condicion. Asi se podra usar un
	 *            {@link PreparedStatement}
	 * @param field
	 *            campo de la tabla
	 * @param value
	 *            valor de la condicion
	 * @return setencia WHERE SQL con la condicion
	 */
	protected static String addClobCondition(String sql, List<Object> params, String field, String value) {
		return addClobCondition(sql, params, field, value, 4000, 1);
	}
	
	/**
	 * Añade paginacion a la SQL indicada, añadiendo las condiciones necesarias.
	 * 
	 * @param sql
	 *            parametrizada a paginar
	 * @param params
	 *            lista donde se guardara el valor del parametro, en caso de que
	 *            se añada la condicion. Asi se podra usar un
	 *            {@link PreparedStatement}
	 * @param pagina
	 *            numero de pagina, empezando en 1.
	 * @param tamaño
	 *            numero de elementos por pagina
	 * @return SQL paginada
	 */
	protected static String addPagination(String sql, List<Object> params, Integer pagina, Integer tamano) {
		String sqlSelect = "SELECT * FROM (SELECT A.*, ROWNUM RNUM FROM (";
		String sqlWhereRowmax = " ) A WHERE ROWNUM < ?";
		String sqlWhereRowmin = " ) WHERE RNUM >= ?";
		params.add(new Integer((pagina * tamano) + 1));
		params.add(new Integer((pagina - 1) * tamano + 1));
		return sqlSelect + sql + sqlWhereRowmax + sqlWhereRowmin;
	}
	
	/**
	 * Añade paginacion a la SQL indicada, añadiendo las condiciones necesarias.
	 * 
	 * @param sql
	 *            parametrizada a paginar
	 * @param params
	 *            lista donde se guardara el valor del parametro, en caso de que
	 *            se añada la condicion. Asi se podra usar un
	 *            {@link PreparedStatement}
	 * @param inicio
	 *            posicion/indice del primer elemento que se quiere obtener
	 *            (empezando en 1: 1 como primer elemento, 2 para el segundo).
	 * @param tamano
	 *             numero de elementos que se quieren obtener
	 * @return SQL paginada
	 */
	protected static String addLimits(String sql, List<Object> params, Integer inicio, Integer tamano) {
		String sqlSelect = "SELECT * FROM (SELECT A.*, ROWNUM RNUM FROM (";
		String sqlWhereRowmax = " ) A WHERE ROWNUM < ?";
		String sqlWhereRowmin = " ) WHERE RNUM >= ?";
		params.add(new Integer(inicio + tamano));
		params.add(new Integer(inicio));
		return sqlSelect + sql + sqlWhereRowmax + sqlWhereRowmin;
	}
	
	/**
	 * Obtiene del {@link ResultSet} el valor del entero en la posicion
	 * indicada. Tiene en cuenta que puede ser <code>null</code>.
	 * 
	 * @param rs
	 *            {@link ResultSet} del que se obtendra el dato
	 * @param position
	 *            posicion del campo
	 * @return el valor entero o <code>null</code>
	 * @throws SQLException 
	 */
	protected static Integer getInt(ResultSet rs, int position) throws SQLException {
		Integer result = rs.getInt(position);
		if(rs.wasNull()) {
			result = null;
		}
		return result;
	}
	
	/**
	 * Obtiene del {@link ResultSet} el valor del entero en la posicion
	 * indicada. Tiene en cuenta que puede ser <code>null</code>.
	 * 
	 * @param rs
	 *            {@link ResultSet} del que se obtendra el dato
	 * @param position
	 *            posicion del campo
	 * @return el valor entero o <code>null</code>
	 * @throws SQLException 
	 */
	protected static Integer getInt(ResultSet rs, String position) throws SQLException {
		Integer result = rs.getInt(position);
		if(rs.wasNull()) {
			result = null;
		}
		return result;
	}
	
	/**
	 * Obtiene del {@link ResultSet} el valor del entero en la posicion
	 * indicada. Tiene en cuenta que puede ser <code>null</code>.
	 * 
	 * @param rs
	 *            {@link ResultSet} del que se obtendra el dato
	 * @param position
	 *            posicion del campo
	 * @return el valor entero o <code>null</code>
	 * @throws SQLException 
	 */
	protected static Long getLong(ResultSet rs, int position) throws SQLException {
		Long result = rs.getLong(position);
		if(rs.wasNull()) {
			result = null;
		}
		return result;
	}
	
	/**
	 * Obtiene del {@link ResultSet} el valor del entero en la posicion
	 * indicada. Tiene en cuenta que puede ser <code>null</code>.
	 * 
	 * @param rs
	 *            {@link ResultSet} del que se obtendra el dato
	 * @param position
	 *            posicion del campo
	 * @return el valor entero o <code>null</code>
	 * @throws SQLException 
	 */
	protected static Double getDouble(ResultSet rs, int position) throws SQLException {
		Double result = rs.getDouble(position);
		if(rs.wasNull()) {
			result = null;
		}
		return result;
	}
	
	/**
	 * Obtiene del {@link ResultSet} el valor del CLOB en la posicion
	 * indicada. Tiene en cuenta que puede ser <code>null</code>.
	 * 
	 * @param rs
	 *            {@link ResultSet} del que se obtendra el dato
	 * @param position
	 *            posicion del campo
	 * @return el valor en bytes o <code>null</code>
	 * @throws SQLException 
	 */
	protected byte[] getClob(ResultSet rs, int position) throws SQLException {
		Reader xml = rs.getCharacterStream(position);
		byte[] result = null;
		if(xml != null) {
			try {
				result = IOUtils.toByteArray(xml, "UTF-8");
			} catch (IOException e) {
				throw new SQLException("Error al obtener los bytes de un CLOB", e);
			}
		}
		return result;
	}
	
	/*
     * ********************************************************************** *
     * INSERT, UPDATE o DELETE *
     * **********************************************************************
     */
	
	/**
	 * Ejecuta el INSERT, UPDATE o DELETE indicado con un {@link PreparedStatement}
	 * añadiendole los indicados
	 * 
	 * @param sql       parametrizada a ejecutar
	 * @param params    parametros que se usaran en la orden, si se recibe mas de
	 *                  una lista de parametros, se ejecuta la misma orden para cada
	 *                  conjunto de parametros
	 * @param psHandler interfaz para procesar un {@link PreparedStatement} despues
	 *                  de ejecutar el insert/update/delete. Puede ser
	 *                  <code>null</code> si no se quiere hacer nada.
	 * @return numero de datos afectados
	 */
 	protected int runMultipleUpdate(String sql, List<List<Object>> params, Consumer<PreparedStatement> psHandler) throws Exception {
		PreparedStatement stmt = null;
		int retorno = 0;
		String parametros = "";
		try {
			log.debug("SQL: " + sql);
			// Creamos el prepared statement
			if (psHandler != null) {
				stmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			} else {
				stmt = con.prepareStatement(sql);
			}
			for (List<Object> param : params) {
				log.debug("Parametros: "+(parametros =StringUtils.join(param, ", ")));
				// Lo rellenamos
				fillStmt(param, stmt);
				// Lo ejecutamos
				retorno = stmt.executeUpdate();
				log.debug("Numero de modificaciones: " + retorno);
				if (psHandler != null) {
					psHandler.accept(stmt);
				}
			}
		} catch (Exception e) {
			String message = "Error al ejecutar SQL:\n"+sql+"\nParametros: "+parametros+"\n";
			if (e instanceof SQLIntegrityConstraintViolationException) {
				throw e;
			}
			throw new Exception(message, e);
		} finally {
			close(null, stmt);
		}
		return retorno;
	}
	
	/**
	 * Ejecuta el INSERT, UPDATE o DELETE indicado con un
	 * {@link PreparedStatement} añadiendole los indicados
	 * 
	 * @param sql
	 *            parametrizada a ejecutar
	 * @param params
	 *            parametros que se usaran en la orden
	 * @return numero de datos afectados
	 */
	protected int runUpdate(String sql, List<Object> params, Consumer<PreparedStatement> psHandler) throws Exception {
		return runMultipleUpdate(sql, Arrays.asList(params), psHandler);
	}
	
	/**
	 * Realiza un insert en la tabla indicada de los valores suministrados
	 * 
	 * @param table
	 *            nombre de la tabla
	 * @param fields
	 *            campos de la tabla que se actualizaran
	 * @param values
	 *            valores que se insertaran
	 * @return <code>true</code> si se ha realizado el insert correctamente
	 * @throws Exception
	 */
	protected Long insert(String table, List<String> fields, List<Object> values) throws Exception {
		String sql = buildInsert(table, fields);
		List<Long> id = new ArrayList<Long>(1);
		runUpdate(sql, values, ps->{
			ResultSet rs;
			try {
				rs = ps.getGeneratedKeys();
				if(rs!=null &&  rs.next()) {
					id.add(rs.getLong(1));
				}
			} catch (SQLException e) {
				log.error("",e);
			}
		});
		if (id.isEmpty()) {
			throw new Exception("Error, no se ha podido recuperar el ID generado al realizar insert SQL:\n"+sql+"\nParametros: "+StringUtils.join(values, ", ")+"\n");
		}
		return id.get(0);
	}
	
	/**
	 * Realiza un update en la tabla indicada de los valores suministrados, para
	 * las tuplas que cumplan las condiciones que se indiquen
	 * 
	 * @param table
	 *            nombre de la tabla
	 * @param fields
	 *            campos de la tabla que se actualizaran
	 * @param values
	 *            valores que se usaran tanto en los datos a actualizar como en
	 *            las condiciones
	 * @param conditions
	 *            campos con los que se evaluaran las condiciones para hacer el
	 *            update. Si no contiene '?' se usara una condicion '='. Se
	 *            contiene algun '?' se usara tal cual. Todas las condiciones se
	 *            uniran con operadores AND
	 * @return <code>true</code> si se ha realizado el insert correctamente
	 * @throws Exception
	 */
	protected boolean update(String table, List<String> fields, List<String> conditions, List<Object> values) throws Exception {
		String sql = buildUpdate(table, fields, conditions);
		return runUpdate(sql, values, null) != -1;
	}
	
	/**
	 * Realiza un update en la tabla indicada de los valores suministrados, para
	 * las tuplas que cumplan las condiciones que se indiquen
	 * 
	 * @param table
	 *            nombre de la tabla
	 * @param field
	 *            campos de la tabla que se actualizaran
	 * @param values
	 *            valores que se usaran tanto en los datos a actualizar como en
	 *            las condiciones
	 * @param condition
	 *            campos con los que se evaluaran las condiciones para hacer el
	 *            update. Si no contiene '?' se usara una condicion '='. Se
	 *            contiene algun '?' se usara tal cual. Todas las condiciones se
	 *            uniran con operadores AND
	 * @return <code>true</code> si se ha realizado el insert correctamente
	 * @throws Exception
	 */
	protected boolean update(String table, String field, String condition, List<Object> values) throws Exception {
		return update(table, Arrays.asList(field), Arrays.asList(condition), values);
	}
	
	/**
	 * Realiza un update por id en la tabla indicada de los valores suministrados
	 * 
	 * @param table
	 *            nombre de la tabla
	 * @param fields
	 *            campos de la tabla que se actualizaran
	 * @param values
	 *            valores que se usaran tanto en los datos a actualizar como en
	 *            las condiciones
	 * @return <code>true</code> si se ha realizado el insert correctamente
	 * @throws Exception
	 */
	protected boolean update(String table, List<String> fields, List<Object> values) throws Exception {
		return update(table, fields, Arrays.asList("id"), values);
	}
	
	/**
	 * Realiza un delete en la tabla indicada para las tuplas que cumplan las
	 * condiciones que se indiquen
	 * 
	 * @param table
	 *            nombre de la tabla
	 * @param values
	 *            valores que se usaran en las condiciones
	 * @param conditions
	 *            campos con los que se evaluaran las condiciones para hacer el
	 *            update. Si no contiene '?' se usara una condicion '='. Se
	 *            contiene algun '?' se usara tal cual. Todas las condiciones se
	 *            uniran con operadores AND
	 * @return <code>true</code> si se ha realizado el insert correctamente
	 * @throws Exception
	 */
	protected boolean delete(String table, List<String> conditions, List<Object> values) throws Exception {
		String sql = buildDelete(table, conditions);
		return runUpdate(sql, values, null) != -1;
	}
	
	/**
	 * Realiza un delete en la tabla indicada para las tuplas que cumplan las
	 * condiciones que se indiquen
	 * 
	 * @param table
	 *            nombre de la tabla
	 * @param value
	 *            valores que se usaran en las condiciones
	 * @param condition
	 *            campos con los que se evaluaran las condiciones para hacer el
	 *            update. Si no contiene '?' se usara una condicion '='. Se
	 *            contiene algun '?' se usara tal cual. Todas las condiciones se
	 *            uniran con operadores AND
	 * @return <code>true</code> si se ha realizado el insert correctamente
	 * @throws Exception
	 */
	protected boolean delete(String table, String condition, Object value) throws Exception {
		return delete(table, Arrays.asList(condition), buildParams(value));
	}
	
	/**
	 * Realiza un delete en la tabla indicada para las tuplas que cumplan las
	 * condiciones que se indiquen
	 * 
	 * @param table
	 *            nombre de la tabla
	 * @param values
	 *            valores que se usaran en las condiciones
	 * @param conditions
	 *            campos con los que se evaluaran las condiciones para hacer el
	 *            update. Si no contiene '?' se usara una condicion '='. Se
	 *            contiene algun '?' se usara tal cual. Todas las condiciones se
	 *            uniran con operadores AND
	 * @return <code>true</code> si se ha realizado el insert correctamente
	 * @throws Exception
	 */
	protected boolean delete(String table, Long id) throws Exception {
		String sql = buildDelete(table, Arrays.asList("id"));
		return runUpdate(sql, buildParams(id), null) != -1;
	}
	
	/**
	 * Construye una setencia INSERT SQL segun los datos indicados
	 * 
	 * @param table
	 *            tabla en la que se hara el insert
	 * @param valueFields
	 *            campos de los que se insertaran datos
	 * @return cadena con el INSERT construido
	 */
	protected static String buildInsert(String table, Collection<String> valueFields) {
		return "INSERT INTO "+table+" (" +StringUtils.join(valueFields, ",")+")"
				+ " VALUES("+StringUtils.join(Collections.nCopies(valueFields.size(), "?"), ",")+")";
	}
	
	/**
	 * Construye una setencia UPDATE SQL segun los datos indicados
	 * 
	 * @param table
	 *            tabla en la que se hara el update
	 * @param valueFields
	 *            campos de los que se insertaran datos
	 * @param conditionFields
	 *            campos con los que se evaluaran las condiciones para hacer el
	 *            update. Si no contiene '?' se usara una condicion '='. Se
	 *            contiene algun '?' se usara tal cual. Todas las condiciones se
	 *            uniran con operadores AND
	 * @return cadena con el UPDATE construido
	 */
	protected static String buildUpdate(String table, Collection<String> valueFields, Collection<String> conditionFields) {
		return "UPDATE " + table + " SET " + StringUtils.join(valueFields, " = ?, ") + " = ? WHERE "+ buildConditions(conditionFields);
	}
	
	/**
	 * Construye una setencia DELETE SQL segun los datos indicados
	 * 
	 * @param table
	 *            tabla en la que se hara el delete
	 * @param conditionFields
	 *            campos con los que se evaluaran las condiciones para hacer el
	 *            delete. Si no contiene '?' se usara una condicion '='. Se
	 *            contiene algun '?' se usara tal cual. Todas las condiciones se
	 *            uniran con operadores AND
	 * @return cadena con el DELETE construido
	 */
	protected static String buildDelete(String table, Collection<String> conditionFields) {
		return "DELETE FROM " + table + " WHERE "+ buildConditions(conditionFields);
	}
	
	/**
	 * Construye las condiciones indicadas unidadas con operadores AND
	 * 
	 * @param conditionFields
	 *            campos con los que se evaluaran las condiciones para hacer el
	 *            update. Si no contiene '?' se usara una condicion '='. Se
	 *            contiene algun '?' se usara tal cual. Todas las condiciones se
	 *            uniran con operadores AND
	 * @return cadena con las condiciones
	 */
	private static String buildConditions(Collection<String> conditionFields) {
		StringBuilder condition = new StringBuilder();
		for (String field : conditionFields) {
			if(condition.length() != 0) {
				condition.append(" AND ");
			} else {
				condition.append(" ");
			}
			if(field.contains("?")) {
				condition.append(field);
			} else {
				condition.append(field).append("=?");
			}
		}
		return condition.toString();
	}

	/**
	 * Construye un objeto {@link Blob} de JDBC, y lo rellena con los bytes
	 * suministrados
	 * 
	 * @param bytes
	 *            con los que se rellenara el BLOB
	 * @return instancia de {@link Blob} rellenado con los bytes
	 * @throws SQLException
	 */
	protected Blob getBlob(byte[] bytes) throws SQLException {
		if (bytes == null) {
			return null;
		}
		Blob blob = con.createBlob();
		blob.setBytes(1, bytes);
		return blob;
	}

	/**
	 * Construye un objeto {@link Clob} de JDBC, y lo rellena con los bytes
	 * suministrados
	 * 
	 * @param bytes
	 *            con los que se rellenara el CLOB
	 * @return instancia de {@link Clob} rellenado con los bytes
	 * @throws SQLException
	 */
	protected Clob getClob(byte[] bytes) throws SQLException {
		if (bytes == null) {
			return null;
		}
		Clob clob = con.createClob();
		clob.setString(1, new String(bytes, Charset.forName("UTF-8")));
		return clob;
	}
}
