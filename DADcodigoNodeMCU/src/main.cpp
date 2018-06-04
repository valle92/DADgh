#include <Arduino.h>
#include <ArduinoJson.h>
#include <TimeLib.h>
#include <ESP8266WebServer.h>
#include <RestClient.h>
#include <PubSubClient.h>


//DATOS DE CONEXION WIFI
WiFiClient espClient;
PubSubClient pubsubClient(espClient);
char msg[50];
const char* ssid = "Valle";
const char* pass = "12345678";
#define IP "192.168.43.145" // Server IP
#define PORT 8083
RestClient client = RestClient(IP, PORT);

//DECLARACION BOTONES, ETC.
 int botonSesion = D0;
 int boton1 = D1;
 int boton2 = D2;
 int boton3 = D3;
int altavoz = D4;
int potenciometro = A0;
int contadorSesion = 0;
long valorPot;
long idSesion;

//DECLARACION DE FUNCIONES
void emitirSonido1();
void emitirSonido2();
void emitirSonido3();
void insertarBoton1();
void insertarBoton2();
void insertarBoton3();
void insertarPotenciometro();
void cerrarSesion();

//CALLLBACK
void callback(char* topic, byte* payload, unsigned int length) {
	Serial.print("Mensaje recibido [");
	Serial.print(topic);
	Serial.print("] ");
	String message = String((char *)payload);
	Serial.print(message);

		if(message = "boton1"){
			Serial.println("Botón 1 pulsado...");
				emitirSonido1();
				insertarBoton1();
		}else if(message = "boton2"){
			Serial.println("Botón 2 pulsado...");
				emitirSonido2();
				insertarBoton2();
		}else if(message = "boton3"){
			Serial.println("Botón 3 pulsado...");
				emitirSonido3();
				insertarBoton3();
		}
}


void setup() {
    Serial.begin(115200);
    delay(10);

 //CONFIGURACION BOTONES
		 pinMode(botonSesion, INPUT);
		 pinMode(boton1, INPUT);
		 pinMode(boton2, INPUT);
		 pinMode(boton3, INPUT);
		 pinMode(potenciometro, INPUT);
		 pinMode(altavoz, OUTPUT);
		 valorPot = analogRead(potenciometro);
    //CONEXION WIF
    Serial.println("Conectando a la red..");
    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid, pass);

    while(WiFi.status() != WL_CONNECTED){
    delay(1000);
    Serial.print(".");
     }
     Serial.println("ConexiÃ³n establecida");
     Serial.print("IP asignada: ");
     Serial.println(WiFi.localIP());

     pubsubClient.setServer(IP, 1883);
     pubsubClient.setCallback(callback);

		 Serial.println("Esperando inicio de sesión...");
}

void reconnect() {
	while (!pubsubClient.connected()) {
		Serial.print("Conectando al servidor MQTT");
		if (pubsubClient.connect("dadESP")) {
			Serial.println("Conectado");
			pubsubClient.subscribe("topic_2");
		} else {
			Serial.print("Error, rc=");
			Serial.print(pubsubClient.state());
			Serial.println(" Reintentando en 5 segundos");
			delay(5000);
		}
	}
}

