package exa15brevep;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.*;
import java.sql.*;

public class Exa15brevep implements Serializable {

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

    public void leerPlatos(Platos plato, File file, File file2) throws IOException, ClassNotFoundException, SQLException, XMLStreamException {
        XMLOutputFactory out = XMLOutputFactory.newInstance();
        XMLStreamWriter xml = out.createXMLStreamWriter(new FileWriter(file2));
        plato = null;
        ObjectInputStream inpF = new ObjectInputStream(new FileInputStream(file));

        int peso = 0;
        int graxa = 0;
        int graxaParcial = 0;
        int grasaTotal = 0;
        String codC = "";
        Statement consulta = conn.createStatement();

        Statement consulta2 = conn.createStatement();
        xml.writeStartDocument("1.0");
        while ((plato = (Platos) inpF.readObject()) != null) {
            xml.writeStartElement("Platos");
            xml.writeStartElement("Plato");
            xml.writeAttribute("codigo", plato.getCodigop());
            xml.writeStartElement("NomeP");
            xml.writeCharacters(plato.getNomep());
            xml.writeEndElement();

            System.out.println(plato);
            ResultSet r = consulta.executeQuery("SELECT codc,peso from composicion where codp ='" + plato.getCodigop() + "'");
            while (r.next()) {
                codC = r.getString(1);
                peso = r.getInt(2);
                ResultSet r2 = consulta2.executeQuery("SELECT graxa from componentes where codc = '" + codC + "'"); // Segunda consulta en función de el codc que devuelva la primera

                while (r2.next()) {
                    graxa = r2.getInt(1);
                    graxaParcial = (peso / 100) * graxa;
                    grasaTotal += graxaParcial;
                }
            }
            xml.writeStartElement("GraxaTotal");
            System.out.println("Grasa total = " + grasaTotal + "\n");
            xml.writeCharacters(String.valueOf(grasaTotal));
            xml.writeEndElement();
            xml.writeEndElement();
            xml.writeEndElement();
            grasaTotal = 0; // Vuelvo a poner a cero la grasa total para que no sume la primera vuelta
        }

        inpF.close();
        xml.close();

    }

    public static void main(String[] args) throws XMLStreamException, SQLException, IOException, ClassNotFoundException {
        File file = new File("/home/dam2a/IdeaProjects/EjerciciosExamen/src/platoss");
        File file2 = new File("xmlPlatos.xml");
        Exa15brevep prueba = new Exa15brevep();
        Platos plato = new Platos();
        prueba.leerPlatos(plato,file,file2);
    }
}
