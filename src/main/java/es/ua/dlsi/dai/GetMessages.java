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

public class GetMessages extends HttpServlet{
	
	private DatastoreService datastore;
	
	private double p_Distancia_punto(double lat1, double long1, double lat2, double long2)
	{
		double pi, radio_tierra, to_radian;
		double latcoord1_rad, latcoord2_rad, longcoord1_rad, longcoord2_rad;
		double phicoordx, lambdacoordy;
        double haversine, distancia;
		
		 pi= Math.PI;
         radio_tierra = 6372800;
         to_radian = pi / 180;
         
         latcoord1_rad = lat1 * to_radian;
         latcoord2_rad = lat2 * to_radian;
         longcoord1_rad = long1 * to_radian;
         longcoord2_rad = long2 * to_radian;

         phicoordx = latcoord2_rad - latcoord1_rad;
         lambdacoordy = longcoord2_rad - longcoord1_rad;
         haversine = Math.sin(phicoordx / 2) * Math.sin(phicoordx / 2) +
                 Math.cos(latcoord1_rad) * Math.cos(latcoord2_rad) *
                         Math.sin(lambdacoordy / 2) * Math.sin(lambdacoordy / 2);
          return distancia = (2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine))) * radio_tierra;
	}
	
	  @Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response) 
	      throws IOException {
	      
		  //response.setContentType("text/plain");
		  response.setContentType("application/json");
		  response.setCharacterEncoding("UTF-8");
			
		  String latitud = request.getParameter("latitud");
		  String longitud = request.getParameter("longitud");
		  
		  JsonObject latitudjson = new JsonObject();
			 latitudjson.addProperty("latitud", latitud);
			 
			 JsonObject longitudjson = new JsonObject();
			 longitudjson.addProperty("longitud", longitud);
			 
		  		JsonArray array = new JsonArray();
		  		array.add(latitudjson);
		  		array.add(longitudjson);
			 JsonObject resultok = new JsonObject();
			 resultok.add("result", array);
			 
			 //response.getWriter().write(resultok.toString());
			 
			 datastore = DatastoreServiceFactory.getDatastoreService();
			 
			Query busqueda = new Query("alerta");		
			List<Entity> results = datastore.prepare(busqueda).asList(FetchOptions.Builder.withDefaults());
			JsonArray arrayalerts = new JsonArray();
			System.out.println(results.toString());
			
			for(int i=0; i < results.size(); i++)
			{
				Entity jsonalert = results.get(i);
				GeoPt coordenada = (GeoPt) jsonalert.getProperty("coordenada_origen");
				if(coordenada != null)
				{
					double latitud_mensaje, longitud_mensaje, distancia;
					latitud_mensaje = coordenada.getLatitude();
					longitud_mensaje = coordenada.getLongitude();
					
					distancia = p_Distancia_punto(latitud_mensaje, longitud_mensaje, Double.parseDouble(latitud), Double.parseDouble(longitud));
					
					if((long) distancia <= (long) jsonalert.getProperty("area_mensaje"))
					{						
						JsonObject mensaje_json = new JsonObject();
						
						String mensaje = (String) jsonalert.getProperty("contenido").toString();
						String nombre = (String) jsonalert.getKey().toString();
						String area_mensaje = (String) jsonalert.getProperty("area_mensaje").toString();
						String coordenada_origen = (String) jsonalert.getProperty("coordenada_origen").toString();
						String tiempo_creacion = (String) jsonalert.getProperty("tiempo_creacion").toString();
						String tiempo_vida = (String) jsonalert.getProperty("tiempo_vida").toString();
						
						mensaje_json.addProperty("nombre", nombre);
						mensaje_json.addProperty("mensaje", mensaje);
						mensaje_json.addProperty("area_mensaje", area_mensaje);
						mensaje_json.addProperty("coordenada_origen", coordenada_origen);
						mensaje_json.addProperty("tiempo_creacion", tiempo_creacion);
						mensaje_json.addProperty("tiempo_vida", tiempo_vida);
												
						arrayalerts.add(mensaje_json);
					}
				
				}		
			}
			response.getWriter().println(arrayalerts);
		  //response.getWriter().println("latitud:" + latitud + "\n" + "longitud:" + longitud );
	    
	  }

}
