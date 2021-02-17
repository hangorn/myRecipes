CREATE TABLE recetas (
	id INTEGER NOT NULL AUTO_INCREMENT,
	descripcion VARCHAR(50) NOT NULL,
	foto VARCHAR(50),
	CONSTRAINT pk_recetas PRIMARY KEY(id)
);

CREATE TABLE pasos_recetas (
	id INTEGER NOT NULL AUTO_INCREMENT,
	descripcion VARCHAR(100) NOT NULL,
	receta INTEGER NOT NULL,
	CONSTRAINT pk_pasos_recetas PRIMARY KEY(id),
	CONSTRAINT fk_pasos_recetas_receta FOREIGN KEY (receta) REFERENCES recetas(id)
);

CREATE TABLE tipos (
	id INTEGER NOT NULL AUTO_INCREMENT,
	descripcion VARCHAR(50) NOT NULL,
	CONSTRAINT pk_tipos PRIMARY KEY(id)
);

CREATE TABLE tipos_recetas (
	id INTEGER NOT NULL AUTO_INCREMENT,
	receta INTEGER NOT NULL,
	tipo INTEGER NOT NULL,
	CONSTRAINT pk_tipos_recetas PRIMARY KEY(id),
	CONSTRAINT fk_tipos_recetas_receta FOREIGN KEY (receta) REFERENCES recetas(id),
	CONSTRAINT fk_tipos_recetas_tipo FOREIGN KEY (tipo) REFERENCES tipos(id)
);

CREATE TABLE ingredientes (
	id INTEGER NOT NULL AUTO_INCREMENT,
	descripcion VARCHAR(50) NOT NULL,
	foto VARCHAR(50) NOT NULL,
	CONSTRAINT pk_ingredientes PRIMARY KEY(id)
);

CREATE TABLE ingredientes_recetas (
	id INTEGER NOT NULL AUTO_INCREMENT,
	receta INTEGER NOT NULL,
	cantidad VARCHAR(50) NOT NULL,
	ingrediente INTEGER NOT NULL,
	CONSTRAINT pk_ingredientes_recetas PRIMARY KEY(id),
	CONSTRAINT fk_ingredientes_recetas_receta FOREIGN KEY (receta) REFERENCES recetas(id),
	CONSTRAINT fk_ingredientes_recetas_ingrediente FOREIGN KEY (ingrediente) REFERENCES ingredientes(id)
);

