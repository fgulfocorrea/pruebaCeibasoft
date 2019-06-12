package dominio;

import dominio.repositorio.RepositorioProducto;

import java.util.Calendar;

import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;

public class Vendedor {

    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
    public static final String EL_PRODUCTO_NO_TIENE_GARANTIA = "El producto no tiene garantia extendida";

    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;

    public Vendedor(RepositorioProducto repositorioProducto, RepositorioGarantiaExtendida repositorioGarantia) {
        this.repositorioProducto = repositorioProducto;
        this.repositorioGarantia = repositorioGarantia;

    }

    public void generarGarantia(String codigo, String nombreCliente) {
    	
		if(codigoProductoConTresVocales(codigo)){
			throw new GarantiaExtendidaException(EL_PRODUCTO_NO_TIENE_GARANTIA);
		}else{
			if(tieneGarantia(codigo)) {
	    		throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
	    	}else {
	    		Producto producto = repositorioProducto.obtenerPorCodigo(codigo);
	    		
	    		//Calculo de fecha finalizaion garantia
	    		Calendar fechaActual = Calendar.getInstance();
	    		
	    		if(fechaActual.getTime().getDay() == 1){
	    			fechaActual.add(Calendar.DATE, 1);
	    		}
	    		
	    		Calendar fecha200dias = Calendar.getInstance();
	    		fecha200dias.add(Calendar.DATE, 200);
	    		
	    		if(fecha200dias.getTime().getDay() == 0){
	    			fecha200dias.add(Calendar.DATE, 201);
	    		}
	    		
	    		Calendar fecha100dias = Calendar.getInstance();
	    		fecha100dias.add(Calendar.DATE, 100);
	    		
	    		if(fecha100dias.getTime().getDay() == 0){
	    			fecha100dias.add(Calendar.DATE, 101);
	    		}
	    		
	    		//precio limite de producto
	    		Double precioLimite = 500000d;
	    		
	    		/*Si el precio del producto supera los 500.000, 
	    		  el precio de la garantia es el 20% del costo del producto
	    		  y la fecha fin garantia son 200 dias. De lo contrario
	    		  el precio de la garantia seria 10% y la fecha fin 100 dias.
	    		*/
	    		if(precioLimite.compareTo(producto.getPrecio())<0){
	    			GarantiaExtendida garantia = new GarantiaExtendida(producto, fechaActual.getTime(), fecha200dias.getTime(), producto.getPrecio()*0.2, nombreCliente);
	    			repositorioGarantia.agregar(garantia);
	    		}else{
	    			GarantiaExtendida garantia = new GarantiaExtendida(producto, fechaActual.getTime(), fecha100dias.getTime(), producto.getPrecio()*0.1, nombreCliente);
	    			repositorioGarantia.agregar(garantia);
	    		}
	    	}
		}
    }
    
    public boolean codigoProductoConTresVocales(String codigo) {
    	// validacion del codigo del producto si cuando tiene tres vocales
		int contador = 0;
		boolean si = false;
		for(int x=0;x<codigo.length();x++) {
			  if ((codigo.charAt(x)=='a') || (codigo.charAt(x)=='e') || (codigo.charAt(x)=='i') || (codigo.charAt(x)=='o') || (codigo.charAt(x)=='u') || (codigo.charAt(x)=='A') || (codigo.charAt(x)=='E') || (codigo.charAt(x)=='I') || (codigo.charAt(x)=='O') || (codigo.charAt(x)=='U')){
			    contador++;
			  }  
		}
		if(contador == 3){
			si = true;
		}
		
		return si;
    }

    public boolean tieneGarantia(String codigo) {
        if(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo) != null) {
        	return true;
        }else {
        	return false;
        }
    }

}
