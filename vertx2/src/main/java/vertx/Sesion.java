package vertx;

public class Sesion {

	private int id;
	private long tiempo_inicio;
	private long tiempo_fin;
	private int maquina;
	private int usuario;
	
	public Sesion() {
		super();
		id = 0;
		tiempo_inicio = 0;
		tiempo_fin = 0;
		maquina = 0;
		usuario = 0;
	}
	
	public Sesion(int id, long tiempo_inicio, long tiempo_fin, int maquina, int usuario) {
		super();
		this.id = id;
		this.tiempo_inicio = tiempo_inicio;
		this.tiempo_fin = tiempo_fin;
		this.maquina = maquina;
		this.usuario = usuario;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getTiempo_inicio() {
		return tiempo_inicio;
	}
	public void setTiempo_inicio(long tiempo_inicio) {
		this.tiempo_inicio = tiempo_inicio;
	}
	public long getTiempo_fin() {
		return tiempo_fin;
	}
	public void setTiempo_fin(long tiempo_fin) {
		this.tiempo_fin = tiempo_fin;
	}
	public int getMaquina() {
		return maquina;
	}
	public void setMaquina(int maquina) {
		this.maquina = maquina;
	}
	public int getUsuario() {
		return usuario;
	}
	public void setUsuario(int usuario) {
		this.usuario = usuario;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + maquina;
		result = prime * result + (int) (tiempo_fin ^ (tiempo_fin >>> 32));
		result = prime * result + (int) (tiempo_inicio ^ (tiempo_inicio >>> 32));
		result = prime * result + usuario;
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
		Sesion other = (Sesion) obj;
		if (id != other.id)
			return false;
		if (maquina != other.maquina)
			return false;
		if (tiempo_fin != other.tiempo_fin)
			return false;
		if (tiempo_inicio != other.tiempo_inicio)
			return false;
		if (usuario != other.usuario)
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Sesion [id=" + id + ", tiempo_inicio=" + tiempo_inicio + ", tiempo_fin=" + tiempo_fin + ", maquina="
				+ maquina + ", usuario=" + usuario + "]";
	}
	
	
	
	
}
