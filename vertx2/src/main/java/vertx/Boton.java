package vertx;

public class Boton {

	private int id;
	private int id_boton;
	private long marca_temporal;
	private int valor;
	private int sesion;
	
	
	public Boton() {
		super();
		id = 0;
		id_boton = 0;
		marca_temporal = 0;
		valor = 0;
		sesion = 0;
	}
	
	public Boton(int id, int id_boton, long marca_temporal, int valor, int sesion) {
		super();
		this.id = id;
		this.id_boton = id_boton;
		this.marca_temporal = marca_temporal;
		this.valor = valor;
		this.sesion = sesion;
	}



	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public int getId_boton() {
		return id_boton;
	}



	public void setId_boton(int id_boton) {
		this.id_boton = id_boton;
	}



	public long getMarca_temporal() {
		return marca_temporal;
	}



	public void setMarca_temporal(long marca_temporal) {
		this.marca_temporal = marca_temporal;
	}



	public int getValor() {
		return valor;
	}



	public void setValor(int valor) {
		this.valor = valor;
	}



	public int getSesion() {
		return sesion;
	}



	public void setSesion(int sesion) {
		this.sesion = sesion;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + id_boton;
		result = prime * result + (int) (marca_temporal ^ (marca_temporal >>> 32));
		result = prime * result + sesion;
		result = prime * result + valor;
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Boton other = (Boton) obj;
		if (id != other.id)
			return false;
		if (id_boton != other.id_boton)
			return false;
		if (marca_temporal != other.marca_temporal)
			return false;
		if (sesion != other.sesion)
			return false;
		if (valor != other.valor)
			return false;
		return true;
	}



	@Override
	public String toString() {
		return "Boton [id=" + id + ", id_boton=" + id_boton + ", marca_temporal=" + marca_temporal + ", valor=" + valor
				+ ", sesion=" + sesion + "]";
	}
	
	
	
	
	
	
	
}
