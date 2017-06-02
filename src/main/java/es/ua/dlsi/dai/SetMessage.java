package es.ua.dlsi.dai;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.GeoPt;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.repackaged.com.google.gson.JsonArray;
import com.google.appengine.repackaged.com.google.gson.JsonObject;

public class SetMessage extends HttpServlet{
	
	private DatastoreService datastore;

	
	  @Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response) 
	      throws IOException {
	      
		  //response.setContentType("text/plain");
		  response.setContentType("application/json");
		  response.setCharacterEncoding("UTF-8");
			
		  String area_mensaje = request.getParameter("area_mensaje");
		  String contenido = request.getParameter("contenido");
		  String tiempo_vida = request.getParameter("tiempo_vida");
		  String latitud = request.getParameter("latitud");
		  String longitud = request.getParameter("longitud");
		  
		  JsonObject area_mensaje_json = new JsonObject();
		  area_mensaje_json.addProperty("area_mensaje", area_mensaje);
		  
		  JsonObject contenido_json = new JsonObject();
		  contenido_json.addProperty("contenido", contenido);
		  
		  JsonObject tiempo_vida_json = new JsonObject();
		  tiempo_vida_json.addProperty("tiempo_vida", tiempo_vida);
		  
		  JsonObject latitudjson = new JsonObject();
		  latitudjson.addProperty("latitud", latitud);
			 
		  JsonObject longitudjson = new JsonObject();
		  longitudjson.addProperty("longitud", longitud);
			 
		  JsonArray array = new JsonArray();
		  array.add(latitudjson);
		  array.add(longitudjson);
		  JsonObject resultok = new JsonObject();
		  resultok.add("result", array);
			 			 
		  datastore = DatastoreServiceFactory.getDatastoreService();
		  
		  Entity savealerta = new Entity("alerta");
		  savealerta.setProperty("area_mensaje", area_mensaje);
		  savealerta.setProperty("contenido", contenido);
		  savealerta.setProperty("tiempo_vida", tiempo_vida);
		  savealerta.setProperty("latitud", latitud);
		  savealerta.setProperty("longitud", longitud);
		  datastore.put(savealerta);
		  
		  JsonArray arrayvacio = new JsonArray();
		  resultok.add("result", arrayvacio);
			 
		  response.getWriter().write(resultok.toString());	    
	  }

}
