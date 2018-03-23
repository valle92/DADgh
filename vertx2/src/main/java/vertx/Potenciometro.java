package vertx;



public class Potenciometro {

	private int id;
	private int id_potenciometro;
	private long marca_temporal;
	private int sesion;
	private int valor;
	
	public Potenciometro() {
		super();
		id = 0;
		id_potenciometro = 0;
		marca_temporal = 0;
		sesion = 0;
		valor = 0;
	}
	
	public Potenciometro(int id, int id_potenciometro,
			long marca_temporal,int sesion, int valor) {
		super();
		this.id = id;
		this.id_potenciometro = id_potenciometro;
		this.marca_temporal = marca_temporal;
		this.sesion = sesion;
		this.valor = valor;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId_potenciometro() {
		return id_potenciometro;
	}

	public void setId_potenciometro(int id_potenciometro) {
		this.id_potenciometro = id_potenciometro;
	}

	public long getMarca_temporal() {
		return marca_temporal;
	}

	public void setMarca_temporal(long marca_temporal) {
		this.marca_temporal = marca_temporal;
	}

	public int getSesion() {
		return sesion;
	}

	public void setSesion(int sesion) {
		this.sesion = sesion;
	}

	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + id_potenciometro;
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
		Potenciometro other = (Potenciometro) obj;
		if (id != other.id)
			return false;
		if (id_potenciometro != other.id_potenciometro)
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
		return "Potenciometro [id=" + id + ", id_potenciometro=" + id_potenciometro + ", marca_temporal="
				+ marca_temporal + ", sesion=" + sesion + ", valor=" + valor + "]";
	}

	
	
}
