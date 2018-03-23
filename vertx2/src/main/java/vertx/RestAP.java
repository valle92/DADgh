package vertx;



import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.asyncsql.MySQLClient;


public class RestAP extends AbstractVerticle {
	
	
	private SQLClient mySQLClient;
	
	
	public void start (Future<Void> startFuture) {
		
		JsonObject mySQLClientConfig = new JsonObject().put("host","127.0.0.1")
				.put("port", 3306)
				.put("database", "dad")
				.put("username", "root")
				.put("password", "pass");
		
		mySQLClient = MySQLClient.createShared(vertx,  mySQLClientConfig);
		
		
		Router router = Router.router(vertx);
		
		
		vertx.createHttpServer().requestHandler(router::accept)
			.listen(8083, res -> {
				if(res.succeeded()) {
					System.out.println("Servidor desplegado");
				}else {
					System.out.println("Error: " + res.cause());
				}
			});
		
		
	router.route("/*").handler(BodyHandler.create()); 
	

	router.get("/api/potenciometro/:id").handler(this::getOnePotenciometro);
	router.get("/api/boton/:id").handler(this::getOneBoton);
	router.get("/api/maquina/:id").handler(this::getOneMaquina);
	router.get("/api/usuario/:id").handler(this::getOneUsuario);
	router.get("/api/sesion/:id").handler(this::getOneSesion);
	router.get("/api/potporsesion/:id").handler(this::getPotPorSesion);
	router.get("/api/botporsesion/:id").handler(this::getBotPorSesion);
	router.get("/api/sesionporusuario/:id").handler(this::getSesionPorUsuario);
	
	router.put("/api/potenciometro").handler(this::putOnePotenciometro); 
	router.put("/api/boton").handler(this::putOneBoton);
	router.put("/api/usuario").handler(this::putOneUsuario);
	router.put("/api/maquina").handler(this::putOneMaquina);
	router.put("/api/sesion").handler(this::putOneSesion);
	
	
	}
	