void loop() {
//BOTON DE SESION PULSADO
    if(digitalRead(botonSesion) == HIGH ){
      Serial.println("Botón sesión pulsado");
      //INICIO DE SESION
          if(contadorSesion == 0 ){
                  Serial.println("Iniciando sesión...");
                  contadorSesion++;
                  String respuestaSesion = "";
                  //Creo Json de sesión
                  const int size_t_capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 120;
                  DynamicJsonBuffer jsonBuffer(size_t_capacity);
                  JsonObject& sesion = jsonBuffer.createObject();
                  time_t fechaSesionInicio;
                  fechaSesionInicio = now();
                  sesion["tiempo_inicio"] = fechaSesionInicio;
                  sesion["maquina"] = 1;
                  sesion["usuario"] = 1;
                  char jsonChar[200];
                  sesion.printTo(jsonChar);
                  //Insercción de la sesión en la base de datos
                  int statusSesion = client.put("/api/sesion", jsonChar, &respuestaSesion);
                  if( statusSesion == 200){
                    Serial.println("Sesión iniciada correctamente.");
                    //RECUPERAR ID
                    String respuestaID = "";
                    int statusID = client.get("/api/sesion",	&respuestaID);
                    Serial.print("Obteniendo ID ...");
                    Serial.println(statusID);
                    Serial.println(respuestaID);
                    if(statusID == 200){
                                Serial.println("El identificador de sesión es:");
                                idSesion = respuestaID.toInt();
                                Serial.println(idSesion);
                    }else{
                      Serial.println("No se ha podido recuperar el ID de la sesión.");
                      contadorSesion--;
                    }
                  }else{
                        Serial.println("No se ha podido iniciar sesión.");
                        Serial.println(statusSesion);
                        contadorSesion--;
                  }
                }else{
                cerrarSesion();
                }
          }

          if(digitalRead(boton1) == HIGH && contadorSesion == 1 ){
            Serial.println("Botón 1 pulsado...");
              emitirSonido1();
              insertarBoton1();
        }

        if(digitalRead(boton2) == HIGH && contadorSesion == 1 ){
          Serial.println("Botón 2 pulsado...");
            emitirSonido2();
            insertarBoton2();
      }

      if(digitalRead(boton3) == HIGH && contadorSesion == 1 ){
        Serial.println("Botón 3 pulsado...");
          emitirSonido3();
          insertarBoton3();
    }
    // MQTT
  if (!pubsubClient.connected()) {
    reconnect();
  }

	 pubsubClient.loop();


}

void insertarPotenciometro(){
  Serial.println("Guardando valor de potenciometro...");
  String respuestaPot = "";
  const int size_t_capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 120;
  DynamicJsonBuffer jsonBuffer(size_t_capacity);
  JsonObject& pot = jsonBuffer.createObject();
  pot["id_potenciometro"] = 1;
  time_t tpot = now();
  pot["marca_temporal"] = tpot;
  pot["valor"] = valorPot;
  pot["sesion"] = idSesion;
  char jsonChar[200];
  pot.printTo(jsonChar);
  //Insercción de la sesión en la base de datos
  int statusPot = client.put("/api/potenciometro", jsonChar, &respuestaPot);
  if( statusPot == 200){
  Serial.println("Cambio volumen guardado.");
    }else {
        Serial.println("Error potenciometro");
        }
  }

void insertarBoton1(){
  Serial.println("Guardando sonido...");
  String respuestaBoton1 = "";
  const int size_t_capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 120;
  DynamicJsonBuffer jsonBuffer(size_t_capacity);
  JsonObject& boton1 = jsonBuffer.createObject();
  boton1["id_boton"] = 1;
  time_t tbot = now();
  boton1["marca_temporal"] = tbot;
  boton1["valor"] = 1;
  boton1["sesion"] = idSesion;
  char jsonChar[200];
  boton1.printTo(jsonChar);
  int statusBoton1 = client.put("/api/boton", jsonChar, &respuestaBoton1);
  if( statusBoton1 == 200){
    Serial.println("Sonido guardado.");
  }  else   {
    Serial.println("No se ha podido registrar el sonido.");
  }
  }

  void insertarBoton2(){
    Serial.println("Guardando sonido...");
    String respuestaBoton2 = "";
    const int size_t_capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 120;
    DynamicJsonBuffer jsonBuffer(size_t_capacity);
    JsonObject& boton2 = jsonBuffer.createObject();
    boton2["id_boton"] = 2;
    time_t tbot = now();
    boton2["marca_temporal"] = tbot;
    boton2["valor"] = 1;
    boton2["sesion"] = idSesion;
    char jsonChar[200];
    boton2.printTo(jsonChar);
    int statusBoton2 = client.put("/api/boton", jsonChar, &respuestaBoton2);
    if( statusBoton2 == 200){
      Serial.println("Sonido guardado.");
    }  else   {
      Serial.println("No se ha podido registrar el sonido.");
    }
    }

    void insertarBoton3(){
      Serial.println("Guardando sonido...");
      String respuestaBoton3 = "";
      const int size_t_capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 120;
      DynamicJsonBuffer jsonBuffer(size_t_capacity);
      JsonObject& boton3 = jsonBuffer.createObject();
      boton3["id_boton"] = 3;
      time_t tbot = now();
      boton3["marca_temporal"] = tbot;
      boton3["valor"] = 1;
      boton3["sesion"] = idSesion;
      char jsonChar[200];
      boton3.printTo(jsonChar);
      int statusBoton3 = client.put("/api/boton", jsonChar, &respuestaBoton3);
      if( statusBoton3 == 200){
        Serial.println("Sonido guardado.");
      }  else   {
        Serial.println("No se ha podido registrar el sonido.");
      }
      }

