package vertx;



import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttTopicSubscription;
import io.vertx.mqtt.messages.MqttPublishMessage;
import io.vertx.ext.asyncsql.MySQLClient;


public class RestAP extends AbstractVerticle {
	
	
	private SQLClient mySQLClient;
	private static Multimap<String, MqttEndpoint> topicsCliente;
	
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
	router.get("/api/sesion").handler(this::getID);
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
	router.put("/api/UpdateSesion").handler(this::putUpdateSesion);
	router.get("/api/mqttboton1/").handler(this::getMqttPulsaBoton1);
	router.get("/api/mqttboton2/").handler(this::getMqttPulsaBoton2);
	router.get("/api/mqttboton3/").handler(this::getMqttPulsaBoton3);
	router.get("/api/mqttpotenciometro/").handler(this::getMqttCambiaPotenciometro);
	
	
	
	topicsCliente = HashMultimap.create();

	
	MqttServer mqttServer = MqttServer.create(vertx);
	init(mqttServer);
	MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
	
	MqttClient mqttClient2 = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
	mqttClient2.connect(1883, "192.168.43.145", s -> {

		mqttClient2.subscribe("topic_2", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
			if (handler.succeeded()) {
				
				System.out.println("Cliente " + mqttClient.clientId() + " suscrito correctamente al canal topic_2");
				
				mqttClient2.publishHandler(new Handler<MqttPublishMessage>() {
					@Override
					public void handle(MqttPublishMessage arg0) {
						
						System.out.println("Mensaje recibido por el cliente 2: " + arg0.payload().toString());
					}
				});
			}
		});

	});
}
	private static void init(MqttServer mqttServer) {
		mqttServer.endpointHandler(endpoint -> {
			
			System.out.println("Nuevo cliente MQTT [" + endpoint.clientIdentifier()
					+ "] solicitando suscribirse [Id de sesión: " + endpoint.isCleanSession() + "]");
			
			endpoint.accept(false);
			handleSubscription(endpoint);
			handleUnsubscription(endpoint);
			publishHandler(endpoint);
			handleClientDisconnect(endpoint);
		}).listen(ar -> {
			if (ar.succeeded()) {
				System.out.println("MQTT server está a la escucha por el puerto " + ar.result().actualPort());
			} else {
				System.out.println("Error desplegando el MQTT server");
				ar.cause().printStackTrace();
			}
		});
	}
	
	private static void handleSubscription(MqttEndpoint endpoint) {
		endpoint.subscribeHandler(subscribe -> {
			
			List<MqttQoS> grantedQosLevels = new ArrayList<>();
			for (MqttTopicSubscription s : subscribe.topicSubscriptions()) {
				System.out.println("Suscripción al topic " + s.topicName() + " con QoS " + s.qualityOfService());
				grantedQosLevels.add(s.qualityOfService());
				
				// Añadimos al cliente en la lista de clientes suscritos al topic
				topicsCliente.put(s.topicName(), endpoint);
			}
		
			
			endpoint.subscribeAcknowledge(subscribe.messageId(), grantedQosLevels);
		});
	}

	
	private static void handleUnsubscription(MqttEndpoint endpoint) {
		endpoint.unsubscribeHandler(unsubscribe -> {
			for (String t : unsubscribe.topics()) {
				
				topicsCliente.remove(t, endpoint);
				System.out.println("Eliminada la suscripción del topic " + t);
			}
			// Informamos al cliente que la desuscripción se ha realizado
			endpoint.unsubscribeAcknowledge(unsubscribe.messageId());
		});
	}
	
	private static void publishHandler(MqttEndpoint endpoint) {
		endpoint.publishHandler(message -> {
			
			handleMessage(message, endpoint);
		}).publishReleaseHandler(messageId -> {
			
			endpoint.publishComplete(messageId);
		});
	}

	private static void handleMessage(MqttPublishMessage message, MqttEndpoint endpoint) {
		System.out.println("Mensaje publicado por el cliente " + endpoint.clientIdentifier() + " en el topic "
				+ message.topicName());
		System.out.println("    Contenido del mensaje: " + message.payload().toString());
		
		
		System.out.println("Origen: " + endpoint.clientIdentifier());
		for (MqttEndpoint client: topicsCliente.get(message.topicName())) {
			System.out.println("Destino: " + client.clientIdentifier());
			if (!client.clientIdentifier().equals(endpoint.clientIdentifier()))
				client.publish(message.topicName(), message.payload(), message.qosLevel(), message.isDup(), message.isRetain());
		}
		
		if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
			String topicName = message.topicName();
			switch (topicName) {
			}
			endpoint.publishAcknowledge(message.messageId());
		} else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
			
			endpoint.publishRelease(message.messageId());
		}
	}

	
	private static void handleClientDisconnect(MqttEndpoint endpoint) {
		endpoint.disconnectHandler(h -> {
			
			Stream.of(topicsCliente.keySet())
				.filter(e -> topicsCliente.containsEntry(e, endpoint))
				.forEach(s -> topicsCliente.remove(s, endpoint));
			System.out.println("El cliente remoto se ha desconectado [" + endpoint.clientIdentifier() + "]");
		});
	}

	private void getMqttPulsaBoton1(RoutingContext routingContext) {
		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, "192.168.43.145", s -> {
			
			
			mqttClient.subscribe("topic_2", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
				if (handler.succeeded()) {
					
					System.out.println("Cliente " + mqttClient.clientId() + " suscrito correctamente al canal topic_2");
				}
			});
			mqttClient.publish("topic_2", Buffer.buffer("boton1"), MqttQoS.AT_LEAST_ONCE, false, false);
			routingContext.response().setStatusCode(200).end();
		});
	}
	
	private void getMqttPulsaBoton2(RoutingContext routingContext) {
		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, "192.168.43.145", s -> {
			
			
			mqttClient.subscribe("topic_2", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
				if (handler.succeeded()) {
					
					System.out.println("Cliente " + mqttClient.clientId() + " suscrito correctamente al canal topic_2");
				}
			});
			mqttClient.publish("topic_2", Buffer.buffer("boton2"), MqttQoS.AT_LEAST_ONCE, false, false);
			routingContext.response().setStatusCode(200).end();
		});
	}
	
	private void getMqttPulsaBoton3(RoutingContext routingContext) {
		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, "192.168.43.145", s -> {
			
			
			mqttClient.subscribe("topic_2", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
				if (handler.succeeded()) {
					
					System.out.println("Cliente " + mqttClient.clientId() + " suscrito correctamente al canal topic_2");
				}
			});
			mqttClient.publish("topic_2", Buffer.buffer("boton3"), MqttQoS.AT_LEAST_ONCE, false, false);
			routingContext.response().setStatusCode(200).end();
		});
	}
	
	private void getMqttCambiaPotenciometro(RoutingContext routingContext) {
		MqttClient mqttClient = MqttClient.create(vertx, new MqttClientOptions().setAutoKeepAlive(true));
		mqttClient.connect(1883, "192.168.43.145", s -> {
			
			
			mqttClient.subscribe("topic_2", MqttQoS.AT_LEAST_ONCE.value(), handler -> {
				if (handler.succeeded()) {
					
					System.out.println("Cliente " + mqttClient.clientId() + " suscrito correctamente al canal topic_2");
				}
			});
			mqttClient.publish("topic_2", Buffer.buffer("cambia,potenciometro"), MqttQoS.AT_LEAST_ONCE, false, false);
			routingContext.response().setStatusCode(200).end();
		});
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
	
	private void getID(RoutingContext routingContext) {
		try {
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "SELECT MAX(id) AS id "
							+ "FROM sesion; ";
					connection.query(
							query,
							res -> {
								if (res.succeeded()) {
									routingContext.response().end(
									Json.encodePrettily(res.result().getRows().get(0).getInteger("id")));
									
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
	
	
	
	private void putUpdateSesion(RoutingContext routingContext) {
		
		Sesion state = Json.decodeValue(routingContext.getBodyAsString(), Sesion.class);
	
			try {
			mySQLClient.getConnection(conn -> {
				if (conn.succeeded()) {
					SQLConnection connection = conn.result();
					String query = "UPDATE sesion SET tiempo_fin = ? WHERE id = ? ";
					JsonArray paramQuery = new JsonArray().add(state.getTiempo_fin())
							.add(state.getId())							
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
	

