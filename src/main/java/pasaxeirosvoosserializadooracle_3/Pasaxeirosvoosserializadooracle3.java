package pasaxeirosvoosserializadooracle_3;

import java.io.*;
import java.sql.*;

public class Pasaxeirosvoosserializadooracle3 implements Serializable {
    String driver = "jdbc:postgresql:";
    String host = "//localhost:";
    String porto = "5432";
    String sid = "postgres";
    String usuario = "dam2a";
    String password = "castelao";
    String url = driver + host + porto + "/" + sid;
    Connection conn;

    {
        try {
            conn = DriverManager.getConnection(url, usuario, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Método de conexión que devuelve un objeto de tipo Connection para usar con los métodos a base de datos
    public Connection conexion() throws SQLException {

        return conn;
    }

    private static int acumuladorPrecio;

    public void leerReserva(Reserva reserva, File file) throws IOException, SQLException, ClassNotFoundException {
        ObjectInputStream inpS = new ObjectInputStream(new FileInputStream(file));
        PreparedStatement actualizar = conn.prepareStatement("update pasaxeiros set nreservas = nreservas +1 where dni= ?");
        PreparedStatement insertar = conn.prepareStatement("insert into reservasfeitas values(?,?,?,?)");
        Statement consulta = conn.createStatement();
        Statement consulta2 = conn.createStatement();


        while ((reserva = (Reserva) inpS.readObject()) != null) {
            System.out.println(reserva.getCodr() + " " + reserva.getDni() + " " + reserva.getIdvooida() + " " + reserva.getIdvoovolta());
            ResultSet r1 = consulta.executeQuery("select nome from pasaxeiros where dni = '" + reserva.getDni() + "'");
            actualizar.setString(1, reserva.getDni());
            actualizar.execute();
            insertar.setInt(1, reserva.getCodr());
            insertar.setString(2, reserva.getDni());
            while (r1.next()) {
                ResultSet r2 = consulta2.executeQuery("select sum(prezo) from voos where  voo='" + reserva.getIdvooida() + "' or voo='" + reserva.getIdvoovolta() + "'");
                insertar.setString(3, r1.getString(1));
                while (r2.next()) {
                    acumuladorPrecio = r2.getInt(1);
                    insertar.setInt(4, acumuladorPrecio);
                    insertar.execute();
                }
            }
        }

    }

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        Pasaxeirosvoosserializadooracle3 prueba = new Pasaxeirosvoosserializadooracle3();
        Reserva reserva = new Reserva();
        File file = new File("/home/dam2a/IdeaProjects/EjerciciosExamen/src/reservas");
        prueba.leerReserva(reserva,file);
    }
}
