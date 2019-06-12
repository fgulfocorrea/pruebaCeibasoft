package dominio.integracion;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import dominio.Vendedor;
import dominio.Producto;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioProducto;
import dominio.repositorio.RepositorioGarantiaExtendida;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.ProductoTestDataBuilder;

public class VendedorTest {

	private static final String COMPUTADOR_LENOVO = "Computador Lenovo";
	private static final String COMPUTADOR_SONY = "Computador Sony";
	private static final String COMPUTADOR_ASUS = "Computador Asus";
	private static final String TECLADO_HP = "Teclado Hp";
	private static final double PRECIO_SONY = 1500000;
	private static final double PRECIO_ASUS = 1240000;
	private static final double PRECIO_HP = 56000;
	private static final String NOMBRE_CLIENTE_UNO = "Flavio Gulfo";
	private static final String NOMBRE_CLIENTE_DOS = "Raul Gulfo";
	
	private SistemaDePersistencia sistemaPersistencia;
	
	private RepositorioProducto repositorioProducto;
	private RepositorioGarantiaExtendida repositorioGarantia;

	@Before
	public void setUp() {
		
		sistemaPersistencia = new SistemaDePersistencia();
		
		repositorioProducto = sistemaPersistencia.obtenerRepositorioProductos();
		repositorioGarantia = sistemaPersistencia.obtenerRepositorioGarantia();
		
		sistemaPersistencia.iniciar();
	}
	

	@After
	public void tearDown() {
		sistemaPersistencia.terminar();
	}

	@Test
	public void generarGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		repositorioProducto.agregar(producto);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(),NOMBRE_CLIENTE_UNO);
		
		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));
	}
	
	@Test
	public void generarGarantiaVariosProductosTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conCodigo("FG0001").conNombre(COMPUTADOR_SONY).conPrecio(PRECIO_SONY).build();
		Producto producto2 = new ProductoTestDataBuilder().conCodigo("FG1234").conNombre(COMPUTADOR_ASUS).conPrecio(PRECIO_ASUS).build();
		Producto producto3 = new ProductoTestDataBuilder().conCodigo("FG2468").conNombre(TECLADO_HP).conPrecio(PRECIO_HP).build();
		repositorioProducto.agregar(producto);
		repositorioProducto.agregar(producto2);
		repositorioProducto.agregar(producto3);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(),NOMBRE_CLIENTE_UNO);
		vendedor.generarGarantia(producto2.getCodigo(),NOMBRE_CLIENTE_DOS);
		vendedor.generarGarantia(producto3.getCodigo(),NOMBRE_CLIENTE_UNO);
		
		// assert
		Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
		Assert.assertTrue(vendedor.tieneGarantia(producto2.getCodigo()));
		Assert.assertTrue(vendedor.tieneGarantia(producto3.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto2.getCodigo()));
		Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto3.getCodigo()));
		
		/*test de precio de la garantia dependiendo del costo del producto,cuando el costo del producto es mayor a $500.000
		 * el precio de la garantia es del 20% del costo del producto, de lo contrario el precio de la garantia es el 10% del costo del producto
		*/
		Assert.assertEquals(repositorioGarantia.obtener(producto.getCodigo()).getPrecioGarantia(),300000,0);
		Assert.assertEquals(repositorioGarantia.obtener(producto2.getCodigo()).getPrecioGarantia(),248000,0);
		Assert.assertEquals(repositorioGarantia.obtener(producto3.getCodigo()).getPrecioGarantia(),5600,0);
	}
	
	@Test(expected=GarantiaExtendidaException.class)
	public void generarGarantiaValidacionCodigoProductoTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conCodigo("EGA0O01").conNombre(COMPUTADOR_SONY).conPrecio(PRECIO_SONY).build();
		Producto producto2 = new ProductoTestDataBuilder().conCodigo("FGAU1234").conNombre(COMPUTADOR_ASUS).conPrecio(PRECIO_ASUS).build();
	
		repositorioProducto.agregar(producto);
		repositorioProducto.agregar(producto2);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(),NOMBRE_CLIENTE_UNO);
		vendedor.generarGarantia(producto2.getCodigo(),NOMBRE_CLIENTE_DOS);
		
		/*test del codigo del producto cuando contiene 3 vocales, se realiza el test con un producto donde el codigo contiene 3 vocales,
		 * un test con el codigo de producto donde el codigo tiene menos de 3 vocales
		*/
		vendedor.codigoProductoConTresVocales(producto.getCodigo());
		Assert.assertFalse(vendedor.codigoProductoConTresVocales(producto2.getCodigo()));
	}
	
	@Test
	public void generarGarantiaValidacionFechasNombreCliente() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conCodigo("PCT0001").conNombre(COMPUTADOR_SONY).conPrecio(PRECIO_SONY).build();
		Producto producto2 = new ProductoTestDataBuilder().conCodigo("PCT0002").conNombre(TECLADO_HP).conPrecio(PRECIO_HP).build();
	
		repositorioProducto.agregar(producto);
		repositorioProducto.agregar(producto2);
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(),NOMBRE_CLIENTE_UNO);
		vendedor.generarGarantia(producto2.getCodigo(),NOMBRE_CLIENTE_DOS);
		
		//test de nombre del cliente para producto con codigo PCT0001
		Assert.assertEquals(repositorioGarantia.obtener(producto.getCodigo()).getNombreCliente(), "Flavio Gulfo");
		
		//test de nombre del cliente para producto con codigo PCT0002
				Assert.assertEquals(repositorioGarantia.obtener(producto2.getCodigo()).getNombreCliente(), "Raul Gulfo");
		
		//test de las fechas, solicitud de la garantia y fin de la garantia
		Assert.assertNotNull(repositorioGarantia.obtener(producto.getCodigo()).getFechaSolicitudGarantia());
		Assert.assertNotNull(repositorioGarantia.obtener(producto2.getCodigo()).getFechaSolicitudGarantia());
		Assert.assertNotNull(repositorioGarantia.obtener(producto.getCodigo()).getFechaFinGarantia());
		Assert.assertNotNull(repositorioGarantia.obtener(producto2.getCodigo()).getFechaFinGarantia());
	}
	
	@Test
	public void productoYaTieneGarantiaTest() {

		// arrange
		Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();
		
		repositorioProducto.agregar(producto);
		
		Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia);

		// act
		vendedor.generarGarantia(producto.getCodigo(),NOMBRE_CLIENTE_UNO);
		try {
			
			vendedor.generarGarantia(producto.getCodigo(),NOMBRE_CLIENTE_UNO);
			fail();
			
		} catch (GarantiaExtendidaException e) {
			// assert
			Assert.assertEquals(Vendedor.EL_PRODUCTO_TIENE_GARANTIA, e.getMessage());
		}
	}
}
