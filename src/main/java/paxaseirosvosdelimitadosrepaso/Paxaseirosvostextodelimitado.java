package paxaseirosvosdelimitadosrepaso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class Paxaseirosvostextodelimitado {
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
    public void desconectar() {
        conn = null;
    }


    public void reservas(Reserva p) throws SQLException, IOException {
        Statement act, act2;
        ResultSet set, set2;
        //Reserva p = null;

        act = conn.createStatement();
        act2 = conn.createStatement();

        //leer del archivo y pasarlo al objeto reserva

        String datosGrabdos = null;
        BufferedReader bufeF = new BufferedReader(new FileReader("/home/dam2a/IdeaProjects/EjerciciosExamen/reservas.txt"));

        while ((datosGrabdos = bufeF.readLine()) != null) {
            String[] datosLeidos = datosGrabdos.split(",");
            p.setCodr(Integer.parseInt(datosLeidos[0]));
            p.setDni(datosLeidos[1]);
            p.setIdvooida(Integer.parseInt(datosLeidos[2]));
            p.setIdvoovolta(Integer.parseInt(datosLeidos[3]));

            /*System.out.println("C贸digo: " + p.getCodr() + "\n" +
                    "DNI: " + p.getDni() + "\n" +
                    "Ida: " + p.getIdvooida() + "\n" +
                    "Volta: " + p.getIdvoovolta());
*/

            int codr = 0;
            String dni = null;
            String nome = null;
            int prezoReserva = 0;


            codr = p.getCodr();
            dni = p.getDni();
            int voovol = p.getIdvooida();
            int vooid = p.getIdvoovolta();

            System.out.println("CODIGO RESERVA: " + codr
                    + "\nDNI: " + dni);
            //actualizamos la tabla
            PreparedStatement act1 = conn.prepareStatement("UPDATE pasaxeiros set nreservas = nreservas + 1 where dni ='" + dni + "'");
            act1.execute();

            //con el dni sacado del objeto reservas buscamos en la base de datos
            set = act.executeQuery("select nome from pasaxeiros where dni='" + dni + "'");
            while (set.next()) {
                System.out.print("Nome: " + set.getString("NOME"));
                nome = set.getString("NOME");

            }
            set2 = act2.executeQuery("select sum(prezo) from voos where voo='" + vooid + "' or voo='" + voovol + "'");
            while (set2.next()) {
                prezoReserva = set2.getInt(1);
                System.out.println("\nPrecio: " + prezoReserva);

            }
            PreparedStatement insertarReservas = conn.prepareStatement("insert into reservasfeitas values (?,?,?,?)");
            insertarReservas.setInt(1, codr);
            insertarReservas.setString(2, dni);
            insertarReservas.setString(3, nome);
            insertarReservas.setInt(4, prezoReserva);
            insertarReservas.execute();

            //si no pones esto al final se repite el primero en bucle infitino
        }
        bufeF.close();


        desconectar();
    }

    public static void main(String[] args) throws SQLException, IOException {
        Paxaseirosvostextodelimitado prueba = new Paxaseirosvostextodelimitado();
        Reserva reserva = new Reserva();
        prueba.reservas(reserva);
    }
}
