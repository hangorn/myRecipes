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