void cerrarSesion(){
  Serial.println("Cerrando sesion...");
  String finalSesion = "";
  //Creo Json de sesión
  const int size_t_capacity = JSON_OBJECT_SIZE(3) + JSON_ARRAY_SIZE(2) + 120;
  DynamicJsonBuffer jsonBuffer(size_t_capacity);
  JsonObject& sesionFin = jsonBuffer.createObject();
  sesionFin["id"] = idSesion;
  time_t tsesionFin = now();
  sesionFin["tiempo_fin"] = tsesionFin;
  char jsonChar[200];
  sesionFin.printTo(jsonChar);
  Serial.println(jsonChar);
  //Insercción de la sesión en la base de datos
  int statusSesion = client.put("/api/UpdateSesion", jsonChar, &finalSesion);
  Serial.println(statusSesion);
  if( statusSesion == 200){
    Serial.println("Sesión finalizada correctamente.");
    contadorSesion--;
  }else {
    Serial.println("No se ha podido cerrar la sesión");
  }
}

void emitirSonido1(){
  long valorPotActual = analogRead(potenciometro);
  if(valorPotActual != valorPot){
    insertarPotenciometro();
    valorPot = valorPotActual;
  }
  double frecuencia = 0;

  if(valorPot < 127.875) {
          frecuencia = 65.406;
  }else if(valorPot < 255.75){
    frecuencia = 130.813;
  }else if(valorPot < 383.625){
      frecuencia = 261.626;
    }else if(valorPot < 511.5){
        frecuencia = 523.251;
      }else if(valorPot < 639.375){
          frecuencia = 1046.502;
        }else if(valorPot < 767.26){
            frecuencia = 2093.005;
          }else if(valorPot < 895.125){
              frecuencia = 4186.009;
                }else if(valorPot < 1024){
                    frecuencia = 8372.018;
                    }
Serial.println(frecuencia);
  tone(altavoz, frecuencia);
  delay(500);
  noTone(altavoz);

}
void emitirSonido2(){
  long valorPotActual = analogRead(potenciometro);
  if(valorPotActual != valorPot){
    insertarPotenciometro();
    valorPot = valorPotActual;
  }
  double frecuencia = 0;

  if(valorPot < 127.875) {
          frecuencia = 82.407;
  }else if(valorPot < 255.75){
    frecuencia = 164.814;
  }else if(valorPot < 383.625){
      frecuencia = 329.628;
    }else if(valorPot < 511.5){
        frecuencia = 659.255;
      }else if(valorPot < 639.375){
          frecuencia = 1318.51;
        }else if(valorPot < 767.26){
            frecuencia = 2637.02;
          }else if(valorPot < 895.125){
              frecuencia = 5274.041;
                }else if(valorPot < 1024){
                    frecuencia = 10548.082;
                    }
Serial.println(frecuencia);
  tone(altavoz, frecuencia);
  delay(500);
  noTone(altavoz);

}
void emitirSonido3(){
  long valorPotActual = analogRead(potenciometro);
  if(valorPotActual != valorPot){
    insertarPotenciometro();
    valorPot = valorPotActual;
  }
  double frecuencia = 0;

  if(valorPot < 127.875) {
          frecuencia = 97.999;
  }else if(valorPot < 255.75){
    frecuencia = 195.998;
  }else if(valorPot < 383.625){
      frecuencia = 391.995;
    }else if(valorPot < 511.5){
        frecuencia = 783.991;
      }else if(valorPot < 639.375){
          frecuencia = 1567.982;
        }else if(valorPot < 767.26){
            frecuencia = 3135.963;
          }else if(valorPot < 895.125){
              frecuencia = 6271.927;
                }else if(valorPot < 1024){
                    frecuencia = 12543.854;
                    }
Serial.println(frecuencia);
  tone(altavoz, frecuencia);
  delay(500);
  noTone(altavoz);

}