	//POTENCIOMETRO
	private void getOnePotenciometro(RoutingContext routingContext) {
		String paramStr = routingContext.request().getParam("id");
		if(paramStr != null) {
			try {
			int param = Integer.parseInt(paramStr);
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "SELECT id, id_potenciometro, marca_temporal, valor, sesion "
							+ "FROM potenciometro "
							+ "WHERE id = ?";
					JsonArray paramQuery = new JsonArray().add(param);
					connection.queryWithParams(
							query,
							paramQuery,
							res -> {
								if (res.succeeded()) {
									routingContext.response().end(
									Json.encodePrettily(res.result().getRows()));
									
								}else {									
									routingContext.response().setStatusCode(400).end("Error:" + res.cause());
								}
								
							});
										
				}else {
					routingContext.response().setStatusCode(400).end("Error:" + conn.cause());		
				}
			});
		}catch (ClassCastException e) {
			routingContext.response().setStatusCode(400).end();
		}
		}else {
			routingContext.response().setStatusCode(400).end();
		}
	}
	
	
	private void putOnePotenciometro(RoutingContext routingContext) {
		Potenciometro state = Json.decodeValue(routingContext.getBodyAsString(), Potenciometro.class);
	
			try {
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "INSERT INTO potenciometro (id, id_potenciometro, marca_temporal, valor, sesion) "
							+ "VALUES (?, ?, ?, ?, ?) ";
					JsonArray paramQuery = new JsonArray().add(state.getId())
							.add(state.getId_potenciometro())
							.add(state.getMarca_temporal())
							.add(state.getValor())
							.add(state.getSesion())
							;
					connection.queryWithParams(
							query,
							paramQuery,
							res -> {
								if (res.succeeded()) {
									routingContext.response().end(
									Json.encodePrettily(state));
									
								}else {									
									routingContext.response().setStatusCode(400).end("Error:" + res.cause());
								}
								
							});
										
				}else {
					routingContext.response().setStatusCode(400).end("Error:" + conn.cause());		
				}
			});
		}catch (ClassCastException e) {
			routingContext.response().setStatusCode(400).end();
		}
	}
	
	//BOTON
	private void getOneBoton(RoutingContext routingContext) {
		String paramStr = routingContext.request().getParam("id");
		if(paramStr != null) {
			try {
			int param = Integer.parseInt(paramStr);
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "SELECT id, id_boton, marca_temporal, valor, sesion "
							+ "FROM boton "
							+ "WHERE id = ?";
					JsonArray paramQuery = new JsonArray().add(param);
					connection.queryWithParams(
							query,
							paramQuery,
							res -> {
								if (res.succeeded()) {
									routingContext.response().end(
									Json.encodePrettily(res.result().getRows()));
									
								}else {									
									routingContext.response().setStatusCode(400).end("Error:" + res.cause());
								}
								
							});
										
				}else {
					routingContext.response().setStatusCode(400).end("Error:" + conn.cause());		
				}
			});
		}catch (ClassCastException e) {
			routingContext.response().setStatusCode(400).end();
		}
		}else {
			routingContext.response().setStatusCode(400).end();
		}
	}
	
	private void putOneBoton(RoutingContext routingContext) {
		Boton state = Json.decodeValue(routingContext.getBodyAsString(), Boton.class);
	
			try {
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "INSERT INTO boton (id, id_boton, marca_temporal, valor, sesion) "
							+ "VALUES (?, ?, ?, ?, ?) ";
					JsonArray paramQuery = new JsonArray().add(state.getId())
							.add(state.getId_boton())
							.add(state.getMarca_temporal())
							.add(state.getValor())
							.add(state.getSesion())
							;
					connection.queryWithParams(
							query,
							paramQuery,
							res -> {
								if (res.succeeded()) {
									routingContext.response().end(
									Json.encodePrettily(state));
									
								}else {									
									routingContext.response().setStatusCode(400).end("Error:" + res.cause());
								}
								
							});
										
				}else {
					routingContext.response().setStatusCode(400).end("Error:" + conn.cause());		
				}
			});
		}catch (ClassCastException e) {
			routingContext.response().setStatusCode(400).end();
		}
	}
	
	//MAQUINA 
	
	private void getOneMaquina(RoutingContext routingContext) {
		String paramStr = routingContext.request().getParam("id");
		if(paramStr != null) {
			try {
			int param = Integer.parseInt(paramStr);
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "SELECT id, alias "
							+ "FROM maquina "
							+ "WHERE id = ?";
					JsonArray paramQuery = new JsonArray().add(param);
					connection.queryWithParams(
							query,
							paramQuery,
							res -> {
								if (res.succeeded()) {
									routingContext.response().end(
									Json.encodePrettily(res.result().getRows()));
									
								}else {									
									routingContext.response().setStatusCode(400).end("Error:" + res.cause());
								}
								
							});
										
				}else {
					routingContext.response().setStatusCode(400).end("Error:" + conn.cause());		
				}
			});
		}catch (ClassCastException e) {
			routingContext.response().setStatusCode(400).end();
		}
		}else {
			routingContext.response().setStatusCode(400).end();
		}
	}
	
	private void putOneMaquina(RoutingContext routingContext) {
		Maquina state = Json.decodeValue(routingContext.getBodyAsString(), Maquina.class);
	
			try {
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "INSERT INTO maquina (id, alias) "
							+ "VALUES (?, ?) ";
					JsonArray paramQuery = new JsonArray().add(state.getId())
							.add(state.getAlias())
							;
					connection.queryWithParams(
							query,
							paramQuery,
							res -> {
								if (res.succeeded()) {
									routingContext.response().end(
									Json.encodePrettily(state));
									
								}else {									
									routingContext.response().setStatusCode(400).end("Error:" + res.cause());
								}
								
							});
										
				}else {
					routingContext.response().setStatusCode(400).end("Error:" + conn.cause());		
				}
			});
		}catch (ClassCastException e) {
			routingContext.response().setStatusCode(400).end();
		}
	}
	
	//USUARIO 
	
	private void getOneUsuario(RoutingContext routingContext) {
		String paramStr = routingContext.request().getParam("id");
		if(paramStr != null) {
			try {
			int param = Integer.parseInt(paramStr);
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "SELECT id, nombre, apellidos "
							+ "FROM usuario "
							+ "WHERE id = ?";
					JsonArray paramQuery = new JsonArray().add(param);
					connection.queryWithParams(
							query,
							paramQuery,
							res -> {
								if (res.succeeded()) {
									routingContext.response().end(
									Json.encodePrettily(res.result().getRows()));
									
								}else {									
									routingContext.response().setStatusCode(400).end("Error:" + res.cause());
								}
								
							});
										
				}else {
					routingContext.response().setStatusCode(400).end("Error:" + conn.cause());		
				}
			});
		}catch (ClassCastException e) {
			routingContext.response().setStatusCode(400).end();
		}
		}else {
			routingContext.response().setStatusCode(400).end();
		}
	}
	
	private void putOneUsuario(RoutingContext routingContext) {
		Usuario state = Json.decodeValue(routingContext.getBodyAsString(), Usuario.class);
	
			try {
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "INSERT INTO usuario (id, nombre, apellidos) "
							+ "VALUES (?, ?, ?) ";
					JsonArray paramQuery = new JsonArray().add(state.getId())
							.add(state.getNombre())
							.add(state.getApellidos())
							;
					connection.queryWithParams(
							query,
							paramQuery,
							res -> {
								if (res.succeeded()) {
									routingContext.response().end(
									Json.encodePrettily(state));
									
								}else {									
									routingContext.response().setStatusCode(400).end("Error:" + res.cause());
								}
								
							});
										
				}else {
					routingContext.response().setStatusCode(400).end("Error:" + conn.cause());		
				}
			});
		}catch (ClassCastException e) {
			routingContext.response().setStatusCode(400).end();
		}
	}
	//MAQUINA 
	
	private void getOneSesion(RoutingContext routingContext) {
		String paramStr = routingContext.request().getParam("id");
		if(paramStr != null) {
			try {
			int param = Integer.parseInt(paramStr);
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "SELECT id, tiempo_inicio, tiempo_fin, maquina, usuario "
							+ "FROM sesion "
							+ "WHERE id = ?";
					JsonArray paramQuery = new JsonArray().add(param);
					connection.queryWithParams(
							query,
							paramQuery,
							res -> {
								if (res.succeeded()) {
									routingContext.response().end(
									Json.encodePrettily(res.result().getRows()));
									
								}else {									
									routingContext.response().setStatusCode(400).end("Error:" + res.cause());
								}
								
							});
										
				}else {
					routingContext.response().setStatusCode(400).end("Error:" + conn.cause());		
				}
			});
		}catch (ClassCastException e) {
			routingContext.response().setStatusCode(400).end();
		}
		}else {
			routingContext.response().setStatusCode(400).end();
		}
	}
	
	private void putOneSesion(RoutingContext routingContext) {
		Sesion state = Json.decodeValue(routingContext.getBodyAsString(), Sesion.class);
	
			try {
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "INSERT INTO sesion (id, tiempo_inicio, tiempo_fin, maquina, usuario) "
							+ "VALUES (?, ?, ?, ?, ?) ";
					JsonArray paramQuery = new JsonArray().add(state.getId())
							.add(state.getTiempo_inicio())
							.add(state.getTiempo_fin())
							.add(state.getMaquina())
							.add(state.getUsuario())
							;
					connection.queryWithParams(
							query,
							paramQuery,
							res -> {
								if (res.succeeded()) {
									routingContext.response().end(
									Json.encodePrettily(state));
									
								}else {									
									routingContext.response().setStatusCode(400).end("Error:" + res.cause());
								}
								
							});
										
				}else {
					routingContext.response().setStatusCode(400).end("Error:" + conn.cause());		
				}
			});
		}catch (ClassCastException e) {
			routingContext.response().setStatusCode(400).end();
		}
	}
	private void getBotPorSesion(RoutingContext routingContext) {
		String paramStr = routingContext.request().getParam("id");
		if(paramStr != null) {
			try {
			int param = Integer.parseInt(paramStr);
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "SELECT id, id_boton, marca_temporal, valor "							
							+ "FROM boton "
							+ "WHERE sesion = ? "
							+ "ORDER BY marca_temporal";  
					JsonArray paramQuery = new JsonArray().add(param);
					connection.queryWithParams(
							query,
							paramQuery,
							res -> {
								if (res.succeeded()) {
									routingContext.response().end(
									Json.encodePrettily(res.result().getRows()));
									
								}else {									
									routingContext.response().setStatusCode(400).end("Error:" + res.cause());
								}
								
								
							});
										
				}else {
					routingContext.response().setStatusCode(400).end("Error:" + conn.cause());		
				}
			});
		}catch (ClassCastException e) {
			routingContext.response().setStatusCode(400).end();
		}
		}else {
			routingContext.response().setStatusCode(400).end();
		}
	}
	
	private void getPotPorSesion(RoutingContext routingContext) {
		String paramStr = routingContext.request().getParam("id");
		if(paramStr != null) {
			try {
			int param = Integer.parseInt(paramStr);
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "SELECT potenciometro.id, potenciometro.id_potenciometro, potenciometro.marca_temporal, potenciometro.valor "							+ "FROM potenciometro "
							+ "WHERE potenciometro.sesion = ? "
							+ "ORDER BY marca_temporal";  
					JsonArray paramQuery = new JsonArray().add(param);
					connection.queryWithParams(
							query,
							paramQuery,
							res -> {
								if (res.succeeded()) {
									routingContext.response().end(
									Json.encodePrettily(res.result().getRows()));
									
								}else {									
									routingContext.response().setStatusCode(400).end("Error:" + res.cause());
								}
								
								
							});
										
				}else {
					routingContext.response().setStatusCode(400).end("Error:" + conn.cause());		
				}
			});
		}catch (ClassCastException e) {
			routingContext.response().setStatusCode(400).end();
		}
		}else {
			routingContext.response().setStatusCode(400).end();
		}
	}
	
	private void getSesionPorUsuario(RoutingContext routingContext) {
		String paramStr = routingContext.request().getParam("id");
		if(paramStr != null) {
			try {
			int param = Integer.parseInt(paramStr);
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "SELECT id, tiempo_inicio, tiempo_fin, maquina, usuario "		
							+ "FROM sesion "
							+ "WHERE usuario = ? " ;  
					JsonArray paramQuery = new JsonArray().add(param);
					connection.queryWithParams(
							query,
							paramQuery,
							res -> {
								if (res.succeeded()) {
									routingContext.response().end(
									Json.encodePrettily(res.result().getRows()));
									
								}else {									
									routingContext.response().setStatusCode(400).end("Error:" + res.cause());
								}
								
								
							});
										
				}else {
					routingContext.response().setStatusCode(400).end("Error:" + conn.cause());		
				}
			});
		}catch (ClassCastException e) {
			routingContext.response().setStatusCode(400).end();
		}
		}else {
			routingContext.response().setStatusCode(400).end();
		}
	}
	
}
	

